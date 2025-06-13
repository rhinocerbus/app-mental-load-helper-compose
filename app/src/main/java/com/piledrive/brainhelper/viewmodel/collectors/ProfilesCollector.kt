package com.piledrive.brainhelper.viewmodel.collectors

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

class ProfilesCollector(
	coroutineScope: CoroutineScope,
	selfProfileSourceFlow: Flow<Profile?>,
	profilesSourceFlow: Flow<List<Profile>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val selfSource = watchSelf(selfProfileSourceFlow)
			val profilesSource = watchProfiles(profilesSourceFlow)

			merge(selfSource, profilesSource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var selfContent: Profile? = null
	private val _selfContentFlow = MutableStateFlow(selfContent)
	val selfContentFlow: StateFlow<Profile?> = _selfContentFlow

	private fun watchSelf(source: Flow<Profile?>): Flow<Unit> {
		return source.mapLatest {
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			selfContent = it
			withContext(Dispatchers.Main) {
				_selfContentFlow.value = selfContent
			}
		}
	}

	private var profilesContent: List<Profile> = listOf()
	private val _profilesContentFlow = MutableStateFlow(profilesContent)
	val profilesContentFlow: StateFlow<List<Profile>> = _profilesContentFlow

	private fun watchProfiles(source: Flow<List<Profile>>): Flow<Unit> {
		return source.mapLatest {
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

	private suspend fun recompileData() {
	}

	/////////////////////////////////////////////////
	//  endregion
}