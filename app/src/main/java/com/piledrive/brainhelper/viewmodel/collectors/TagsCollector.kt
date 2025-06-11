package com.piledrive.brainhelper.viewmodel.collectors

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.brainhelper.data.model.composite.FullTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class TagsCollector(
	coroutineScope: CoroutineScope,
	tagsSourceFlow: Flow<List<Tag>>,
	profilesSourceFlow: Flow<List<Profile>>
) {

	init {
		coroutineScope.launch(Dispatchers.Default) {
			val tagsSource = watchTags(tagsSourceFlow)
			val profilesSource = watchProfiles(profilesSourceFlow)
			merge(tagsSource, profilesSource)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}

	//  region Raw data model inputs
	/////////////////////////////////////////////////

	private var tagsContent: List<Tag> = listOf()
	private var profilesContent: List<Profile> = listOf()

	private fun watchTags(source: Flow<List<Tag>>): Flow<Unit> {
		return source.mapLatest {
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			tagsContent = it
		}
	}


	private fun watchProfiles(source: Flow<List<Profile>>): Flow<Unit> {
		return source.mapLatest {
			/*itemsContent = itemsContent.copy(
				data = itemsContent.data.copy(items = it)
			)*/
			profilesContent = it
		}
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Composite data outputs
	/////////////////////////////////////////////////

	private val _tagsContentFlow = MutableStateFlow<List<FullTag>>(listOf())
	val tagsContentFlow: StateFlow<List<FullTag>> = _tagsContentFlow

	private suspend fun recompileData() {
		val fullTags = tagsContent.map { tag ->
			val profile = profilesContent.firstOrNull { profile ->
				tag.profileId == profile.id
			}
			FullTag(tag, profile)
		}
		_tagsContentFlow.value = fullTags
	}

	/////////////////////////////////////////////////
	//  endregion
}