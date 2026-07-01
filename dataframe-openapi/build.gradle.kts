plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
    }
    with(libs.plugins) {
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
