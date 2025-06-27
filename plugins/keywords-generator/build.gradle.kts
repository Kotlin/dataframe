@file:OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.github.gmazzo.buildconfig") version "5.5.1"
}

val kotlinPoetVersion: String by project

repositories {
    mavenCentral()
}

buildConfig {
    packageName = "org.jetbrains.kotlinx.dataframe"
    className = "BuildConfig"
    buildConfigField("kotlinCompilerVersion", kotlin.compilerVersion.get())
}

dependencies {
    compileOnly(kotlin("compiler-embeddable", kotlin.compilerVersion.get()))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
}

kotlin {
    jvmToolchain(21)
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

gradlePlugin {
    plugins {
        create("dependencies") {
            id = "org.jetbrains.dataframe.generator"
            version = "1.0"
            implementationClass = "org.jetbrains.dataframe.keywords.KeywordsGeneratorPlugin"
        }
    }
}
