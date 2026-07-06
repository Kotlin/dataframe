pluginManagement {
    includeBuild("../build-settings-logic")
}

plugins {
    id("dfsettings.base")
    // version should be kept in sync with `gradle/libs.versions.toml`
    id("dev.panuszewski.typesafe-conventions") version "0.11.1"
}

rootProject.name = "build-logic"
