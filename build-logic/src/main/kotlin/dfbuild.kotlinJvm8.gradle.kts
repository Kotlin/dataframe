import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(conventions.plugins.dfbuild.kotlinJvmCommon)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        freeCompilerArgs.add("-Xjdk-release=8")
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
    options.release.set(8)
}

// Main code targets JVM 1.8, but the test sources target 11 (for Kotest 6.0+).
tasks.named<KotlinCompile>("compileTestKotlin") {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
        freeCompilerArgs.add("-Xjdk-release=11")
    }
}

tasks.named<JavaCompile>("compileTestJava") {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
    options.release.set(11)
}
