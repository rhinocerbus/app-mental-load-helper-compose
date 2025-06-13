package com.piledrive.brainhelper.repo.datasource.powersync

import android.content.ContentValues
import com.piledrive.brainhelper.data.model.Scratch
import com.piledrive.brainhelper.data.model.ScratchSlug
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
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
) : CrudPowerSyncDataSource<Scratch, ScratchSlug> {

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

	override suspend fun addNewData(slug: ScratchSlug) {
		val values = ContentValues().apply {
			put("family_id", slug.familyId)
			put("content", slug.content)
		}
		powerSync.insert("scratch", values, Scratch::class)
	}

	override suspend fun updateData(data: Scratch) {
		val values = ContentValues().apply {
			put("content", data.content)
		}
		powerSync.update("scratch", values, whereValue = data.id, clazz = Scratch::class)
	}
}
