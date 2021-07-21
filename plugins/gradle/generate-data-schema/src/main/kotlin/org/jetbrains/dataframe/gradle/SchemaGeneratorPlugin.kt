package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.URL

class SchemaGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("schemaGenerator")
        target.afterEvaluate {
            val appliedPlugin = KOTLIN_EXTENSIONS
                    .mapNotNull {
                        target.extensions.findByType(it.extensionClass)?.let { ext -> AppliedPlugin(ext, it) }
                    }
                    .firstOrNull()

            if (appliedPlugin == null) {
                target.logger.warn("Schema generator plugin applied, but no Kotlin plugin was found")
            }

            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach { schema ->
                val interfaceName = getInterfaceName(schema)
                fun propertyError(property: String): Nothing {
                    error("No supported Kotlin plugin was found. Please apply one or specify $property for task $interfaceName explicitly")
                }

                val sourceSet by lazy {
                    schema.sourceSet
                        ?: extension.sourceSet
                        ?: (appliedPlugin ?: propertyError("sourceSet")).sourceSetConfiguration.defaultSourceSet
                }

                val packageName = schema.name?.let { name -> extractPackageName(name) }
                    ?: schema.packageName
                    ?: extension.packageName
                    ?: run {
                        (appliedPlugin ?: propertyError("packageName"))
                        inferPackageName(appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSet))
                    }

                val src: File = schema.src
                    ?: run {
                        appliedPlugin ?: propertyError("src")
                        appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSet)
                        project.file("src/$sourceSet/kotlin")
                    }

                val task = target.tasks.create("generate${interfaceName}", GenerateDataSchemaTask::class.java) {
                    data.set(schema.data)
                    this.interfaceName.set(interfaceName)
                    this.packageName.set(packageName)
                    this.src.set(src)
                    generateExtensionProperties.set(extension.generateExtensionProperties)
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

    private fun getInterfaceName(schema: Schema): String? {
        val rawName = schema.name?.substringAfterLast('.')
            ?: fileName(schema.data)?.capitalize()
            ?.removeSurrounding("`")
            ?: return null
        NameChecker.checkValidIdentifier(rawName)
        return rawName
    }

    private class AppliedPlugin(val kotlinExtension: KotlinProjectExtension, val sourceSetConfiguration: SourceSetConfiguration<*>)

    private class SourceSetConfiguration<T: KotlinProjectExtension>(
        val extensionClass: Class<T>, val defaultSourceSet: String
    )

    private fun fileName(data: Any?): String? {
        return when (data) {
            is String -> extractFileName(data)
            is URL -> extractFileName(data)
            is File -> extractFileName(data)
            else -> throw IllegalArgumentException("data for schema must be File, URL or String, but was ${data?.javaClass ?: ""}($data)")
        }
    }

    private fun extractPackageName(fqName: String): String? {
        val packageName = fqName
            .substringBeforeLast('.')
            .takeIf { it != fqName }
        if (packageName != null) {
            NameChecker.checkValidPackageName(packageName)
        }
        return packageName
    }

    private fun inferPackageName(sourceSet: KotlinSourceSet): String? {
        val isKotlinRoot = { f: File -> f.absolutePath.contains("/src/${sourceSet.name}/kotlin") }
        val root = sourceSet.kotlin.sourceDirectories.firstOrNull(isKotlinRoot)?.absoluteFile ?: return null
        val node = root.findDeepestCommonSubdirectory()
        val parentPath = root.absolutePath
        return node.absolutePath
            .removePrefix(parentPath)
            .removePrefix(File.separator)
            .replace(File.separatorChar, '.')
            .let {
                if (it.isEmpty()) "dataframe" else "$it.dataframe"
            }
    }

    private companion object {
        private val KOTLIN_EXTENSIONS = sequenceOf(
            SourceSetConfiguration(KotlinJvmProjectExtension::class.java, "main"),
            SourceSetConfiguration(KotlinMultiplatformExtension::class.java, "jvmMain"),
        )
    }
}
