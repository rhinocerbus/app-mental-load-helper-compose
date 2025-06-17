package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Scratch
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
					notesContent = it
				},
				activeNoteIdSourceFlow.mapLatest {
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
		_noteDetailsContentFlow.value = notesContent.firstOrNull { it.note.id == activeNoteId }
	}

	/////////////////////////////////////////////////
	//  endregion
}