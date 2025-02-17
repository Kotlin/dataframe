import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.BaseKotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(jupyter.api)
        alias(kover)
        alias(ktlint)
        alias(buildconfig)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project
repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(jupyterApiTCRepo)
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(projects.core)
    implementation(projects.dataframeCodegen)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.scriptingJvm)
    testImplementation(libs.jsoup)
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.Integration")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
    explicitApi()
}
tasks.withType<KotlinCompile>().configureEach {
    val friendModules = listOf(
        projects.core,
        projects.dataframeCodegen,
    )
    val paths = friendModules.map { project(it.path).projectDir }
    friendPaths.from(paths)
}

kotlinPublications {
    publication {
        publicationName = "dataframeJupyter"
        artifactId = project.name
        description = "Dataframe Kotlin Jupyter support"
        packageName = artifactId
    }
}
