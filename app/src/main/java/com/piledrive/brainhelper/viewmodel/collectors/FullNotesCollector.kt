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

class FullNotesCollector(
	coroutineScope: CoroutineScope,
	fullNotesSourceFlow: Flow<List<FullNote>>,
) {

	private var notesContent: List<FullNote> = listOf()

	init {
		coroutineScope.launch(Dispatchers.Default) {
			merge(
				fullNotesSourceFlow.mapLatest {
					Timber.d("full notes received: $it")
					notesContent = it
				},
			)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private val _fullNotesContentFlow: MutableStateFlow<List<FullNote>> = MutableStateFlow(listOf())
	val fullNotesContentFlow: StateFlow<List<FullNote>> = _fullNotesContentFlow

	private suspend fun recompileData() {
		Timber.d("> recompiling full notes notes state")
		_fullNotesContentFlow.value = notesContent
	}

	/////////////////////////////////////////////////
	//  endregion
}