plugins {
	// core android
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.google.ksp)
	// DI
	alias(libs.plugins.hilt.android)

	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
	namespace = "com.piledrive.brainhelper"
	compileSdk = 35

	buildFeatures.buildConfig = true

	defaultConfig {
		applicationId = "com.piledrive.brainhelper"
		minSdk = 27
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

secrets {
	// To add your Maps API key to this project:
	// 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
	// 2. Add this line, where YOUR_API_KEY is your API key:
	//        MAPS_API_KEY=YOUR_API_KEY
	propertiesFileName = "secrets.properties"

	// A properties file containing default secret values. This file can be
	// checked in version control.
	defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
	// composite build config
	// internal libraries (no version necessary)
	implementation(libs.lib.compose.components)
	implementation(libs.lib.supabase.powersync)
	implementation(libs.lib.datastore)

	// android/androidx/compose
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.ui.graphics.android)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.compose.livedata)
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.navigation.compose)
	debugImplementation(libs.ui.tooling)

	// DI
	implementation(libs.hilt)
	ksp(libs.hilt.compiler)
	implementation(libs.hilt.navigation)

	// logging
	implementation(libs.timber)

	// testing
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
}
