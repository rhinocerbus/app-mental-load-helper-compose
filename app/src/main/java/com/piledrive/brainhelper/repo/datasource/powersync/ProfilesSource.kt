package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.datastore.SessionDataStore
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesSource @Inject constructor(
	private val dataStore: SessionDataStore,
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

	fun watchSelfProfile(): Flow<Profile?> {
		return callbackFlow {
			val userId = dataStore.checkUserId() ?: kotlin.run {
				send(null)
				close(IllegalStateException("MISSING USER ID"))
			}

			powerSync.db.watch(
				"SELECT * FROM profiles WHERE id = '$userId'", mapper = { cursor ->
					Profile(
						id = cursor.getString("id"),
						firstName = cursor.getString("first_name"),
						lastName = cursor.getString("last_name"),
						color = cursor.getString("color"),
					)
				}
			).collect {
				trySend(it.firstOrNull())
			}
		}
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
		).map {
			Timber.d("Profiles received: $it")
			it
		}
	}
}
