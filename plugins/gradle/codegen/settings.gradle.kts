rootProject.name = "codegen"
enableFeaturePreview("VERSION_CATALOGS")
includeBuild("../../../generator")
includeBuild("../../../") {
    dependencySubstitution {
        substitute(module("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")).using(project(":"))
    }
}
include(":gradle-plugin")
include(":symbol-processor")
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("ksp", "1.5.21-1.0.0-beta07")
            alias("ksp-gradle").to("com.google.devtools.ksp", "symbol-processing-gradle-plugin").versionRef("ksp")
            alias("ksp-api").to("com.google.devtools.ksp", "symbol-processing-api").versionRef("ksp")
        }
    }
}
