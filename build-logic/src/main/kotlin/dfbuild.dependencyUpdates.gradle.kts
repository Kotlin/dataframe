import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.api.*

plugins {
    alias(libs.plugins.dependencyVersions)
}

enum class Version : Comparable<Version> {
    SNAPSHOT,
    DEV,
    ALPHA,
    BETA,
    RC,
    STABLE,
}

fun String.findVersion(): Version {
    val version = this.lowercase()
    return when {
        "snapshot" in version -> Version.SNAPSHOT
        "dev" in version -> Version.DEV
        "alpha" in version -> Version.ALPHA
        "beta" in version -> Version.BETA
        "rc" in version -> Version.RC
        else -> Version.STABLE
    }
}

// these names of outdated dependencies will not show up in the table output
val dependencyUpdateExclusions = listOf(
    // Directly dependent on the Gradle version
    "org.gradle.kotlin.kotlin-dsl",
    // need to revise our tests to update
    libs.android.gradle.api.get().group,
    // we have spark3 and spark4 for examples, keep spark3 at 3.3.2
)

// run `./gradlew dependencyUpdates` to check for updates
tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    outputFormatter = "json,html"
    revision = "milestone"

    rejectVersionIf {
        val current = currentVersion.findVersion()
        val candidate = candidate.version.findVersion()
        candidate < current
    }

    doLast {
        val outputFile = layout.buildDirectory
            .file("../$outputDir/$reportfileName.json")
            .get().asFile
        when (val outDatedDependencies = DataFrame.readJson(outputFile)["outdated"]["dependencies"][0]) {
            is AnyFrame -> {
                val df = outDatedDependencies.select {
                    cols("group", "name", "version") and {
                        "available"["milestone"] named "newVersion"
                    }
                }.filter { "name"() !in dependencyUpdateExclusions && "group"() !in dependencyUpdateExclusions }
                logger.warn("Outdated dependencies found:")
                df.print(
                    rowsLimit = Int.MAX_VALUE,
                    valueLimit = Int.MAX_VALUE,
                    borders = true,
                    title = true,
                    alignLeft = true,
                )
            }

            else -> logger.info("No outdated dependencies found")
        }
    }
}
