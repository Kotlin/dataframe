@file:OptIn(ExperimentalBuildToolsApi::class)

import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi

plugins {
    alias(convention.plugins.kotlinJvmCommon)
    alias(libs.plugins.buildconfig)
}

buildConfig {
    packageName = "org.jetbrains.kotlinx.dataframe"
    className = "BuildConfig"
    buildConfigField("KOTLIN_VERSION", libs.versions.kotlin.asProvider().get())
    buildConfigField("KOTLIN_COMPILER_VERSION", kotlin.compilerVersion.get())
    buildConfigField("VERSION", "${project.version}")
    buildConfigField("DEBUG", findProperty("kotlin.dataframe.debug")?.toString()?.toBoolean() ?: false)
}

// combining buildconfig with ktlint
val buildConfigSources by kotlin.sourceSets.creating {
    kotlin.srcDir("build/generated/sources/buildConfig/main")
}
tasks.generateBuildConfig {
    finalizedBy(
        "runKtlintFormatOver${buildConfigSources.name.uppercaseFirstChar()}SourceSet",
    )
}
tasks.named("runKtlintCheckOver${buildConfigSources.name.uppercaseFirstChar()}SourceSet") {
    dependsOn(
        tasks.generateBuildConfig,
        "runKtlintFormatOver${buildConfigSources.name.uppercaseFirstChar()}SourceSet",
    )
}
