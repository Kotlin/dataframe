import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(kover)
        alias(ktlint)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(projects.core)

    implementation(libs.kotlin.stdlib)
    implementation(libs.serialization.core)
    implementation(libs.serialization.json)
    implementation(libs.sl4j)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
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

tasks.test {
    useJUnitPlatform()
}

kotlinPublications {
    publication {
        publicationName = "dataframeJson"
        artifactId = project.name
        description = "Kotlin DataFrame JSON integration"
        packageName = artifactId
    }
}
