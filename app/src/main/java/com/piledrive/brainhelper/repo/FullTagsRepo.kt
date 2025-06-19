package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.brainhelper.data.model.composite.FullNote
import com.piledrive.brainhelper.data.model.composite.FullTag
import com.piledrive.brainhelper.repo.abstracts.BaseRemoteRepo
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import com.piledrive.brainhelper.repo.datasource.powersync.TagsSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CompositeDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class FullTagsRepo @Inject constructor(
	val scope: CoroutineScope,
	private val source: TagsSource,
	private val profilesSource: ProfilesSource
) : BaseRemoteRepo(scope), BasicPowerSyncDataSource<FullTag>, CompositeDataSource<FullTag> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow
	override fun initStateFlow(): StateFlow<Int> = source.initStateFlow

	private var tagsContent: List<Tag> = listOf()
	private var profilesContent: List<Profile> = listOf()

	init {
		initWatch()
	}

	override suspend fun setupSources() {
		scope.launch(Dispatchers.Default) {
			merge(
				source.watchContent().mapLatest {
					tagsContent = it
				},
				profilesSource.watchContent().mapLatest {
					profilesContent = it
				}
			)
				.debounce(500)
				.collect {
					recompileData()
				}
		}
	}


	//  region basic data source overrides
	/////////////////////////////////////////////////

	override fun watchContent(): Flow<List<FullTag>> {
		return outputContentFlow
	}

	/////////////////////////////////////////////////
	//  endregion


	//  region Composite data source overrides
	/////////////////////////////////////////////////

	private val _tagsContentFlow = MutableStateFlow<List<FullTag>>(listOf())
	override val outputContentFlow: StateFlow<List<FullTag>> = _tagsContentFlow

	override suspend fun recompileData() {
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