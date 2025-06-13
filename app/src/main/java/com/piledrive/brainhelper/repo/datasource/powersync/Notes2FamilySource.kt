package com.piledrive.brainhelper.repo.datasource.powersync

import android.content.ContentValues
import com.piledrive.brainhelper.data.model.Note2Family
import com.piledrive.brainhelper.data.model.Note2FamilySlug
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.getString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notes2FamilySource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : CrudPowerSyncDataSource<Note2Family, Note2FamilySlug> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Note2Family>> {
		return powerSync.db.watch(
			"SELECT * FROM notes_to_family", mapper = { cursor ->
				Note2Family(
					id = cursor.getString("id"),
					familyId = cursor.getString("family_id"),
					noteId = cursor.getString("note_id"),
				)
			}
		).map {
			Timber.d("Notes2Family received: $it")
			it
		}
	}

	override suspend fun addNewData(slug: Note2FamilySlug) {
		val values = ContentValues().apply {
			put("family_id", slug.familyId)
			put("note_id", slug.noteId)
		}
		powerSync.insert("notes_to_family", values, Note2Family::class)
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Update unsupported for table")
	override suspend fun updateData(data: Note2Family) {
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Delete unsupported for table")
	override suspend fun deleteData(data: Note2Family) {
	}
}
