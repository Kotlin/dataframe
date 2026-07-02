rootProject.name = "dataframe"

// Enables our build-settings logic convention plugins for the root project,
// setting up all common logic and version- and convention catalogs.
pluginManagement {
    includeBuild("./build-settings-logic")
}
plugins {
    id("dfsettings.base")
}

// Enables our build logic convention plugins for the root project,
// so they can be applied in child projects in their build.gradle.kts files.
includeBuild("./build-logic")

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
