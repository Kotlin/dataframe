pluginManagement {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "kotlin-dataframe-plugin-example"
