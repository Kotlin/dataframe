import dfbuild.buildExampleProjects.BuildSystem
import dfbuild.buildExampleProjects.detectBuildSystem
import dfbuild.buildExampleProjects.setupGradleBuildTask
import dfbuild.buildExampleProjects.setupGradleSyncVersionsTask
import dfbuild.buildExampleProjects.setupMavenBuildTask
import dfbuild.buildExampleProjects.setupMavenSyncVersionsTask
import dfbuild.toCamelCaseByDelimiters

plugins {
    alias(convention.plugins.kotlinJvmCommon)
}

val versionsToSync =
    listOf(
        "kotlin",
        "dataframe",
        "kandy",
        "ktlint-gradle",
        "ktlint",
    )

val syncExampleFolders by tasks.registering {
    group = "build"
    description = "Sync the versions in the nested Gradle build in /examples/projects"
}
val buildExampleFolders by tasks.registering {
    group = "verification"
    description = "Builds the nested Gradle build in /examples/projects to verify they compile correctly."
}

val promoteExamples by tasks.registering {
    group = "publishing"
    description = "Promotes the /examples/projects/dev example projects to /examples/projects"

    val projectsFolder = file("examples/projects")

    doLast {
        // Deletes existing projects before promotion
        projectsFolder.listFiles()?.forEach {
            if (!it.isDirectory || it.name == "dev") return@forEach
            it.deleteRecursively()
        }
        logger.lifecycle("Removed example projects from /examples/projects")
        projectsFolder.resolve("dev").listFiles()?.forEach {
            if (!it.isDirectory) return@forEach
            it.copyRecursively(projectsFolder.resolve(it.name))
        }
        logger.lifecycle("Copied example projects from /examples/projects/dev to examples/projects")
    }
    finalizedBy(syncExampleFolders)
}

tasks.named("assemble") {
    dependsOn(syncExampleFolders)
}
tasks.named("check") {
//    // only builds the examples when debug mode is enabled
//    if (project.properties["kotlin.dataframe.debug"].toString() == "true") {
    dependsOn(buildExampleFolders)
//    }
}

/**
 * Sets up example folder with sync and build tasks
 */
private fun setupExampleProjectFolder(folder: File, isDev: Boolean) {
    val name = folder.name.toCamelCaseByDelimiters().replaceFirstChar { it.uppercase() } +
        (if (isDev) "Dev" else "")

    val buildSystem = folder.detectBuildSystem()
        ?: error(
            "Could not detect build system in example project folder '$folder'. We only support ${BuildSystem.entries.toList()}.",
        )

    val libs = versionCatalogs.named("libs")

    val syncTask =
        when (buildSystem) {
            BuildSystem.GRADLE ->
                setupGradleSyncVersionsTask(
                    name = name,
                    folder = folder,
                    isDev = isDev,
                    versionCatalog = libs,
                    versionsToSync = versionsToSync,
                )

            BuildSystem.MAVEN ->
                setupMavenSyncVersionsTask(
                    name = name,
                    folder = folder,
                    isDev = isDev,
                    versionCatalog = libs,
                    versionsToSync = versionsToSync,
                )
        }
    syncExampleFolders {
        dependsOn(syncTask)
    }

    val buildTask =
        when (buildSystem) {
            BuildSystem.GRADLE ->
                setupGradleBuildTask(name = name, folder = folder)

            BuildSystem.MAVEN ->
                setupMavenBuildTask(name = name, folder = folder)
        }
    buildTask {
        dependsOn(syncTask)
        val requiresPublishToMavenLocal = buildSystem == BuildSystem.MAVEN
        if (requiresPublishToMavenLocal) {
            dependsOn(":publishToMavenLocal")
        }
    }
    buildExampleFolders {
        dependsOn(buildTask)
    }
}

file("examples/projects").listFiles()?.forEach {
    if (!it.isDirectory || it.name == "dev") return@forEach
    setupExampleProjectFolder(folder = it, isDev = false)
}
file("examples/projects/dev").listFiles()?.forEach {
    if (!it.isDirectory) return@forEach
    setupExampleProjectFolder(folder = it, isDev = true)
}
