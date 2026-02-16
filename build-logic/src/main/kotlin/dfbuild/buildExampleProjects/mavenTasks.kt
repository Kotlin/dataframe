package dfbuild.buildExampleProjects

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * Registers task to sync and overwrite versions and settings for the example project.
 *
 * This includes:
 * - libs.versions.toml -> pom.xml: <properties>
 * - .editorconfig
 *
 * @param isDev if true, the DataFrame version in pom.xml will be substituted by the root project's version.
 *   To test a Maven dev build successfully, make sure `publishToMavenLocal` runs before this task.
 */
internal fun Project.setupMavenSyncVersionsTask(
    name: String,
    folder: File,
    isDev: Boolean,
    versionCatalog: VersionCatalog,
    versionsToSync: List<String>,
): TaskProvider<Task> =
    tasks.register("sync$name") {
        group = "build"
        description = "Sync the versions in the nested Maven build in ./${folder.name}"

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
            // overwrite versions in pom.xml <properties>
            val pomFile = folder.resolve("pom.xml")
            val versionRegex = "( +)<([a-zA-Z0-9-]+)\\.version>[^<]+</([a-zA-Z0-9-]+).version>".toRegex()
            val newLibsVersionsTomlContent = pomFile.readText().lines().joinToString("\n") {
                val match = versionRegex.matchEntire(it) ?: return@joinToString it
                if (match.groupValues[2] != match.groupValues[3]) return@joinToString it
                val name = match.groupValues[2]
                if (name !in versions) return@joinToString it
                val indent = match.groupValues[1]

                """$indent<$name.version>${versions[name]}</$name.version>"""
            }
            pomFile.writeText(newLibsVersionsTomlContent)

            // overwrite .mvn/wrapper/maven-wrapper.properties
            folder.resolve(".mvn/wrapper/maven-wrapper.properties").writeText(
                """
                wrapperVersion=${versions["maven-wrapper"]}
                distributionType=bin
                distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${versions["maven"]}/apache-maven-${versions["maven"]}-bin.zip
                wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/${versions["maven-wrapper"]}/maven-wrapper-${versions["maven-wrapper"]}.jar

                """.trimIndent(),
            )

            // overwrite .editorconfig
            folder.resolve(".editorconfig").writeText(sourceEditorConfig.readText())
        }
    }
