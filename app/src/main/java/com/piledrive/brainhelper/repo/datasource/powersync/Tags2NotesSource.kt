package com.piledrive.brainhelper.repo.datasource.powersync

import android.content.ContentValues
import com.piledrive.brainhelper.data.model.Tags2Notes
import com.piledrive.brainhelper.data.model.Tags2NotesImpl
import com.piledrive.brainhelper.data.model.Tags2NotesSlug
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.powersync.PowerSyncDbWrapper
import com.powersync.db.SqlCursor
import com.powersync.db.getLong
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

	private val mapper: (SqlCursor) -> Tags2Notes = { cursor ->
		Tags2Notes(
			id = cursor.getString("id"),
			noteId = cursor.getString("note_id"),
			tagId = cursor.getString("tag_id"),
			_enabled = cursor.getLong("enabled").toInt()
		)
	}

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Tags2Notes>> {
		return powerSync.db.watch(
			"SELECT * FROM tags_to_notes", mapper = mapper
		).map {
			Timber.d("Tags2Notes received: $it")
			it
		}
	}

	override suspend fun addNewData(slug: Tags2NotesSlug) {
		val values = ContentValues().apply {
			put("note_id", slug.noteId)
			put("tag_id", slug.tagId)
			put("enabled", slug._enabled)
		}
		powerSync.insert(
			"tags_to_notes", values,
			//onConflict = "ON CONFLICT (note_id, tag_id) DO UPDATE SET enabled = EXCLUDED.enabled",
			clazz = Tags2Notes::class
		)
	}

	suspend fun upsertTagsForNote(noteId: String, newData: List<Tags2NotesImpl>) {
		val current = powerSync.select(
			"tags_to_notes",
			whereClause = "note_id = ?",
			whereValue = noteId,
			mapper = mapper,
			clazz = Tags2Notes::class
		)
		val fresh = mutableSetOf<Tags2NotesImpl>()
		val updated = mutableSetOf<Tags2NotesImpl>()

		newData.forEach { new ->
			when {
				current.firstOrNull { it.tagId == new.tagId } == null -> fresh.add(new)
				else -> updated.add(new)
			}
		}

		clearTagsForNote(noteId)

		fresh.forEach {
			when (it) {
				is Tags2NotesSlug -> {
					addNewData(Tags2NotesSlug(it.noteId, it.tagId, _enabled = 1))
				}

				is Tags2Notes -> {
					addNewData(Tags2NotesSlug(it.noteId, it.tagId, _enabled = 1))
				}
			}
		}

		updated.forEach {
			enableTagForNote(it)
			/*
			when (it) {
				is Tags2NotesSlug -> {
					//addNewData(Tags2NotesSlug(it.noteId, it.tagId, _enabled = 1))
				}

				is Tags2Notes -> {
					updateData2(it.copy(_enabled = 1))
				}
			}*/
		}
	}

	override suspend fun updateData(data: Tags2Notes) {
		val values = ContentValues().apply {
			put("enabled", data._enabled)
		}
		powerSync.update("tags_to_notes", values, whereValue = data.id, clazz = Tags2Notes::class)
	}

	suspend fun clearTagsForNote(noteId: String) {
		Timber.d("> Clearing enabled flag for tags relations on note: $noteId")
		val values = ContentValues().apply {
			put("enabled", 0)
		}
		val result =
			powerSync.update("tags_to_notes", values, whereClause = "note_id = ?", whereValue = noteId, Tags2Notes::class)
		Timber.d("<< Tag relations cleared: $result")
	}

	suspend fun enableTagForNote(data: Tags2NotesImpl) {
		val values = ContentValues().apply {
			put("enabled", 1)
		}
		powerSync.update(
			"tags_to_notes",
			values,
			whereClauses = listOf("note_id = ?", "tag_id = ?"),
			whereValues = listOf(data.noteId, data.tagId),
			clazz = Tags2Notes::class
		)
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Delete unsupported for table")
	override suspend fun deleteData(data: Tags2Notes) {
	}
}
