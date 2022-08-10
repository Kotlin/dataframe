plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    id("org.jetbrains.kotlinx.kover")
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(project(":core"))

    implementation(libs.arrow.vector)
    implementation(libs.arrow.format)
    implementation(libs.arrow.memory)
    implementation(libs.commonsCompress)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.datetimeJvm)

    testApi(project(":core"))
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName.set("dataframeArrow")
        artifactId.set(project.name)
        description.set("Apache Arrow support for Kotlin Dataframe")
        packageName.set(artifactId)
    }
}

kotlin {
    explicitApi()
}
