package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Scratch
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

class ScratchCollector(
	coroutineScope: CoroutineScope,
	scratchSourceFlow: Flow<List<Scratch>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val scratchSource = watchScratch(scratchSourceFlow)
			merge(scratchSource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var scratchContent: List<Scratch> = listOf()
	private val _scratchContentFlow = MutableStateFlow(scratchContent)
	val scratchContentFlow: StateFlow<List<Scratch>> = _scratchContentFlow

	private fun watchScratch(source: Flow<List<Scratch>>): Flow<Unit> {
		return source.mapLatest {
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			scratchContent = it
			withContext(Dispatchers.Main) {
				_scratchContentFlow.value = scratchContent
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