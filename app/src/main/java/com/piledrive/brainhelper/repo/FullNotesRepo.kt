package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Note2Family
import com.piledrive.brainhelper.data.model.Note2FamilySlug
import com.piledrive.brainhelper.data.model.NoteSlug
import com.piledrive.brainhelper.data.model.Tags2Notes
import com.piledrive.brainhelper.data.model.composite.FullNote
import com.piledrive.brainhelper.data.model.composite.FullNoteSlug
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.repo.abstracts.BaseRemoteRepo
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import com.piledrive.brainhelper.repo.datasource.powersync.Tags2NotesSource
import com.piledrive.brainhelper.util.UUIDv5
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CompositeDataSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject
import kotlin.uuid.Uuid

@ViewModelScoped
class FullNotesRepo @Inject constructor(
	val scope: CoroutineScope,
	private val dataStore: SessionDataStore,
	private val notes2FamilyRepo: Notes2FamilyRepo,
	private val notesRepo: NotesRepo,
	private val fullTagsRepo: FullTagsRepo,
	private val tags2Notes: Tags2NotesSource,
	private val profilesSource: ProfilesSource,
) : BaseRemoteRepo(scope), CrudPowerSyncDataSource<FullNote, FullNoteSlug>, CompositeDataSource<FullNote> {

	override fun initStateFlow(): StateFlow<Int> = notesRepo.initStateFlow
	override val initStateFlow: StateFlow<Int> = notesRepo.initStateFlow

	private val _outputContentFlow = MutableStateFlow<List<FullNote>>(listOf())
	override val outputContentFlow: StateFlow<List<FullNote>> = _outputContentFlow

	init {
		initWatch()
	}

	override suspend fun addNewData(slug: FullNoteSlug) {
		val activeFamily = dataStore.checkActiveFamilyId() ?: run {
			return
		}

		/*
		 HAVE TO insert to the join table first so the content insert doesn't fail
		 was getting rls policy errors because the relation wasn't in the join table, so the post-insert query failed
		 figured it out by looking at api docs and seeing it did a query after the insert, nothing else suggested a problem like taht
		 */
		val noteId = UUIDv5.nameUUIDFromString().toString()
		runBlocking {
			notes2FamilyRepo.addNewData(Note2FamilySlug(familyId = activeFamily, noteId = noteId))
		}
		runBlocking {
			notesRepo.addNewData(NoteSlug(id = noteId, title = slug.title, content = slug.content))
		}
	}

	override suspend fun updateData(data: FullNote) {
		notesRepo.updateData(data.note)
	}

	override suspend fun deleteData(data: FullNote) {
		notesRepo.deleteData(data.note)
	}

	override fun watchContent(): Flow<List<FullNote>> {
		return outputContentFlow
	}

	/*
	 * flow merging:
	 * all require either caching latest manually or recompiling latest into a passthrough model
	 * https://dev.to/vtsen/kotlin-flow-combine-merge-and-zip-4l75
	 * - marge: straight pass-through emit of input flows, no transform
	 * -- dealing with impl issue where the merge output is Unit, requires locally caching on each incoming flow to use in the final collect
	 * -- based on docs, could be due to only expecting one type
	 * - zip & combine: requires compiling an output per change of incoming flow via a transform, cannot defer/delay the transform
	 * -- combine: fires on any incoming flow change, with latest from all inputs
	 * -- zip: similar but seems to miss latest from some inputs
	 * merge seems more performant with being able to delay compilation
	 */

	override suspend fun setupSources() {
		scope.launch(Dispatchers.Default) {
			combine(
				dataStore.watchActiveFamilyId(),
				notes2FamilyRepo.watchContent(),
				notesRepo.watchContent(),
				fullTagsRepo.watchContent(),
				tags2Notes.watchContent(),
			) { famId, notes2Fam, notes, fullTags, tags2Notes ->
				compileData(famId, notes2Fam, notes, fullTags, tags2Notes)
			}
				.mapLatest { it }
				// see delay notes in compileData
				//.debounce(500)
				.collect {
					_outputContentFlow.value = it
				}
		}
	}

	// based on merge, need to pick one :(
	override suspend fun recompileData() {
	}

	private suspend fun compileData(
		famId: String?,
		notes2Fam: List<Note2Family>,
		notes: List<Note>,
		fullTags: List<FullTag>,
		tags2Notes: List<Tags2Notes>
	): List<FullNote> {
		// todo: not in love with the delay, either get over it and let the recompilations stack, make it cancellable, or use merge w/ caching
		delay(500L)
		Timber.d("> recompiling full notes")
		val safeFamId = famId ?: run {
			Timber.d("<< no active family id")
			return listOf<FullNote>()
		}
		val notesIdsForFam = notes2Fam.filter { it.familyId == safeFamId }.map { it.noteId }
		Timber.d(">> note ids for family: $notesIdsForFam")
		val notesForFam = notes.filter { notesIdsForFam.contains(it.id) }
		Timber.d(">> notes for family: $notesForFam")

		val tagsForNotes = tags2Notes.filter { notesIdsForFam.contains(it.noteId) }
		val tags2NotesGrouped = tagsForNotes.groupBy { it.noteId }

		val fullNotes = notesForFam.map { note ->
			val tagIdsForNote = tags2NotesGrouped[note.id]?.map { it.tagId } ?: listOf()
			val tagsForNote = fullTags.filter { tag -> tagIdsForNote.contains(tag.id) }
			FullNote(note = note, tags = tagsForNote)
		}
		Timber.d(">> full notes for family: $fullNotes")
		return fullNotes
	}
}