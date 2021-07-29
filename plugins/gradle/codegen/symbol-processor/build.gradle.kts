plugins {
    kotlin("jvm") version "1.5.21"
    id("com.vanniktech.maven.publish")
}

group = "org.jetbrains.dataframe"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.21-1.0.0-beta05")
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.2")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.2")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
}
