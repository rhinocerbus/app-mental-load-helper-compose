package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Family
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

class FamiliesCollector(
	coroutineScope: CoroutineScope,
	familiesSourceFlow: Flow<List<Family>>,
	profilesSourceFlow: Flow<List<Profile>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val familiesSource = watchFamilies(familiesSourceFlow)
			val profilesSource = watchProfiles(profilesSourceFlow)

			merge(familiesSource, profilesSource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var familiesContent: List<Family> = listOf()
	private val _familiesContentFlow = MutableStateFlow(familiesContent)
	val familiesContentFlow: StateFlow<List<Family>> = _familiesContentFlow

	private fun watchFamilies(source: Flow<List<Family>>): Flow<Unit> {
		return source.mapLatest {
			Timber.d("Families received: $it")
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			familiesContent = it
			withContext(Dispatchers.Main) {

				_familiesContentFlow.value = familiesContent
			}
		}
	}

	private var profilesContent: List<Profile> = listOf()
	private val _profilesContentFlow = MutableStateFlow(profilesContent)
	val profilesContentFlow: StateFlow<List<Profile>> = _profilesContentFlow

	private fun watchProfiles(source: Flow<List<Profile>>): Flow<Unit> {
		return source.mapLatest {
			Timber.d("Profiles received: $it")
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			profilesContent = it
			withContext(Dispatchers.Main) {
				_profilesContentFlow.value = profilesContent
			}
		}
	}

	/////////////////////////////////////////////////
	//  endregion

	
	//  region Composite data outputs
	/////////////////////////////////////////////////

	data class FullFamiliesContentState(
		val data: List<Family> = listOf()
	) 
	
	private var fullFamiliesContent: FullFamiliesContentState = FullFamiliesContentState()
	private val _fullFamiliesContentFlow = MutableStateFlow<FullFamiliesContentState>(fullFamiliesContent)
	val fullFamiliesContentFlow: StateFlow<FullFamiliesContentState> = _fullFamiliesContentFlow

	// todo - resolve this with powersync queries, relations
	// todo - add optional state for current tag to filter by to keep optimization in mainviewmodel
	private suspend fun recompileData() {
		val updated = fullFamiliesContent.copy(
			data = familiesContent
		)
		withContext(Dispatchers.Main) {
			_fullFamiliesContentFlow.value = updated
		}
	}

	/////////////////////////////////////////////////
	//  endregion
}