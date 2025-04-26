import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(kover)
        alias(ktlint)
        alias(jupyter.api)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(projects.core)
    compileOnly(projects.dataframeJson)

    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)
    testImplementation(projects.core)
    testImplementation(projects.dataframeArrow)
    testImplementation(projects.dataframeCsv)
    testImplementation(projects.dataframeExcel)
    testImplementation(projects.dataframeJdbc)
    testImplementation(projects.dataframeJson)
    // experimental
    testImplementation(projects.dataframeOpenapiGenerator)
    testImplementation(projects.dataframeOpenapi)

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
