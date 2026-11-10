plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
        alias(dokka)
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
        description = "Kotlin DataFrame: OpenAPI support"
        packageName = artifactId
    }
}
