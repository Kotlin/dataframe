plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(project(":"))

    implementation(libs.arrow.vector)
    implementation(libs.arrow.format)
    implementation(libs.arrow.memory)
    implementation(libs.commonsCompress)

    testApi(project(":"))
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName.set("dataframe-arrow")
        artifactId.set(project.name)
        description.set("Apache Arrow support for Kotlin Dataframe")
        packageName.set(artifactId)
    }
}
