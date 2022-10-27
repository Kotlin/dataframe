@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"
enableFeaturePreview("VERSION_CATALOGS")

includeBuild("generator")
include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("plugins:type-api")
include("tests")
include("dataframe-arrow")
include("dataframe-openapi")
//include("bridge-generator")

include("examples:idea-examples:titanic")
include("examples:idea-examples:movies")
include("examples:idea-examples:youtube")
include("examples:idea-examples:json")

val jupyterApiTCRepo: String by settings

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven(jupyterApiTCRepo)
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }
}

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        mavenLocal()
        gradlePluginPortal()
    }
}
include("dataframe-excel")
include("core")
//include("plugins:dataframe-introspection")
//findProject(":plugins:dataframe-introspection")?.name = "dataframe-introspection"
//include("type-api")
//includeBuild("plugins/extensible-dataframes")
//findProject(":plugins:extensible-dataframes")?.name = "extensible-dataframes"
