package dfbuild.buildExampleProjects

import dfbuild.findRootDir
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Registers task to sync and overwrite versions and settings for the example project.
 *
 * This includes:
 * - gradle-wrapper.properties
 * - gradle.properties
 * - libs.versions.toml
 * - settings.gradle.kts
 * - .editorconfig
 *
 * @param isDev if true,
 *   the settings.gradle.kts file in the destination will gain `includeBuild("../../../..")`
 *   and thus have its DataFrame dependencies substituted by the root project.
 */
internal fun Project.setupGradleSyncVersionsTask(
    name: String,
    folder: File,
    isDev: Boolean,
    versionCatalog: VersionCatalog,
    versionsToSync: List<String>,
): TaskProvider<Task> =
    tasks.register("sync$name") {
        group = "build"
        description = "Sync the versions in the nested Gradle build in ./${folder.name}"

        outputs.upToDateWhen { false }
        val versions = versionsToSync.associateWith {
            versionCatalog.findVersion(it).get().requiredVersion
        }.toMutableMap()

        val sourceGradleWrapperProperties = file("gradle/wrapper/gradle-wrapper.properties")
        val sourceEditorConfig = file(".editorconfig")

        doLast {
            // overwrite gradle-wrapper.properties
            folder.resolve("gradle/wrapper/gradle-wrapper.properties").writeText(
                sourceGradleWrapperProperties.readText(),
            )

            // overwrite gradle.properties
            val gradleProperties = folder.resolve("gradle.properties")
            val gradlePropertiesContent = gradleProperties
                .readText()
                .lines()
                .toMutableList()

            if ("kotlin.code.style=official" !in gradlePropertiesContent) {
                gradlePropertiesContent.removeAll { it.startsWith("kotlin.code.style=") }
                gradlePropertiesContent.add("kotlin.code.style=official")
            }
            if ("kotlin.incremental=false" !in gradlePropertiesContent) {
                gradlePropertiesContent.removeAll { it.startsWith("kotlin.incremental=") }
                gradlePropertiesContent.add(
                    """
                    # Disabling incremental compilation will no longer be necessary
                    # when https://youtrack.jetbrains.com/issue/KT-66735 is resolved.
                    kotlin.incremental=false
                    """.trimIndent(),
                )
            }

            gradleProperties.writeText(gradlePropertiesContent.joinToString("\n"))

            // overwrite libs.versions.toml
            val libsVersionsToml = folder.resolve("gradle/libs.versions.toml")
            val versionRegex = """([a-zA-Z0-9-]+)\s*=\s*".+"""".toRegex()
            val newLibsVersionsTomlContent = libsVersionsToml.readText().lines().joinToString("\n") {
                val match = versionRegex.matchEntire(it) ?: return@joinToString it
                val name = match.groupValues[1]
                if (name !in versions) return@joinToString it

                """$name = "${versions[name]}""""
            }
            libsVersionsToml.writeText(newLibsVersionsTomlContent)

            // overwrite settings.gradle.kts

            // this can also be done by the --include-build argument,
            // however, writing it in the settings.gradle.kts file makes the IDE aware of the dependency substitution
            val relativePathToRoot = findRootDir().relativeTo(folder).path
            val generatedDevConfig =
                """
                // region generated-config
                
                // substitutes dependencies provided by the root project
                includeBuild("$relativePathToRoot") {
                    dependencySubstitution {
                        substitute(module("com.jetbrains.kotlinx:dataframe-core"))
                            .using(project(":core"))
                    }
                }
                
                // endregion
                
                """.trimIndent()

            val regex = "// region generated-config(\\n|.)*?// endregion".toRegex()
            val settingsGradleKts = folder.resolve("settings.gradle.kts")
            val settingsGradleKtsContent = settingsGradleKts.readText()
            val newSettingsGradleKtsContent =
                when (regex) {
                    in settingsGradleKtsContent if isDev ->
                        settingsGradleKtsContent.replace(regex, generatedDevConfig)

                    !in settingsGradleKtsContent if isDev ->
                        settingsGradleKtsContent + "\n" + generatedDevConfig

                    in settingsGradleKtsContent if !isDev ->
                        settingsGradleKtsContent.replace(regex, "")

                    else ->
                        settingsGradleKtsContent
                }
                    // keep only one newline at the end
                    .dropLastWhile { it == '\n' }.plus('\n')

            settingsGradleKts.writeText(newSettingsGradleKtsContent)

            // overwrite .editorconfig
            folder.resolve(".editorconfig").writeText(sourceEditorConfig.readText())
        }
    }
