package dfbuild.buildExampleProjects

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.TaskProvider
import java.io.File

internal fun Project.setupKotlinToolchainSyncVersionsTask(
    name: String,
    folder: File,
    isDev: Boolean,
    versionCatalog: VersionCatalog,
    versionsToSync: List<String>,
): TaskProvider<Task> =
    tasks.register("sync$name") {
        description = "Sync the versions in the nested Kotlin Toolchain build in ./${folder.name}"

        outputs.upToDateWhen { false }

        val versions = versionsToSync.associateWith {
            versionCatalog.findVersion(it).get().requiredVersion
        }.toMutableMap()

        // override the dataframe version with the published-to-mavenLocal one
        if (isDev && "dataframe" in versionsToSync) {
            versions["dataframe"] = project.version.toString()
        }

        val sourceEditorConfig = file(".editorconfig")

        doLast {
            // TODO

            // overwrite libs.versions.toml
            syncLibsVersionsToml(folder, versions)

            // overwrite .editorconfig
            folder.resolve(".editorconfig").writeText(sourceEditorConfig.readText())
        }
    }
