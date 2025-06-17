plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(kover)
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

    implementation(libs.arrow.vector)
    implementation(libs.arrow.format)
    implementation(libs.arrow.memory)
    implementation(libs.commonsCompress)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.datetimeJvm)

    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.arrow.c.data)
    testImplementation(libs.duckdb.jdbc)

    testImplementation(libs.arrow.driver.jdbc)
    testImplementation(libs.h2db)
    testImplementation(libs.embedded.postgresql)
}

kotlinPublications {
    publication {
        publicationName = "dataframeArrow"
        artifactId = project.name
        description = "Apache Arrow support for Kotlin Dataframe"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}
