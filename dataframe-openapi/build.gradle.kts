plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(ktlint)
        alias(publisher)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)
}

kotlinPublications {
    publication {
        publicationName = "dataframeOpenApi"
        artifactId = project.name
        description = "OpenAPI support for Kotlin DataFrame"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}
