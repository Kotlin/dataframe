plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
}

group = "org.jetbrains.dataframe"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.21-1.0.0-beta05")
}
