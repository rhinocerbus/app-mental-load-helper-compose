package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfilesRepo @Inject constructor(
	private val profilesSource: ProfilesSource,
) {

	suspend fun initialize(): Flow<Int> {
		return profilesSource.initPowerSync()
	}

	fun watchProfiles(): Flow<List<Profile>> {
		return profilesSource.watchProfiles()
	}

	fun watchSelfProfile(): Flow<Profile?> {
		return profilesSource.watchSelfProfile()
	}
}