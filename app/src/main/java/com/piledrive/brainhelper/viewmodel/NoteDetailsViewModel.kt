package com.piledrive.brainhelper.viewmodel

import androidx.lifecycle.viewModelScope
import com.piledrive.brainhelper.data.model.composite.FullNoteSlug
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.brainhelper.repo.AuthRepo
import com.piledrive.brainhelper.repo.FullNotesRepo
import com.piledrive.brainhelper.repo.FullTagsRepo
import com.piledrive.brainhelper.ui.screens.note_details.NoteDetailsScreenCoordinator
import com.piledrive.brainhelper.viewmodel.abstracts.AuthenticatedViewModel
import com.piledrive.brainhelper.viewmodel.collectors.NoteDetailsCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
	private val dataStore: SessionDataStore,
	private val authRepo: AuthRepo,
	private val notesRepo: FullNotesRepo,
	private val tagsRepo: FullTagsRepo
) : AuthenticatedViewModel(authRepo) {

	override val initStateFlow: StateFlow<Int> = notesRepo.initStateFlow

	init {
		initDataSync()
	}

	override fun initWatches() {
		viewModelScope.launch(Dispatchers.Default) {
			notesRepo.watchContent().collect {

			}
		}
		viewModelScope.launch {
			textInput
				.mapNotNull { it }
				.distinctUntilChanged { old, new -> old == new }
				.debounce(500L)
				.collect {
					//if (scratchCollector.scratchContentFlow.value != null) {
					//}
				}
		}
	}

	private val activeNoteIdStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)
	fun updateActiveNoteId(id: String?) {
		activeNoteIdStateFlow.value = id
	}

	val collector = NoteDetailsCollector(viewModelScope, notesRepo.outputContentFlow, activeNoteIdStateFlow)

	val coordinator = NoteDetailsScreenCoordinator(
		collector.noteDetailsContentFlow,
		tagsRepo.outputContentFlow,
		onSaveNoteState = { fromLifecycle: Boolean, title: String?, content: String, tagIds: List<String> ->
			viewModelScope.launch { writeChanges(fromLifecycle, title, content, tagIds) }
		}
	)

	private suspend fun writeChanges(fromLifecycle: Boolean, title: String?, content: String, tagIds: List<String>) {
		val activeNote = coordinator.activeNoteSourceFlow.value
		if (activeNote != null) {
			val updatedNote = activeNote.note.copy(title = title, content = content)
			val updatedTags = tagsRepo.outputContentFlow.value.filter { tagIds.contains(it.id) }
			val updatedFullNote = activeNote.copy(note = updatedNote, tags = updatedTags)
			if (updatedFullNote == activeNote) return
			notesRepo.upsertData(updatedFullNote, activeNote)
			return
		}

		if (fromLifecycle) {
			if (title.isNullOrBlank() && content.isBlank()) return
			notesRepo.addNewData(FullNoteSlug(title, content, tagIds))
		} else {
			// either this, or catch id from first update and set as active note id, this is easier atm
			return
		}
	}

	private val textInput: MutableStateFlow<String?> = MutableStateFlow(null)
}
