import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("libs.publisher") version libs.versions.libsPublisher
    kotlin("plugin.serialization") version libs.versions.kotlin
    kotlin("plugin.dataframe") version libs.versions.dataframe apply false

    id("org.jetbrains.dokka") version libs.versions.dokka

    id("org.jmailen.kotlinter") version libs.versions.ktlint
    kotlin("jupyter.api") version libs.versions.kotlinJupyter apply false
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
}

tasks.compileKotlin.map { it.sources }.get()

group = "org.jetbrains.kotlinx"

fun detectVersion(): String {
    val buildNumber = rootProject.findProperty("build.number") as String?
    val versionProp = property("version") as String
    return if(buildNumber != null) {
        if (rootProject.findProperty("build.number.detection") == "true") {
            "$versionProp-dev-$buildNumber"
        } else {
            buildNumber
        }
    }
    else if(hasProperty("release")) {
        versionProp
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
            developer("nikitinas", "Anatoly Nikitin", "Anatoly.Nikitin@jetbrains.com")
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
            url = project.file(File(buildDir, "maven")).toURI()
        }
    }
}
