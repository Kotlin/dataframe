import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(libs.plugins) {
        alias(kotlin.jvm)
        alias(publisher)
        alias(jupyter.api)
        alias(buildconfig)
        alias(binary.compatibility.validator)
    }
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))

    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)
    testImplementation(project(":core"))
    testImplementation(project(":dataframe-arrow"))
    testImplementation(project(":dataframe-csv"))
    testImplementation(project(":dataframe-excel"))
    testImplementation(project(":dataframe-jdbc"))
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlin {
    explicitApi()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xfriend-paths=${project(":core").projectDir}",
        )
    }
}

tasks.processJupyterApiResources {
    libraryProducers = listOf("org.jetbrains.kotlinx.dataframe.jupyter.Integration")
}

kotlinPublications {
    publication {
        publicationName.set("dataframe-jupyter")
        artifactId.set("dataframe-jupyter")
        description.set("Kotlin DataFrame integration with Kotlin Jupyter")
        packageName.set("dataframe-jupyter")
    }
}
