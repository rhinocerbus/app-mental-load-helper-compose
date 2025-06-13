package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Scratch
import com.piledrive.brainhelper.data.model.ScratchSlug
import com.piledrive.brainhelper.repo.datasource.powersync.NotesSource
import com.piledrive.brainhelper.repo.datasource.powersync.ScratchSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.CrudPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScratchRepo @Inject constructor(
	private val source: ScratchSource,
) : CrudPowerSyncDataSource<Scratch, ScratchSlug> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Scratch>> {
		return source.watchContent()
	}

	override suspend fun addNewData(slug: ScratchSlug) {
		return source.addNewData(slug)
	}

	override suspend fun updateData(data: Scratch) {
		return source.updateData(data)
	}
}