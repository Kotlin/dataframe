import dfsettings.getDfRootDir

plugins {
    id("dfsettings.base")
}

/*
 * Base convention plugin for settings.gradle.kts files.
 * This makes sure all Gradle projects of DataFrame use the same version catalog.
 */
dependencyResolutionManagement {
    versionCatalogs {
        if (findByName("libs") == null) {
            create("libs") {
                try {
                    from(
                        files(
                            getDfRootDir().resolve("gradle/libs.versions.toml").absolutePath,
                        ),
                    )
                } catch (e: Exception) {
                    logger.warn(
                        "Could not load version catalog (${getDfRootDir().absolutePath}/gradle/libs.versions.toml) from $settingsDir",
                    )
                }
            }
        }
    }
}
