plugins {
    kotlin("jvm") version "1.5.21"
    id("com.vanniktech.maven.publish") version "0.17.0"
}

repositories {
    mavenCentral()
    // TODO: Remove when kotlin-compile-testing update its KSP dependency to 1.5.21.1.0.0-beta07+
    // since this version is published on the mavenCentral
    google()
}

dependencies {
    implementation(libs.ksp.api)
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.2")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.2")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
}
