plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish") version "0.17.0"
}

repositories {
    mavenCentral()
    maven(url="https://jitpack.io")
}

dependencies {
//    implementation("org.jetbrains.kotlinx:dataframe:0.8.0-dev")
    implementation(libs.ksp.api)
    testImplementation(kotlin("test"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.3")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.3")
    testImplementation("io.kotest:kotest-assertions-core:4.6.0")
}
