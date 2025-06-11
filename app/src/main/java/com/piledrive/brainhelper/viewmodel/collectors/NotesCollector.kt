package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class NotesCollector(
	coroutineScope: CoroutineScope,
	notesSourceFlow: Flow<List<Note>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val notesSource = watchNotes(notesSourceFlow)
			merge(notesSource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var notesContent: List<Note> = listOf()
	private val _notesContentFlow = MutableStateFlow(notesContent)
	val notesContentFlow: StateFlow<List<Note>> = _notesContentFlow

	private fun watchNotes(source: Flow<List<Note>>): Flow<Unit> {
		return source.mapLatest {
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			notesContent = it
			withContext(Dispatchers.Main) {
				_notesContentFlow.value = notesContent
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion

	
	//  region Composite data outputs
	/////////////////////////////////////////////////

	// todo - resolve this with powersync queries, relations
	// todo - add optional state for current tag to filter by to keep optimization in mainviewmodel
	private suspend fun recompileData() {

	}

	/////////////////////////////////////////////////
	//  endregion
}