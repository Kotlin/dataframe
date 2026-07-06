@file:Suppress("UnstableApiUsage")

import dfbuild.keywordsGenerator.KeywordsGeneratorTask

plugins {
    alias(conventions.plugins.dfbuild.kotlinJvmCommon)
}

// Generates enums with restricted Kotlin keywords for the module this plugin is applied to (e.g. `:core`).
// Expects a Kotlin JVM project (uses the `main` source set and `compileKotlin` task).

// from https://kotlinlang.org/docs/whatsnew21.html#compiler-symbols-hidden-from-the-kotlin-gradle-plugin-api
val dependencyScopeConfiguration = configurations.dependencyScope("keywordsGeneratorDependencyScope").get()
dependencies.add(
    dependencyScopeConfiguration.name,
    "org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.asProvider().get()}",
)

val resolvableConfiguration = configurations.resolvable("keywordGeneratorResolvable") {
    extendsFrom(dependencyScopeConfiguration)
}.get()

val genSrcDir = layout.buildDirectory.asFile.get().resolve("generatedSrc")

val mainSourceSet = sourceSets.main.get()
mainSourceSet.java.setSrcDirs(mainSourceSet.java.srcDirs + genSrcDir)

val genTask = tasks.register<KeywordsGeneratorTask>(KeywordsGeneratorTask.NAME) {
    kotlinCompiler.from(resolvableConfiguration)
    srcDir = genSrcDir
}

tasks.compileKotlin {
    dependsOn(genTask)
}
