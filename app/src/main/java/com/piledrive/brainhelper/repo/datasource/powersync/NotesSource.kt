package com.piledrive.brainhelper.repo.datasource.powersync

import android.content.ContentValues
import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.NoteSlug
import com.piledrive.brainhelper.data.model.Scratch
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
class NotesSource @Inject constructor(
	private val powerSync: PowerSyncDbWrapper,
) : CrudPowerSyncDataSource<Note, NoteSlug> {

	override val initStateFlow: StateFlow<Int> = powerSync.initState

	override fun watchContent(): Flow<List<Note>> {
		return powerSync.db.watch(
			"SELECT * FROM notes", mapper = { cursor ->
				Note(
					id = cursor.getString("id"),
					createdAt = cursor.getString("created_at"),
					title = cursor.getStringOptional("title"),
					content = cursor.getString("content"),
				)
			}
		).map {
			Timber.d("Notes received: $it")
			it
		}
	}

	override suspend fun addNewData(slug: NoteSlug) {
		val values = ContentValues().apply {
			put("title", slug.title)
			put("content", slug.content)
		}
		powerSync.insert("scratch", values, Note::class)
	}

	override suspend fun updateData(data: Note) {
		val values = ContentValues().apply {
			// updated_at handled by remote db extension trigger
			put("title", data.title)
			put("content", data.content)
		}
		powerSync.update("notes", values, whereValue = data.id, clazz = Note::class)
	}

	override suspend fun deleteData(data: Note) {
		powerSync.delete("notes", whereValue = data.id, clazz = Note::class)
	}
}
