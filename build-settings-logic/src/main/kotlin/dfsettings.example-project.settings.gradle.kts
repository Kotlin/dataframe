import dfsettings.getDfRootDir

/*
 * This convention plugin can be enabled from example projects to override
 * their dataframe dependencies with one built from the local project.
 *
 * This includes the compiler plugin.
 *
 * If a user wants to use an example project, simply remove this convention plugin
 * from the settings.gradle.kts.
 */

plugins {
    id("dfsettings.base")
//    id("dfsettings.version-catalog")
}

// depend on building the dataframe project
includeBuild(getDfRootDir().absolutePath)
