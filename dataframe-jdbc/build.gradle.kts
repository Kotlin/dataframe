plugins {
    kotlin("jvm")
    kotlin("libs.publisher")
    id("org.jetbrains.kotlinx.kover")
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    implementation(libs.mariadb)
    // https://mvnrepository.com/artifact/ch.vorburger.mariaDB4j/mariaDB4j
    testImplementation(libs.mariadbtesting)
    testImplementation(libs.junit)
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
