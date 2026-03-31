plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.dataframe)
    alias(libs.plugins.ktlint.gradle)

    application
}

repositories {
    mavenCentral()
}

application.mainClass = "org.jetbrains.kotlinx.dataframe.examples.movies.MoviesWithDataClassKt"

dependencies {
    // Add general `dataframe` dependency
    implementation(libs.dataframe)
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
