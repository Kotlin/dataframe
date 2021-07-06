package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

class SchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("schemaGenerator")
        val defaultSrc = target.file("src/gen/kotlin")
        registerGeneratedSources(target, defaultSrc)

        target.afterEvaluate {
            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach {
                val task = target.tasks.create("generate${it.interfaceName}", GenerateDataSchemaTask::class.java) {
                    src.convention(defaultSrc)
                    data.set(it.data)
                    interfaceName.set(it.interfaceName)
                    packageName.set(it.packageName)
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

    private fun registerGeneratedSources(target: Project, directory: File) {
        val kotlinExtension = target.extensions.getByType<KotlinJvmProjectExtension>()
        val sourceSet = kotlinExtension.sourceSets.getByName("main")
        sourceSet.kotlin.srcDir(directory)
    }
}
