plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.publisher)
    alias(libs.plugins.jupyter.api)
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
}

kotlinPublications {
    publication {
        publicationName = "dataframeOpenApi"
        artifactId = project.name
        description = "OpenAPI support for Kotlin Dataframe"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}
