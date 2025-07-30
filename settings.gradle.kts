@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "dataframe"

// treated as a separate project with its own Kotlin version, etc.
includeBuild("plugins/keywords-generator")

include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("plugins:expressions-converter")
include("plugins:kotlin-dataframe")
include("plugins:public-api-modifier")
include("tests")
include("dataframe-json")
include("dataframe-arrow")
include("dataframe-openapi")
include("dataframe-excel")
include("dataframe-jdbc")
include("dataframe-csv")
include("dataframe-jupyter")
include("dataframe-geo")
include("dataframe-spring")
include("dataframe-openapi-generator")
include("core")
include("dataframe-compiler-plugin-core")

include("examples:idea-examples:titanic")
include("examples:idea-examples:movies")
include("examples:idea-examples:youtube")
include("examples:idea-examples:json")
include("examples:idea-examples:unsupported-data-sources")
// includeBuild("examples/kotlin-dataframe-plugin-example")

val jupyterApiTCRepo: String by settings

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        if (jupyterApiTCRepo.isNotBlank()) maven(jupyterApiTCRepo)
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
