import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
//        alias(kover)
        alias(ktlint)
        alias(jupyter.api)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    // geo repository should come before Maven Central
    maven("https://repo.osgeo.org/repository/release")
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
    mavenCentral()
}

dependencies {
    api(projects.dataframe)

    // logger, need it for apache poi
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)

    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)

    // experimental
    testImplementation(projects.dataframeOpenapiGenerator)
    testImplementation(projects.dataframeOpenapi)

    testImplementation(projects.dataframeJupyter)
    testImplementation(projects.dataframeGeoJupyter)

    testImplementation(libs.kandy) {
        exclude("org.jetbrains.kotlinx", "dataframe")
    }
    testImplementation(libs.kandy.geo) {
        exclude("org.jetbrains.kotlinx", "dataframe")
    }
    testImplementation(libs.kandy.stats) {
        exclude("org.jetbrains.kotlinx", "dataframe")
    }

    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    friendPaths.from(project(projects.core.path).projectDir)
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.Integration")
}

tasks.test {
    maxHeapSize = "2048m"
}

kotlinPublications {
    publication {
        publicationName = "dataframeJupyter"
        artifactId = project.name
        description = "Kotlin DataFrame integration with Kotlin Jupyter"
        packageName = artifactId
    }
}
