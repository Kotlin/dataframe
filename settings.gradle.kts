rootProject.name = "dataframe"

// Enables our build-settings logic convention plugins for the root project,
// setting up all common logic and version- and convention catalogs.
pluginManagement {
    includeBuild("./build-settings-logic")
}
plugins {
    id("dfsettings.catalogs")
}

// Enables our build logic convention plugins for the root project,
// so they can be applied in child projects in their build.gradle.kts files.
includeBuild("./build-logic")

includeBuild("plugins/keywords-generator")

include("plugins:expressions-converter")
include("plugins:public-api-modifier")
include("samples")
include("dataframe-json")
include("dataframe-arrow")
include("dataframe-openapi")
include("dataframe-excel")
include("dataframe-jdbc")
include("dataframe-csv")
include("dataframe-jupyter")
include("dataframe-geo")
include("dataframe-geo-jupyter")
include("dataframe-openapi-generator")
include("core")
include("dataframe-compiler-plugin-core")

include("examples:idea-examples:titanic")
include("examples:idea-examples:movies")
include("examples:idea-examples:youtube")
include("examples:idea-examples:json")
include("examples:idea-examples:unsupported-data-sources:exposed")
include("examples:idea-examples:unsupported-data-sources:hibernate")
include("examples:idea-examples:unsupported-data-sources:spark")
include("examples:idea-examples:unsupported-data-sources:multik")
include("examples:idea-examples:spark-parquet-dataframe")
includeBuild("examples/kotlin-dataframe-plugin-gradle-example")
includeBuild("examples/android-example")
