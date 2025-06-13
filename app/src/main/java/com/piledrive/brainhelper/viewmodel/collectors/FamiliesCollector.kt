package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.state.FamilyContentState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class FamiliesCollector(
	coroutineScope: CoroutineScope,
	familiesSourceFlow: Flow<List<Family>>,
	activeFamilyIdPrefSourceFlow: Flow<String?>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val familiesSource = watchFamilies(familiesSourceFlow)
			val activeFamilySource = watchFamily(activeFamilyIdPrefSourceFlow)

			merge(familiesSource, activeFamilySource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var familiesContent: List<Family> = listOf()
	private fun watchFamilies(source: Flow<List<Family>>): Flow<Unit> {
		return source.mapLatest {
			familiesContent = it
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

	private val _familyContentFlow = MutableStateFlow(FamilyContentState())
	val familyContentFlow: StateFlow<FamilyContentState> = _familyContentFlow

	private suspend fun recompileData() {
		val activeFamily = familiesContent.firstOrNull { it.id == activeFamilyId}
		_familyContentFlow.value = FamilyContentState(familiesContent, activeFamily)
	}

	/////////////////////////////////////////////////
	//  endregion
}