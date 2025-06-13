package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.repo.datasource.powersync.FamiliesSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamiliesRepo @Inject constructor(
	private val source: FamiliesSource,
) : BasicPowerSyncDataSource<Family> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Family>> {
		return source.watchContent()
	}
}