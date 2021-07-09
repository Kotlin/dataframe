package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.URL
import java.util.Properties

class SchemaGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("schemaGenerator")
        val properties = Properties()
        properties.load(javaClass.getResourceAsStream("plugin.properties"))
        val dataframeVersion = properties.getProperty("DATAFRAME_VERSION")
        target.afterEvaluate {
            val defaultSrc = registerGeneratedSources(target, dataframeVersion)
            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach {
                val interfaceName = it.interfaceName ?: fileName(it.data)?.capitalize()
                val task = target.tasks.create<GenerateDataSchemaTask>("generate${interfaceName}") {
                    src.convention(defaultSrc)
                    data.set(it.data)
                    this.interfaceName.set(interfaceName)
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

    private fun fileName(data: Any?): String? {
        return when (data) {
            is String -> extractFileName(data)
            is URL -> extractFileName(data)
            is File -> extractFileName(data)
            else -> throw IllegalArgumentException("data for schema must be File, URL or String, but was ${data?.javaClass ?: ""}($data)")
        }
    }

    private fun registerGeneratedSources(target: Project, dataframeVersion: String): File? {
        return sequenceOf(
            SourceSetConfiguration(KotlinJvmProjectExtension::class.java, "main", "src/gen/kotlin", dataframeVersion),
            SourceSetConfiguration(KotlinMultiplatformExtension::class.java, "jvmMain", "src/gen/kotlin", dataframeVersion),
        )
            .map { it.apply(target) }
            .filterNotNull()
            .firstOrNull()
    }

    private class SourceSetConfiguration<T: KotlinProjectExtension>(
        val extensionClass: Class<T>, val name: String, val path: String, val dataframeVersion: String
    ) {
        fun apply(project: Project): File? {
            return project.extensions.findByType(extensionClass)?.let {
                it.sourceSets.findByName(name)?.let { sourceSet ->
                    project.configurations.getByName(sourceSet.implementationConfigurationName).dependencies.add(
                        project.dependencies.create("org.jetbrains.kotlinx:dataframe:$dataframeVersion")
                    )
                    val directory = project.file(path)
                    sourceSet.kotlin.srcDir(directory)
                    directory
                }
            }
        }
    }


}
