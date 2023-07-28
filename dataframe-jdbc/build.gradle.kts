plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    id("org.jetbrains.kotlinx.kover")
    kotlin("jupyter.api")
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project

repositories {
    mavenCentral()
    maven(jupyterApiTCRepo)
}

dependencies {
    api(project(":core"))
    implementation(libs.mariadb)
    implementation("io.github.oshai:kotlin-logging:5.0.1")

    testImplementation(libs.h2db)
    testImplementation(libs.junit)
    testImplementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName.set("dataframeJDBC")
        artifactId.set(project.name)
        description.set("JDBC support for Kotlin Dataframe")
        packageName.set(artifactId)
    }
}

kotlin {
    explicitApi()
}
