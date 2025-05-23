package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesSource @Inject constructor(
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

	fun watchProfiles(): Flow<List<Profile>> {
		return powerSync.db.watch(
			"SELECT * FROM profiles", mapper = { cursor ->
				Profile(
					id = cursor.getString("id"),
					firstName = cursor.getString("first_name"),
					lastName = cursor.getString("last_name"),
					color = cursor.getString("color"),
				)
			}
		)
	}
}
