import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gmazzo.buildconfig.BuildConfigExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
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
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization) apply false
        alias(dokka)
//        alias(kover)
        alias(ktlint)

        // TODO cannot define korro and kodex here due to leaking them kotlin-compiler-embeddable into the build classpath
        // alias(korro) apply false
        // alias(kodex) apply false

        alias(simpleGit) apply false
        alias(dependencyVersions)
        alias(buildconfig) apply false

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
    if (jupyterApiTCRepo.isNotBlank()) maven(jupyterApiTCRepo)
}

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

//    kover(projects.core)
//    kover(projects.dataframeArrow)
//    kover(projects.dataframeExcel)
//    kover(projects.dataframeOpenapi)
//    kover(projects.dataframeJdbc)
//    kover(projects.dataframeCsv)
//    kover(projects.dataframeJson)
//    kover(projects.plugins.kotlinDataframe)
//    kover(projects.dataframeJupyter)
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

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

// DataFrame targets Java 8 for maximum compatibility.
// This is, however, not always possible thanks to external dependencies.
// In those cases, we default to Java 11.
val modulesUsingJava11 = with(projects) {
    setOf(
        dataframeJupyter,
        dataframeGeoJupyter,
        examples.ideaExamples.titanic,
        examples.ideaExamples.unsupportedDataSources,
        tests,
        plugins.dataframeGradlePlugin,
    )
}.map { it.path }

allprojects {
    if (path in modulesUsingJava11) {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_11
                freeCompilerArgs.add("-Xjdk-release=11")
            }
        }
        tasks.withType<JavaCompile> {
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
            options.release.set(11)
        }
    } else {
        tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget = JvmTarget.JVM_1_8
                freeCompilerArgs.add("-Xjdk-release=8")
            }
        }
        tasks.withType<JavaCompile> {
            sourceCompatibility = JavaVersion.VERSION_1_8.toString()
            targetCompatibility = JavaVersion.VERSION_1_8.toString()
            options.release.set(8)
        }
    }
    tasks.withType<KotlinCompile> {
        compilerOptions {
            // enables support for kotlin.time.Instant as kotlinx.datetime.Instant was deprecated; Issue #1350
            // Can be removed once kotlin.time.Instant is marked "stable".
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }

    // Attempts to configure ktlint for each sub-project that uses the plugin
    afterEvaluate {
        try {
            configure<KtlintExtension> {
                version = "1.6.0"
                // rules are set up through .editorconfig
            }
        } catch (_: UnknownDomainObjectException) {
            logger.warn("Could not set ktlint config on :${this.name}")
        }

        // set the java toolchain version to 21 for all subprojects for CI stability
        extensions.findByType<KotlinJvmProjectExtension>()?.jvmToolchain(21)

        // Attempts to configure buildConfig for each sub-project that uses it
        try {
            configure<BuildConfigExtension> {
                packageName = "org.jetbrains.kotlinx.dataframe"
                className = "BuildConfig"
                buildConfigField("KOTLIN_VERSION", libs.versions.kotlin.asProvider().get())
                buildConfigField("VERSION", "${project.version}")
                buildConfigField("DEBUG", findProperty("kotlin.dataframe.debug")?.toString()?.toBoolean() ?: false)
            }
        } catch (_: UnknownDomainObjectException) {
            logger.warn("Could not set buildConfig on :${this.name}")
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
