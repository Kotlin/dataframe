import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.publisher.apache2
import org.jetbrains.kotlinx.publisher.developer
import org.jetbrains.kotlinx.publisher.githubRepo

@Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")
plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("libs.publisher") version libs.versions.libsPublisher
    kotlin("plugin.serialization") version libs.versions.kotlin
    kotlin("jupyter.api") version libs.versions.kotlinJupyter
    kotlin("plugin.dataframe") version libs.versions.dataframe apply false

    id("org.jetbrains.dokka") version libs.versions.dokka
    id("org.jetbrains.dataframe.generator")

    id("org.jmailen.kotlinter") version libs.versions.ktlint

}

val jupyterApiTCRepo: String by project
val projectName: String by project

repositories {
    mavenCentral()
    maven(jupyterApiTCRepo)
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.reflect)

    api(libs.commonsCsv)
    implementation(libs.klaxon)
    implementation(libs.fuel)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)

    implementation(libs.kotlin.datetimeJvm)
    implementation("com.squareup:kotlinpoet:1.11.0")

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)
}

kotlin {
    explicitApi()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
    }
}

allprojects {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

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

tasks.lintKotlinMain {
    exclude("**/*keywords*/**")
}

tasks.lintKotlinTest {
    enabled = true
}

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
            "filename"
    )
}

val instrumentedJars: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add("instrumentedJars", tasks.jar.get().archiveFile) {
        builtBy(tasks.jar)
    }
}

tasks.test {
    maxHeapSize = "2048m"
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.Integration")
}

