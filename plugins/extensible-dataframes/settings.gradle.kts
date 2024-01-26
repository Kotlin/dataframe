pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    }

}

rootProject.name = "extensible-dataframes"

include("plugin-annotations")

includeBuild("../../") {
    dependencySubstitution {
        substitute(module("org.jetbrains.kotlinx:dataframe")).using(project(":"))
    }
}