package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import com.piledrive.lib_supabase_powersync.data.model.abstracts.datasource.abstracts.BasicPowerSyncDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepo @Inject constructor(
	private val source: ProfilesSource,
) : BasicPowerSyncDataSource<Profile> {

	override val initStateFlow: StateFlow<Int> = source.initStateFlow

	override fun watchContent(): Flow<List<Profile>> {
		return source.watchContent()
	}

	fun watchSelfProfile(): Flow<Profile?> {
		return source.watchSelfProfile()
	}
}