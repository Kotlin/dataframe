@file:Suppress("UnstableApiUsage")

rootProject.name = "dataframe"
enableFeaturePreview("VERSION_CATALOGS")

includeBuild("generator")
include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("tests")
include("dataframe-arrow")
include("dataframe-openapi")

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
        maven(url = "https://jitpack.io")
    }

    resolutionStrategy {
        eachPlugin {
            requested.apply {
                val jitpackPlugins = listOf(
                    "com.github.jolanrensen.docProcessorGradlePlugin",
                )
                if ("$id" in jitpackPlugins) {
                    val (_, _, user, name) = "$id".split(".", limit = 4)
                    useModule("com.github.$user:$name:$version")
                }
            }
        }
    }
}
include("dataframe-excel")
include("core")
