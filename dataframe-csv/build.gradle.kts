plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
        alias(kodex)
    }
    with(libs.plugins) {
        alias(publisher)
        alias(serialization)
        alias(binary.compatibility.validator)
        alias(kotlinx.benchmark)
    }
    idea
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)

    // for reading/writing JSON <-> DataFrame/DataRow in CSV/TSV/Delim
    // can safely be excluded when working without JSON and only writing flat dataframes
    api(projects.dataframeJson)

    // for csv reading
    api(libs.deephavenCsv)
    // for csv writing
    api(libs.commonsCsv)
    implementation(libs.commonsIo)
    implementation(libs.sl4j)
    implementation(libs.kotlinLogging)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlinx.benchmark.runtime)
    testImplementation(libs.junit)
    testImplementation(libs.sl4jsimple)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

benchmark {
    targets {
        register("test")
    }
}

kotlinPublications {
    publication {
        publicationName = "dataframeCsv"
        artifactId = project.name
        description = "CSV support for Kotlin DataFrame"
        packageName = artifactId
    }
}
