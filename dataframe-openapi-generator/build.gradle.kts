import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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

val jupyterApiTCRepo: String by project

repositories {
    mavenLocal()
    mavenCentral()
    if (jupyterApiTCRepo.isNotBlank()) maven(jupyterApiTCRepo)
}

dependencies {
    api(projects.core)
    api(projects.dataframeOpenapi)

    implementation(libs.sl4j)
    implementation(libs.kotlinLogging)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinpoet)
    api(libs.swagger) {
        // Fix for Android
        exclude("jakarta.validation")
    }

    testApi(projects.dataframeJupyter)
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    testImplementation(libs.kotlin.jupyter.test.kit)
}

kotlinPublications {
    publication {
        publicationName = "dataframeOpenApi"
        artifactId = project.name
        description = "OpenAPI code generation support for Kotlin DataFrame"
        packageName = artifactId
    }
}

kotlin {
    explicitApi()
}

// uses jupyter for testing, so requires java 11 for that
tasks.compileTestKotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}
tasks.compileTestJava {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.release.set(11)
}
