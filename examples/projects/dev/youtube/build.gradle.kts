plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.ktlint.gradle)

    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application.mainClass = "org.jetbrains.kotlinx.dataframe.examples.youtube.YoutubeKt"

dependencies {
    // Add general `dataframe` dependency
    implementation(libs.dataframe)
    implementation(libs.kotlin.datetimeJvm)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}

ktlint {
    version = libs.versions.ktlint.asProvider()
    // rules are set up through .editorconfig
}
