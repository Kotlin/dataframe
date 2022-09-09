pluginManagement {
    includeBuild("../") {

    }
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
        mavenLocal()
        gradlePluginPortal()
    }
}

includeBuild("../") {
    dependencySubstitution {
        substitute(module("org.jetbrains.kotlinx:dataframe")).using(project(":"))
    }
}
