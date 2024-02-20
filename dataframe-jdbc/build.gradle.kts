plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    id("org.jetbrains.kotlinx.kover")
    kotlin("jupyter.api")
    // TODO enable later: id("org.jmailen.kotlinter")
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
    implementation(libs.kotlinLogging)
    testImplementation(libs.sqlite)
    testImplementation(libs.postgresql)
    testImplementation(libs.mysql)
    testImplementation(libs.h2db)
    testImplementation(libs.vertica)
    testImplementation(libs.junit)
    testImplementation(libs.sl4j)
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
