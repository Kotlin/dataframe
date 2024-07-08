plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(serialization)
        alias(kover)
        alias(kotlinter)
        alias(jupyter.api)
    }
}

group = "org.jetbrains.kotlinx"

val jupyterApiTCRepo: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven(jupyterApiTCRepo)
}

dependencies {
    api(project(":core"))

    implementation(libs.kotlinLogging)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinpoet)
    api(libs.swagger) {
        // Fix for Android
        exclude("jakarta.validation")
    }

    testApi(project(":core"))
    testImplementation(project(":dataframe-jupyter"))
    testImplementation(libs.junit)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlinPublications {
    publication {
        publicationName.set("dataframeOpenApi")
        artifactId.set(project.name)
        description.set("OpenAPI support for Kotlin Dataframe")
        packageName.set(artifactId)
    }
}

kotlin {
    explicitApi()
}

// Only for tests. Because tests depend on kotlin-jupyter, and it's compatible with Java 11
tasks.compileTestJava {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "11"
    }
}
