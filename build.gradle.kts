import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
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
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization) apply false
        alias(jupyter.api) apply false
        alias(dokka)
        alias(kover)
        alias(kotlinter)
        alias(korro) apply false
        alias(docProcessor) apply false
        alias(simpleGit) apply false
        alias(dependencyVersions)

        // dependence on our own plugin
        alias(dataframe) apply false
        alias(ksp) apply false
    }
}

val jupyterApiTCRepo: String by project
val projectName: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(jupyterApiTCRepo)
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

dependencies {
    api(project(":core"))
    api(project(":dataframe-arrow"))
    api(project(":dataframe-excel"))
    api(project(":dataframe-openapi"))
    api(project(":dataframe-jdbc"))
}

enum class Version : Comparable<Version> {
    SNAPSHOT, DEV, ALPHA, BETA, RC, STABLE;
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
    // TODO Requires more work to be updated to 1.7.0+, https://github.com/Kotlin/dataframe/issues/594
    libs.plugins.kover.get().pluginId,
    // TODO Updating requires major changes all across the project, https://github.com/Kotlin/dataframe/issues/364
    libs.plugins.kotlinter.get().pluginId,
    // TODO 5.8.0 is not possible due to https://github.com/Kotlin/dataframe/issues/595
    libs.kotestAssertions.get().name,
    // Can't be updated to 7.4.0+ due to Java 8 compatibility
    libs.android.gradle.api.get().group,
    // TODO 0.1.6 breaks ktlint, https://github.com/Kotlin/dataframe/issues/598
    libs.plugins.korro.get().pluginId,
    // Directly dependent on the Gradle version
    "org.gradle.kotlin.kotlin-dsl",
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
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    // Attempts to configure kotlinter for each sub-project that uses the plugin
    afterEvaluate {
        try {
            kotlinter {
                ignoreFailures = false
                reporters = arrayOf("checkstyle", "plain")
                experimentalRules = true
                disabledRules = arrayOf(
                    "no-wildcard-imports",
                    "experimental:spacing-between-declarations-with-annotations",
                    "experimental:enum-entry-name-case",
                    "experimental:argument-list-wrapping",
                    "experimental:annotation",
                    "max-line-length",
                    "filename",
                    "comment-spacing",
                    "curly-spacing",
                    "experimental:annotation-spacing",
                    "no-unused-imports", // broken
                )
            }
        } catch (_: UnknownDomainObjectException) {
            logger.warn("Could not set kotlinter config on :${this.name}")
        }
    }
}

koverMerged {
    enable()
    filters {
        projects {
            excludes += listOf(
                ":examples:idea-examples:youtube",
                ":examples:idea-examples:titanic",
                ":examples:idea-examples:movies",
                ":examples:idea-examples",
                ":examples",
                ":plugins",
                ":plugins:dataframe-gradle-plugin",
                ":plugins:symbol-processor",
                ":plugins:dataframe-gradle-plugin",
            )
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
    fairDokkaJars.set(false)

    sonatypeSettings(
        project.findProperty("kds.sonatype.user") as String?,
        project.findProperty("kds.sonatype.password") as String?,
        "dataframe project, v. ${project.version}"
    )

    signingCredentials(
        project.findProperty("kds.sign.key.id") as String?,
        project.findProperty("kds.sign.key.private") as String?,
        project.findProperty("kds.sign.key.passphrase") as String?
    )

    pom {
        githubRepo("Kotlin", "dataframe")
        inceptionYear.set("2021")
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
        publicationName.set("api")
        artifactId.set(projectName)
        description.set("Data processing in Kotlin")
        packageName.set(artifactId)
    }

    localRepositories {
        maven {
            url = project.file(layout.buildDirectory.dir("maven")).toURI()
        }
    }
}
