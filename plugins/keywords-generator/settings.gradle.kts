pluginManagement {
//    includeBuild("../../build-logic")
    includeBuild("../../build-settings-logic") {

    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("dfsettings.version-catalog")
}
