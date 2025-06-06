pluginManagement {
	// composite build config
	includeBuild("build-logic") // include build-logic module
	repositories {
		google {
			content {
				includeGroupByRegex("com\\.android.*")
				includeGroupByRegex("com\\.google.*")
				includeGroupByRegex("androidx.*")
			}
		}
		mavenCentral()
		gradlePluginPortal()
	}
}
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		google()
		mavenCentral()
	}
}

rootProject.name = "app-brainhelper"
include(":app")
// composite build config
// list private library git submodules
includeBuild("lib-compose-components")
includeBuild("lib-supabase-powersync")
includeBuild("lib-datastore")
