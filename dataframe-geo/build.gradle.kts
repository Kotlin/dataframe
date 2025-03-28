import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(jupyter.api)
        alias(ktlint)
        alias(dataframe)
        alias(ksp)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    // geo repository should come before Maven Central
    maven("https://repo.osgeo.org/repository/release")
    mavenCentral()
    mavenLocal()
}

// https://stackoverflow.com/questions/26993105/i-get-an-error-downloading-javax-media-jai-core1-1-3-from-maven-central
// jai core dependency should be excluded from geotools dependencies and added separately
fun ExternalModuleDependency.excludeJaiCore() = exclude("javax.media", "jai_core")

dependencies {
    api(projects.core)
    implementation(projects.dataframeJupyter)

    implementation(libs.geotools.main) { excludeJaiCore() }
    implementation(libs.geotools.shapefile) { excludeJaiCore() }
    implementation(libs.geotools.geojson) { excludeJaiCore() }
    implementation(libs.geotools.referencing) { excludeJaiCore() }
    implementation(libs.geotools.epsg.hsql) { excludeJaiCore() }

    implementation(libs.jai.core)

    implementation(libs.jts.core)
    implementation(libs.jts.io.common)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile>().configureEach {
    friendPaths.from(project(projects.core.path).projectDir)
}

kotlinPublications {
    publication {
        publicationName = "dataframeGeo"
        artifactId = "dataframe-geo"
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
