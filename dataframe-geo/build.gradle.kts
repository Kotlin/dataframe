import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
        alias(kodex)
    }
    with(libs.plugins) {
        alias(publisher)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    // osgeo repository should come before Maven Central
    maven(url = "https://repo.osgeo.org/repository/release")
    mavenCentral()
    mavenLocal()
}

kotlin.sourceSets {
    main {
        kotlin.srcDir("src/generated-dataschema-accessors/main/kotlin/")
    }
}

// https://stackoverflow.com/questions/26993105/i-get-an-error-downloading-javax-media-jai-core1-1-3-from-maven-central
// jai core dependency should be excluded from geotools dependencies and added separately
fun ExternalModuleDependency.excludeJaiCore() = exclude("javax.media", "jai_core")

dependencies {
    api(projects.core)

    // Geotools
    // `api` scope for everything whose types appear in our public API, so that consumers can use it
    // without declaring GeoTools themselves (#1920):
    //  - gt-api holds `CoordinateReferenceSystem` (GeoDataFrame.crs, applyCrs, DEFAULT_CRS, AnyFrame.toGeo)
    //  - gt-main holds `ReferencedEnvelope` (bounds) and `SimpleFeatureCollection` (to/from SimpleFeatureCollection)
    //  - gt-referencing holds `CRS`, which is not in our signatures but which consumers need to build a CRS at all
    api(libs.geotools.api) { excludeJaiCore() }
    api(libs.geotools.main) { excludeJaiCore() }
    api(libs.geotools.referencing) { excludeJaiCore() }
    // used internally only; `implementation` still puts them on the consumer's runtime classpath
    implementation(libs.geotools.shapefile) { excludeJaiCore() }
    implementation(libs.geotools.geojson) { excludeJaiCore() }
    implementation(libs.geotools.epsg.hsql) { excludeJaiCore() }

    // JAI
    implementation(libs.jai.core)

    // JTS
    // `Geometry` and friends are the geometry model of WithGeometry and the jts/ helpers
    api(libs.jts.core)
    implementation(libs.jts.io.common)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(kotlin("test"))
    testImplementation(projects.dataframeJson)
}

tasks.withType<KotlinCompile>().configureEach {
    friendPaths.from(project(projects.core.path).projectDir)
}

kotlinPublications {
    publication {
        publicationName = "dataframeGeo"
        artifactId = project.name
        description = "GeoDataFrame API"
        packageName = artifactId
    }
}

tasks.test {
    useJUnitPlatform()
}
