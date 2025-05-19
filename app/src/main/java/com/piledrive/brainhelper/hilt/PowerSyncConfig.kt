package com.piledrive.brainhelper.hilt

import com.piledrive.brainhelper.BuildConfig
import com.piledrive.brainhelper.data.powersync.AppSchema
import com.piledrive.lib_supabase_powersync.hilt.powersync.PowerSyncConfig
import com.powersync.db.schema.Schema
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

object PowerSyncConfig : PowerSyncConfig {
	override val appSchema: Schema = AppSchema
	override val powerSyncUrl: String = BuildConfig.POWERSYNC_URL
}

@Module
@InstallIn(SingletonComponent::class)
object PowerSyncConfigModule {

	@Provides
	fun providePowerSyncConfig(
		// Potential dependencies of this type
	): PowerSyncConfig {
		return PowerSyncConfig
	}
}
