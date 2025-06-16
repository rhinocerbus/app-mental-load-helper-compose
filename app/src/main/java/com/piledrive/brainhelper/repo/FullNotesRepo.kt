package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Note2Family
import com.piledrive.brainhelper.data.model.NoteSlug
import com.piledrive.brainhelper.data.model.composite.FullNote
import com.piledrive.brainhelper.data.model.composite.FullNoteSlug
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.repo.abstracts.BaseRemoteRepo
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
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
import javax.inject.Inject

@ViewModelScoped
class FullNotesRepo @Inject constructor(
	val scope: CoroutineScope,
	private val dataStore: SessionDataStore,
	private val notes2FamilyRepo: Notes2FamilyRepo,
	private val notesRepo: NotesRepo,
	private val fullTagsRepo: FullTagsRepo,
	private val profilesSource: ProfilesSource,
) : BaseRemoteRepo(scope), CrudPowerSyncDataSource<FullNote, FullNoteSlug>, CompositeDataSource<FullNote> {

	override val initStateFlow: StateFlow<Int> = notesRepo.initStateFlow

	private val _outputContentFlow = MutableStateFlow<List<FullNote>>(listOf())
	override val outputContentFlow: StateFlow<List<FullNote>> = _outputContentFlow

	override suspend fun addNewData(slug: FullNoteSlug) {
		notesRepo.addNewData(NoteSlug(updatedAt = slug.updatedAt, title = slug.title, content = slug.content))
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
			) { famId, notes2Fam, notes, fullTags ->
				compileData(famId, notes2Fam, notes, fullTags)
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
		fullTags: List<FullTag>
	): List<FullNote> {
		// todo: not in love with the delay, either get over it and let the recompilations stack, make it cancellable, or use merge w/ caching
		delay(500L)
		val safeFamId = famId?.run {
			return listOf<FullNote>()
		}
		val notesIdsForFam = notes2Fam.filter { it.familyId == safeFamId }.map { it.noteId }
		val notesForFam = notes.filter { notesIdsForFam.contains(it.id) }
		val fullNotes = notesForFam.map {
			FullNote(it, tags = fullTags)
		}
		return fullNotes
	}
}