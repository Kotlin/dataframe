import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.select
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
    }

    with(libs.plugins) {
        alias(publisher)
        alias(serialization) apply false
        alias(dokka)

        // TODO cannot define korro and kodex here due to leaking them kotlin-compiler-embeddable into the build classpath
        // alias(korro) apply false
        // alias(kodex) apply false

        alias(simpleGit) apply false
        alias(dependencyVersions)

        // dependence on our own plugin
        alias(dataframe) apply false
        alias(ksp) apply false
    }
}

val projectName: String by project

configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

dependencies {
    api(projects.core)

    // expose all optional IO dependencies by default
    api(projects.dataframeArrow)
    api(projects.dataframeExcel)
    api(projects.dataframeJdbc)
    api(projects.dataframeCsv)
    api(projects.dataframeJson)

    // experimental, so not included by default:
    // api(projects.dataframeOpenapi)
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

allprojects {
    // Attempts to configure ktlint for each sub-project that uses the plugin
    afterEvaluate {
        // Adds the instrumentedJars configuration/artifact to all sub-projects with a `jar` task
        // This allows other modules to depend on the output of this task, aka the compiled jar of that module
        // Used in :plugins:dataframe-gradle-plugin integration tests and in :samples for compiler plugin support
        try {
            val instrumentedJars: Configuration by configurations.creating {
                isCanBeConsumed = true
                isCanBeResolved = false
            }
            artifacts {
                add("instrumentedJars", tasks.jar.get().archiveFile) {
                    builtBy(tasks.jar)
                }
            }
        } catch (_: Exception) {
            logger.warn("Could not set instrumentedJars on :${this.name}")
        }
    }
}

group = "org.jetbrains.kotlinx"

fun detectVersion(): String {
    val buildNumber = rootProject.findProperty("build.number") as String?
    val versionProp = property("version") as String
    return if (hasProperty("release")) {
        versionProp
    } else if (buildNumber != null) {
        if (rootProject.findProperty("build.number.detection") == "true") {
            "$versionProp-dev-$buildNumber"
        } else {
            error("use build.number + build.number.detection = true or release build")
        }
    } else {
        "$versionProp-dev"
    }
}

val detectVersionForTC by tasks.registering {
    doLast {
        println("##teamcity[buildNumber '$version']")
    }
}

version = detectVersion()
println("Current DataFrame version: $version")

subprojects {
    this.version = rootProject.version
}

kotlinPublications {
    fairDokkaJars = false

    sonatypeSettings(
        project.findProperty("kds.sonatype.central.username") as String?,
        project.findProperty("kds.sonatype.central.password") as String?,
        "dataframe project, v. ${project.version}",
    )

    signingCredentials(
        project.findProperty("kds.sign.key.id") as String?,
        project.findProperty("kds.sign.key.private") as String?,
        project.findProperty("kds.sign.key.passphrase") as String?,
    )

    pom {
        githubRepo("Kotlin", "dataframe")
        inceptionYear = "2021"
        licenses {
            apache2()
        }
        developers {
            developer("koperagen", "Nikita Klimenko", "nikita.klimenko@jetbrains.com")
            developer("Jolanrensen", "Jolan Rensen", "jolan.rensen@jetbrains.com")
            developer("zaleslaw", "Aleksei Zinovev", "aleksei.zinovev@jetbrains.com")
            developer("ermolenkodev", "Nikita Ermolenko", "nikita.ermolenko@jetbrains.com")
            developer("nikitinas", "Anatoly Nikitin", "anatoly.nikitin@jetbrains.com")
        }
    }

    publication {
        publicationName = "api"
        artifactId = projectName
        description = "Data processing in Kotlin"
        packageName = artifactId
    }

    localRepositories {
        maven {
            url = project.file(layout.buildDirectory.dir("maven")).toURI()
        }
    }
}

tasks.assemble {
    // subprojects use the Gradle version from the root project, so let's sync them to ensure standalone version will build as well.
    doLast {
        val source = file("gradle/wrapper/gradle-wrapper.properties")
        listOf("examples/android-example", "examples/kotlin-dataframe-plugin-gradle-example").forEach { sub ->
            val target = file("$sub/gradle/wrapper/gradle-wrapper.properties")
            if (source.readText() != target.readText()) {
                source.copyTo(target, overwrite = true)
            }
        }
    }
}
