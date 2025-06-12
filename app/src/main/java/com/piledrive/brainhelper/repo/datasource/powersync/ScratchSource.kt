package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Scratch
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
class ScratchSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : BasicPowerSyncDataSource<Scratch> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Scratch>> {
		return powerSync.db.watch(
			"SELECT * FROM scratch", mapper = { cursor ->
				Scratch(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					familyId = cursor.getString("family_id"),
					content = cursor.getStringOptional("content"),
				)
			}
		).map {
			Timber.d("Scratch received: $it")
			it
		}
	}
}
