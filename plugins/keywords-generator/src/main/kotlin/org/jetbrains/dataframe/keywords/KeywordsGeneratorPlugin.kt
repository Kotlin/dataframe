package org.jetbrains.dataframe.keywords

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlinx.dataframe.BuildConfig
import java.io.File

abstract class KeywordsGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        // from https://kotlinlang.org/docs/whatsnew21.html#compiler-symbols-hidden-from-the-kotlin-gradle-plugin-api
        val taskDependencyScope = configurations.create("keywordsGeneratorScope")
        dependencies.add(taskDependencyScope.name, "$KOTLIN_COMPILER_EMBEDDABLE:$KOTLIN_COMPILER_VERSION")
        val taskResolvableConfiguration = configurations.create("keywordGeneratorResolvable") {
            extendsFrom(taskDependencyScope)
        }

        val genSrcDir = layout.buildDirectory.asFile.get().resolve("generatedSrc")

        val sourceSets = project.extensions.getByName("sourceSets") as SourceSetContainer
        val mainSourceSet = sourceSets.named("main").get()
        mainSourceSet.addDir(genSrcDir)

        val genTask = tasks.register<KeywordsGeneratorTask>(KeywordsGeneratorTask.NAME) {
            kotlinCompiler.from(taskResolvableConfiguration)
            srcDir = genSrcDir
        }

        tasks["compileKotlin"].dependsOn(genTask)
    }

    private fun SourceSet.addDir(dir: File) {
        java.setSrcDirs(java.srcDirs + dir)
    }

    companion object {
        const val KOTLIN_COMPILER_EMBEDDABLE = "org.jetbrains.kotlin:kotlin-compiler-embeddable"
        const val KOTLIN_COMPILER_VERSION: String = BuildConfig.kotlinCompilerVersion
    }
}
