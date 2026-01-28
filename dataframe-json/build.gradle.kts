import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
//        alias(kover)
        alias(ktlint)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)

    implementation(libs.kotlin.stdlib)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.sl4j)

    // Use Kotlin test integration for JUnit 5 to satisfy variant 'kotlin-test-framework-junit5'
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.sl4jsimple)
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    friendPaths.from(project(projects.core.path).projectDir)
}

tasks.withType<Javadoc> {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
    }
}

kotlinPublications {
    publication {
        publicationName = "dataframeJson"
        artifactId = project.name
        description = "Kotlin DataFrame JSON integration"
        packageName = artifactId
    }
}
