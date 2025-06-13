package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Note2Family
import com.piledrive.brainhelper.data.model.Note2FamilySlug
import com.piledrive.brainhelper.repo.datasource.powersync.Notes2FamilySource
import com.piledrive.brainhelper.repo.datasource.powersync.NotesSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notes2FamilyRepo @Inject constructor(
	private val source: Notes2FamilySource,
) : CrudPowerSyncDataSource<Note2Family, Note2FamilySlug> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Note2Family>> {
		return source.watchContent()
	}

	override suspend fun addNewData(slug: Note2FamilySlug) {
		source.addNewData(slug)
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Update unsupported for table")
	override suspend fun updateData(data: Note2Family) {
	}

	@Deprecated(level = DeprecationLevel.ERROR, message = "Delete unsupported for table")
	override suspend fun deleteData(data: Note2Family) {
	}
}