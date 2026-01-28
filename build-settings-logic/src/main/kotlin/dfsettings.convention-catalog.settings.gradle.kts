import dfsettings.findRootDir

plugins {
    id("dfsettings.base")
}

/*
 * Creates a version catalog with build-logic convention plugins.
 * Files like `build-logic/src/main/kotlin/dfbuild.myConventionPlugin.gradle.kts`
 * can be applied as plugin like:
 * ```
 * plugins {
 *     alias(convention.plugins.myConventionPlugin)
 * }
 * ```
 */
dependencyResolutionManagement {
    versionCatalogs {
        // generate type-safe accessors for convention plugins
        create("convention") {
            val buildConventionFiles = findRootDir()
                .resolve("build-logic/src/main/kotlin")
                .listFiles()
                ?: emptyArray()

            for (it in buildConventionFiles) {
                if (!it.isFile || !it.name.endsWith(".gradle.kts")) continue

                val conventionName = it.name.removeSuffix(".gradle.kts")
                val aliasName = conventionName.removePrefix("dfbuild.")
                plugin(aliasName, conventionName).version("")
            }

            // Additional plugins can be added here:
            // plugin("aliasName", "id")
        }
    }
}
