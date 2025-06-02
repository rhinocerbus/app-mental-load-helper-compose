package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.model.Family
import com.piledrive.brainhelper.data.model.Profile
import com.piledrive.brainhelper.repo.datasource.powersync.FamiliesSource
import com.piledrive.brainhelper.repo.datasource.powersync.ProfilesSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamiliesRepo @Inject constructor(
	private val source: FamiliesSource,
) {

	suspend fun initialize(): Flow<Int> {
		return source.initPowerSync()
	}

	fun watchFamilies(): Flow<List<Family>> {
		return source.watchFamilies()
	}
}