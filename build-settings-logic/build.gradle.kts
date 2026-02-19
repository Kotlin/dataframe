plugins {
    `kotlin-dsl`
}

description = "Conventions to use in settings.gradle.kts scripts"

dependencies {
    implementation(libs.gradlePlugin.gradle.foojayToolchains)
    api(libs.typesafe.conventions)
}