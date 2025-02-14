plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(ktlint)
        alias(publisher)
        alias(jupyter.api)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    api(projects.core)
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
