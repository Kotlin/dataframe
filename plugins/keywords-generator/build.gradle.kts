@file:OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    with(conventions.plugins.dfbuild) {
        alias(kotlinJvm8)
        alias(buildConfig)
    }
}

dependencies {
    compileOnly(kotlin("compiler-embeddable", kotlin.compilerVersion.get()))
    implementation(libs.kotlinpoet)
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
