plugins {
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm11)
    }
}

group = "org.jetbrains.kotlinx"

dependencies {
    api(projects.core)

    api(libs.kotestAssertions) {
        exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
    }
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}
