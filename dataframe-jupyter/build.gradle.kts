plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.publisher)
    alias(libs.plugins.jupyter.api)
}

group = "org.jetbrains.kotlinx"

repositories {
    mavenCentral()
    maven("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven")
}

dependencies {
    compileOnly(project(":core"))
    compileOnly(libs.jupyter.ktor.client)
    testImplementation(libs.junit)
    testImplementation(libs.serialization.json)
    testImplementation(project(":core"))
    testImplementation(libs.jupyter.ktor.client)
    testImplementation(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
}

kotlin {
    explicitApi()
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xfriend-paths=${project(":core").projectDir}")
        jvmTarget = "11"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        val friendModule = project(":core")
        val jarTask = friendModule.tasks.getByName("jar") as Jar
        val jarPath = jarTask.archiveFile.get().asFile.absolutePath
        freeCompilerArgs += "-Xfriend-paths=$jarPath"
        jvmTarget = "11"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
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
