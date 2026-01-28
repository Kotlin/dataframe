rootProject.name = "dataframe"

pluginManagement {
    includeBuild("./build-settings-logic")
}

plugins {
    id("dfsettings.catalogs")
}

includeBuild("./build-logic")

// treated as a separate project with its own Kotlin version, etc.
includeBuild("plugins/keywords-generator")

include("plugins:dataframe-gradle-plugin")
include("plugins:symbol-processor")
include("plugins:expressions-converter")
include("plugins:kotlin-dataframe")
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
