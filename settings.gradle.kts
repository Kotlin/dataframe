@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"

includeBuild("generator")
include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("plugins:expressions-converter")
include("tests")
include("dataframe-arrow")
include("dataframe-openapi")
include("dataframe-excel")
include("core")

include("examples:idea-examples:titanic")
include("examples:idea-examples:movies")
include("examples:idea-examples:youtube")
include("examples:idea-examples:json")

val jupyterApiTCRepo: String by settings

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(jupyterApiTCRepo)
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}


