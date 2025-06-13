package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.NoteSlug
import com.piledrive.brainhelper.repo.datasource.powersync.NotesSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepo @Inject constructor(
	private val source: NotesSource,
) : CrudPowerSyncDataSource<Note, NoteSlug> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Note>> {
		return source.watchContent()
	}

	override suspend fun addNewData(slug: NoteSlug) {
		source.addNewData(slug)
	}

	override suspend fun updateData(data: Note) {
		source.updateData(data)
	}

	override suspend fun deleteData(data: Note) {
		source.deleteData(data)
	}
}