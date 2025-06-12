package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Note
import com.piledrive.brainhelper.data.model.Scratch
import com.piledrive.brainhelper.repo.datasource.powersync.NotesSource
import com.piledrive.brainhelper.repo.datasource.powersync.ScratchSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScratchRepo @Inject constructor(
	private val source: ScratchSource,
) : BasicPowerSyncDataSource<Scratch> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Scratch>> {
		return source.watchContent()
	}
}