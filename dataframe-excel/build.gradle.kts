plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":"))
    api(libs.poi)
    implementation(libs.poi.ooxml)

    implementation(libs.kotlin.datetimeJvm)

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName.set("dataframeExcel")
        artifactId.set(project.name)
        description.set("Excel support for Kotlin Dataframe")
        packageName.set(artifactId)
    }
}

kotlin {
    explicitApi()
}
