@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-example"
include(":app")

// region generated-config

// substitutes dependencies provided by the root project
includeBuild("../../../..") {
    dependencySubstitution {
        substitute(module("com.jetbrains.kotlinx:dataframe-core"))
            .using(project(":core"))
    }
}

// endregion
