package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class SchemaGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("schemaGenerator")
        target.afterEvaluate {
            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach {
                val task = target.tasks.create("generate${it.interfaceName}", GenerateDataSchemaTask::class.java) {
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
}
