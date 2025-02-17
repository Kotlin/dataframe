plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(kover)
        alias(ktlint)
        alias(buildconfig)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(projects.core)
    implementation(libs.kotlinpoet)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
    explicitApi()
}

kotlinPublications {
    publication {
        publicationName = "dataframeCodegen"
        artifactId = project.name
        description = "Dataframe Kotlin Jupyter support"
        packageName = artifactId
    }
}
