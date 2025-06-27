plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
//        alias(kover)
        alias(ktlint)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

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
        description = "Excel support for Kotlin Dataframe"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}
