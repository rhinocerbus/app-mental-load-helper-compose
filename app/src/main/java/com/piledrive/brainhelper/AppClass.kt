package com.piledrive.brainhelper

import android.app.Application
import android.content.pm.ApplicationInfo
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppClass : Application() {
	override fun onCreate() {
		super.onCreate()

		initLogging()
		// TODO: Consider using androidx.startup for heavy initialization
	}

	private fun initLogging() {
		if (isDebug()) {
			Timber.plant(Timber.DebugTree())
		}
	}

	private fun isDebug(): Boolean {
		return applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
	}
}