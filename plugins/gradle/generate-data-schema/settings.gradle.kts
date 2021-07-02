rootProject.name = "generate-data-schema"

includeBuild("../../../generator")
includeBuild("../../../") {
    dependencySubstitution {
        substitute(module("org.jetbrains.kotlinx:dataframe:0.7.3-dev-277-0.10.0.53")).using(project(":"))
    }
}
