package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.data.model.Tag
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import com.powersync.db.getStringOptional
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagsSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) {

	fun initPowerSync(): Flow<Int> {
		/*return callbackFlow {
			send(0)
			powerSync.db.waitForFirstSync()
			send(1)
			close()
		}*/
		return powerSync.initState
	}

	fun watchTags(): Flow<List<Tag>> {
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
		)
	}
}
