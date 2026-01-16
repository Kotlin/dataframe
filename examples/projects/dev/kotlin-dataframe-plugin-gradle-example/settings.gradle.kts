pluginManagement {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
        mavenCentral()
        gradlePluginPortal()
    }

    // remove when running as standalone project!
    includeBuild("../../../../build-settings-logic")
}

plugins {
    // remove when running as standalone project!
    id("dfsettings.example-project")

    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kotlin-dataframe-plugin-gradle-example"
