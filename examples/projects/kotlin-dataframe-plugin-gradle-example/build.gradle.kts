import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"

    val kotlinVersion = "2.3.0"
    kotlin("jvm") version kotlinVersion
    // Add the Kotlin DataFrame Compiler plugin of the same version as the Kotlin plugin.
    kotlin("plugin.dataframe") version kotlinVersion

    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Add general `dataframe` dependency
    implementation("org.jetbrains.kotlinx:dataframe:+")
    // Add `kandy` dependency
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

configure<KtlintExtension> {
    version = "1.6.0"
    // rules are set up through .editorconfig
}
