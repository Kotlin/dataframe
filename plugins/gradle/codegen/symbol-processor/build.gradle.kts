plugins {
    kotlin("jvm") version "1.5.21"
    id("com.vanniktech.maven.publish") version "0.17.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ksp.api)
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.3")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.3")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
}
