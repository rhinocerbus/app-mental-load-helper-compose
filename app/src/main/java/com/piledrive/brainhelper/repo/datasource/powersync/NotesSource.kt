package com.piledrive.brainhelper.repo.datasource.powersync

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : BasicPowerSyncDataSource<Note> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Note>> {
		return powerSync.db.watch(
			"SELECT * FROM notes", mapper = { cursor ->
				Note(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					content = cursor.getString("content"),
				)
			}
		).map {
			Timber.d("Notes received: $it")
			it
		}
	}
}
