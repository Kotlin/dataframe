pluginManagement {
    repositories {
        maven("https://packages.jetbrains.team/maven/p/kt/dev/")
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "kotlin-dataframe-plugin-gradle-example"

// region generated-config

// substitutes dependencies provided by the root project
includeBuild("../../../..")

// endregion
