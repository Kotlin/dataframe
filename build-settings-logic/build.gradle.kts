plugins {
    `kotlin-dsl`
}

description = "Conventions to use in settings.gradle.kts scripts"

dependencies {
    implementation(libs.gradlePlugin.gradle.foojayToolchains)
    implementation(libs.gradlePlugin.gradle.develocity)
    api(libs.typesafe.conventions)
}
