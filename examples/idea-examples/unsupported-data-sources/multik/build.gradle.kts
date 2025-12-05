plugins {
    application
    kotlin("jvm")

    // uses the 'old' Gradle plugin instead of the compiler plugin for now
    id("org.jetbrains.kotlinx.dataframe")

    // only mandatory if `kotlin.dataframe.add.ksp=false` in gradle.properties
    id("com.google.devtools.ksp")
}

repositories {
    mavenLocal() // in case of local dataframe development
    mavenCentral()
}

dependencies {
    // implementation("org.jetbrains.kotlinx:dataframe:X.Y.Z")
    implementation(project(":"))

    // multik support
    implementation(libs.multik.core)
    implementation(libs.multik.default)
}
