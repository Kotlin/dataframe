@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"

includeBuild("generator")
include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("plugins:type-api")
include("plugins:kotlin-dataframe")
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
include("dataframe-excel")
include("core")
//includeBuild("plugins/extensible-dataframes")
//findProject(":plugins:extensible-dataframes")?.name = "extensible-dataframes"
