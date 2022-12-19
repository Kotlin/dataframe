@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"
enableFeaturePreview("VERSION_CATALOGS")

includeBuild("generator")
//include("plugins:dataframe-gradle-plugin")
//include("plugins:symbol-processor")
//include("tests")
include("dataframe-arrow")
include("dataframe-openapi")

//include("examples:idea-examples:titanic")
//include("examples:idea-examples:movies")
//include("examples:idea-examples:youtube")
//include("examples:idea-examples:json")

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
include("dataframe-excel")
include("core")
