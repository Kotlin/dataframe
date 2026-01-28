val jupyterApiTCRepo: String? by settings

dependencyResolutionManagement {

    // allows submodules to override repositories
    // Careful! Once you write `repositories {}`, the ones below are NOT included anymore
    repositoriesMode = RepositoriesMode.PREFER_PROJECT

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        jupyterApiTCRepo?.let { jupyterApiTCRepo ->
            if (jupyterApiTCRepo.isNotBlank()) maven(jupyterApiTCRepo)
        }
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention")
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
