plugins {
    with(convention.plugins) {
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
    api(libs.poi)

    // for writing DataFrame/DataRow -> JSON in Excel cells
    // can safely be excluded when writing only flat dataframes
    api(projects.dataframeJson)

    implementation(libs.poi.ooxml)

    implementation(libs.kotlin.datetimeJvm)

    testImplementation(libs.junit)
    testImplementation(projects.dataframeJson)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName = "dataframeExcel"
        artifactId = project.name
        description = "Excel support for Kotlin DataFrame"
        packageName = artifactId
    }
}
