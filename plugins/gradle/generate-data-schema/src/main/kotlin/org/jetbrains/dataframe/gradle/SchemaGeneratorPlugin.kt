package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

class SchemaGeneratorPlugin : Plugin<Project> {
    private val kotlin = AtomicBoolean(false)

    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("schemaGenerator")
        kotlin.set(KOTLIN_PLUGINS.any { target.plugins.hasPlugin(it) })
        target.afterEvaluate {
            if (!kotlin.get()) {
                target.logger.warn("Schema generator plugin applied, but no Kotlin plugin was found")
            }
            val defaultSrc = registerGeneratedSources(target)
            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach {
                val interfaceName = it.interfaceName ?: fileName(it.data)?.capitalize()
                val task = target.tasks.create("generate${interfaceName}", GenerateDataSchemaTask::class.java) {
                    src.convention(defaultSrc)
                    data.set(it.data)
                    this.interfaceName.set(interfaceName)
                    packageName.set(it.packageName)
                    generateExtensionProperties.set(extension.generateExtensionProperties)
                    src.set(it.src)
                }
                generationTasks.add(task)
            }
            val generateAll = target.tasks.create("generateAll") {
                dependsOn(*generationTasks.toTypedArray())
            }
            tasks.withType<KotlinCompile> {
                dependsOn(generateAll)
            }
        }
    }

    private fun fileName(data: Any?): String? {
        return when (data) {
            is String -> extractFileName(data)
            is URL -> extractFileName(data)
            is File -> extractFileName(data)
            else -> throw IllegalArgumentException("data for schema must be File, URL or String, but was ${data?.javaClass ?: ""}($data)")
        }
    }

    private fun registerGeneratedSources(target: Project): File? {
        return listOf(::defaultJvmSrc, ::defaultMultiplatformSrc)
            .asSequence()
            .map { config -> config(target) }
            .filterNotNull()
            .firstOrNull()
    }

    private fun defaultMultiplatformSrc(project: Project): File? {
        return project.extensions.findByType<KotlinMultiplatformExtension>()?.let {
            it.sourceSets.findByName("jvmMain")?.let { jvmMain ->
                val directory = project.file("src/jvmMain/gen")
                jvmMain.kotlin.srcDir(directory)
                directory
            }
        }
    }

    private fun defaultJvmSrc(project: Project): File? {
        return project.extensions.findByType<KotlinJvmProjectExtension>()?.let {
            it.sourceSets.findByName("main")?.let { main ->
                val directory = project.file("src/main/gen")
                main.kotlin.srcDir(directory)
                directory
            }
        }
    }

    private companion object {
        private val KOTLIN_PLUGINS = listOf(
            "org.jetbrains.kotlin.jvm",
            "org.jetbrains.kotlin.android",
            "org.jetbrains.kotlin.multiplatform"
        )
    }
}
