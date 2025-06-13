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
	scratchSourceFlow: Flow<List<Scratch>>,
	activeFamilyIdPrefSourceFlow: Flow<String?>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val scratchSource = watchScratch(scratchSourceFlow)
			val activeFamilySource = watchFamily(activeFamilyIdPrefSourceFlow)
			merge(scratchSource, activeFamilySource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var scratchContent: List<Scratch> = listOf()

	private fun watchScratch(source: Flow<List<Scratch>>): Flow<Unit> {
		return source.mapLatest {
			scratchContent = it
		}
	}

	private var activeFamilyId: String? = null
	private fun watchFamily(source: Flow<String?>): Flow<Unit> {
		return source.mapLatest {
			activeFamilyId = it
		}
	}


	/////////////////////////////////////////////////
	//  endregion


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private val _scratchContentFlow: MutableStateFlow<Scratch?> = MutableStateFlow(null)
	val scratchContentFlow: StateFlow<Scratch?> = _scratchContentFlow

	// todo - resolve this with powersync queries, relations
	// todo - add optional state for current tag to filter by to keep optimization in mainviewmodel
	private suspend fun recompileData() {
		_scratchContentFlow.value = scratchContent.firstOrNull { it.familyId == activeFamilyId }
	}

	/////////////////////////////////////////////////
	//  endregion
}