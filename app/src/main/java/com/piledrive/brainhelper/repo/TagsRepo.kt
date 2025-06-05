package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.brainhelper.repo.datasource.powersync.FamiliesSource
import com.piledrive.brainhelper.repo.datasource.powersync.NotesSource
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import com.piledrive.brainhelper.repo.datasource.powersync.TagsSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsRepo @Inject constructor(
	private val source: TagsSource,
) {

	suspend fun initialize(): Flow<Int> {
		return source.initPowerSync()
	}

	fun watchTags(): Flow<List<Tag>> {
		return source.watchTags()
	}
}