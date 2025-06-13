package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.brainhelper.repo.datasource.powersync.TagsSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsRepo @Inject constructor(
	private val source: TagsSource,
) : BasicPowerSyncDataSource<Tag> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Tag>> {
		return source.watchContent()
	}
}