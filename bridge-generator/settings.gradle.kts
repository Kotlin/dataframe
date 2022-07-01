pluginManagement {
    includeBuild("../") {

    }
}

includeBuild("../") {
    dependencySubstitution {
        substitute(module("org.jetbrains.kotlinx:dataframe")).using(project(":"))
    }
}
