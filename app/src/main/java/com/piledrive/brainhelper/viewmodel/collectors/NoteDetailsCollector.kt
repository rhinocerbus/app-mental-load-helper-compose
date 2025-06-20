package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.composite.FullNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import timber.log.Timber

class NoteDetailsCollector(
	coroutineScope: CoroutineScope,
	fullNotesSourceFlow: Flow<List<FullNote>>,
	activeNoteIdSourceFlow: Flow<String?>
) {


	private var notesContent: List<FullNote> = listOf()
	private var activeNoteId: String? = null

	init {
		coroutineScope.launch(Dispatchers.Default) {
			merge(
				fullNotesSourceFlow.mapLatest {
					Timber.d("full notes received: $it")
					notesContent = it
				},
				activeNoteIdSourceFlow.mapLatest {
					Timber.d("active details note id: $it")
					activeNoteId = it
				}
			)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private val _noteDetailsContentFlow: MutableStateFlow<FullNote?> = MutableStateFlow(null)
	val noteDetailsContentFlow: StateFlow<FullNote?> = _noteDetailsContentFlow

	private suspend fun recompileData() {
		Timber.d("> recompiling note details state")
		Timber.d(">> note id: $activeNoteId")
		val activeNote = notesContent.firstOrNull {
			Timber.d(">> ${it.note.id}")
			it.note.id == activeNoteId
		}
		Timber.d("active note: $activeNote")
		_noteDetailsContentFlow.value = activeNote
	}

	/////////////////////////////////////////////////
	//  endregion
}