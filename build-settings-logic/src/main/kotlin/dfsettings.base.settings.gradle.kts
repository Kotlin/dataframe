val jupyterApiTCRepo: String? by settings
dependencyResolutionManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        jupyterApiTCRepo?.let { if (it.isNotBlank()) maven(it) }
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
