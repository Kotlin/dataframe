plugins {
    with(convention.plugins) {
        alias(kotlinJvm8)
    }
    with(libs.plugins) {
        alias(publisher)
//        alias(kover)
        alias(ktlint)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)

    implementation(libs.arrow.vector)
    implementation(libs.arrow.format)
    implementation(libs.arrow.memory)
    implementation(libs.arrow.dataset)
    implementation(libs.commonsCompress)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.datetimeJvm)

    testImplementation(libs.junit)
    testImplementation(projects.dataframeJson)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.arrow.c.data)
    testImplementation(libs.duckdb.jdbc)
}

kotlinPublications {
    publication {
        publicationName = "dataframeArrow"
        artifactId = project.name
        description = "Apache Arrow support for Kotlin DataFrame"
        packageName = artifactId
    }
}

tasks.test {
    jvmArgs = listOf("--add-opens", "java.base/java.nio=ALL-UNNAMED")
}
