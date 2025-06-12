package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import com.powersync.db.getStringOptional
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : BasicPowerSyncDataSource<Tag> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Tag>> {
		return powerSync.db.watch(
			"SELECT * FROM tags", mapper = { cursor ->
				Tag(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					profileId = cursor.getStringOptional("profile_id"),
					label = cursor.getStringOptional("label"),
					color = cursor.getStringOptional("color"),
				)
			}
		).map {
			Timber.d("Profiles received: $it")
			it
		}
	}
}
