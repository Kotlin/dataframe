import org.gradle.buildconfiguration.tasks.UpdateDaemonJvm
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
        alias(buildExampleProjects)
        alias(caupain)
    }
    with(libs.plugins) {
        alias(publisher)
        alias(serialization) apply false
    }
}

// `gradle-jdk` is the single source of truth for both build toolchains and the Gradle daemon.
// To upgrade (for example, to JDK 25), change it in gradle/libs.versions.toml, run
// `./gradlew updateDaemonJvm`, and commit the regenerated gradle-daemon-jvm.properties file.
tasks.named<UpdateDaemonJvm>("updateDaemonJvm") {
    languageVersion = libs.versions.gradle.jdk.map { JavaLanguageVersion.of(it) }
}

val projectName: String = providers.gradleProperty("projectName").get()

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

    dokka(projects.core)
    dokka(projects.dataframeArrow)
    dokka(projects.dataframeExcel)
    dokka(projects.dataframeJdbc)
    dokka(projects.dataframeCsv)
    dokka(projects.dataframeJson)
    dokka(projects.dataframeGeo)
    dokka(projects.dataframeGeoJupyter)
    dokka(projects.dataframeOpenapi)
    dokka(projects.dataframeOpenapiGenerator)
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

val detectVersionForTC = tasks.register("detectVersionForTC") {
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
            // Maven directory for the `:publishLocal` task
            url = project.file(layout.buildDirectory.dir("maven")).toURI()
        }
    }
}
