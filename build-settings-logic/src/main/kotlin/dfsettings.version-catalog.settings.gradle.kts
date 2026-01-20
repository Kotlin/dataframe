import dfsettings.findRootDir

plugins {
    id("dfsettings.base")
}

/*
 * Makes sure all Gradle projects use the same version catalog.
 */
dependencyResolutionManagement {
    versionCatalogs {
        // so we can create a new 'libs' if it already exists
        defaultLibrariesExtensionName = "_default"
        create("libs") {
            try {
                from(
                    files(
                        findRootDir().resolve("gradle/libs.versions.toml").absolutePath,
                    ),
                )
            } catch (e: Exception) {
                logger.warn(
                    "Could not load version catalog (${findRootDir().absolutePath}/gradle/libs.versions.toml) from $settingsDir",
                    e
                )
            }
        }
    }
}
