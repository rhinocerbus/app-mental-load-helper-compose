package com.piledrive.brainhelper.repo.datasource.powersync

import android.content.ContentValues
import com.piledrive.brainhelper.data.model.Tags2Notes
import com.piledrive.brainhelper.data.model.Tags2NotesSlug
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
class Tags2NotesSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : CrudPowerSyncDataSource<Tags2Notes, Tags2NotesSlug> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Tags2Notes>> {
		return powerSync.db.watch(
			"SELECT * FROM tags_to_notes", mapper = { cursor ->
				Tags2Notes(
					id = cursor.getString("id"),
					noteId = cursor.getString("note_id"),
					tagId = cursor.getString("tag_id"),
				)
			}
		).map {
			Timber.d("Tags2Notes received: $it")
			it
		}
	}

	override suspend fun addNewData(slug: Tags2NotesSlug) {
		val values = ContentValues().apply {
			put("note_id", slug.noteId)
			put("tag_id", slug.tagId)
		}
		powerSync.insert("tags_to_notes", values, Tags2Notes::class)
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Update unsupported for table")
	override suspend fun updateData(data: Tags2Notes) {
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Delete unsupported for table")
	override suspend fun deleteData(data: Tags2Notes) {
	}
}
