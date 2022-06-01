@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"
enableFeaturePreview("VERSION_CATALOGS")

includeBuild("generator")
include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("tests")
include("dataframe-arrow")

include("examples:idea-examples:titanic")
include("examples:idea-examples:movies")
// TODO: replace url in ImportDataSchema with a sample of response from youtube api
// include("examples:idea-examples:youtube")

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
include("dataframe-spreadsheets")
