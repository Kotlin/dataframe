import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm11)
    }
    with(libs.plugins) {
        alias(publisher)
        alias(jupyter.api)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    // geo repository should come before Maven Central
    maven(url = "https://repo.osgeo.org/repository/release")
    mavenCentral()
    mavenLocal()
}

dependencies {
    // deliberately declares no GeoTools/JTS of its own: it resolves `GeoDataFrame.crs` purely through
    // dataframe-geo's `api` scope, so this module fails to compile if those scopes are ever narrowed again (#1920)
    implementation(projects.dataframeGeo)
    implementation(projects.dataframeJupyter)

    // logger, need it for geotools
    implementation(libs.log4j.core)
    implementation(libs.log4j.api)

    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile>().configureEach {
    friendPaths.from(project(projects.core.path).projectDir)
}

kotlinPublications {
    publication {
        publicationName = "dataframeGeoJupyter"
        artifactId = project.name
        description = "GeoDataFrame API"
        packageName = artifactId
    }
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.IntegrationGeo")
}

tasks.test {
    useJUnitPlatform()
}
