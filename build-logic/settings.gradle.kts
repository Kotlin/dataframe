rootProject.name = "build-logic"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("../build-settings-logic")
}

plugins {
//    id("dev.panuszewski.typesafe-conventions") version "0.10.0"
    id("dfsettings.version-catalog")
}
