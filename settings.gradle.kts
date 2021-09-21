rootProject.name = "dataframe"
enableFeaturePreview("VERSION_CATALOGS")

includeBuild("generator")
include("plugins:gradle:codegen:gradle-plugin")
include("plugins:gradle:codegen:symbol-processor")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("ksp", "1.5.21-1.0.0-beta07")
            alias("ksp-gradle").to("com.google.devtools.ksp", "symbol-processing-gradle-plugin").versionRef("ksp")
            alias("ksp-api").to("com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
        }
    }
}
