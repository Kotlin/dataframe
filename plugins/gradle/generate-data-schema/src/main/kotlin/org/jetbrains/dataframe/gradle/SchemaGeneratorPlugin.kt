package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.*
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
                        inferPackageName(
                            appliedPlugin.sourceSetConfiguration.isKotlinRoot,
                            appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSet)
                        )
                    }

                val src: File = schema.src
                    ?: run {
                        appliedPlugin ?: propertyError("src")
                        appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSet)
                        val path = appliedPlugin.sourceSetConfiguration.sourceSetPath(sourceSet)
                        project.file(path)
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

    private class SourceSetConfiguration<T : KotlinProjectExtension>(
        val extensionClass: Class<T>,
        val defaultSourceSet: String,
        val sourceSetPath: (String) -> String,
        val isKotlinRoot: (File, String) -> Boolean
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

    private fun inferPackageName(isKotlinRoot: (File, String) -> Boolean, sourceSet: KotlinSourceSet): String? {
        val root = sourceSet.kotlin.sourceDirectories.firstOrNull { isKotlinRoot(it, sourceSet.name) }?.absoluteFile ?: return null
        val node = root.findDeepestCommonSubdirectory()
        val parentPath = root.absolutePath
        return node.absolutePath
            .removePrefix(parentPath)
            .removePrefix(File.separator)
            .replace(File.separatorChar, '.')
            .let {
                when {
                    it.isEmpty() -> "dataframe"
                    it.endsWith(".dataframe") -> it
                    else -> "$it.dataframe"
                }
            }
    }

    private companion object {
        private val isKotlinRoot: (File, String) -> Boolean = { f, name -> f.absolutePath.contains("/src/${name}/kotlin") }
        private val sourceSetPath: (String) -> String = { "src/$it/kotlin" }
        private val isAndroidKotlinRoot: (File, String) -> Boolean = { f, name -> f.absolutePath.contains("/src/${name}/java") }
        private val androidSourceSetPath: (String) -> String = { name -> "src/${name}/java" }

        private val KOTLIN_EXTENSIONS = sequenceOf(
            SourceSetConfiguration(KotlinJvmProjectExtension::class.java, "main", sourceSetPath, isKotlinRoot),
            SourceSetConfiguration(KotlinMultiplatformExtension::class.java, "jvmMain", sourceSetPath, isKotlinRoot),
            SourceSetConfiguration(
                KotlinAndroidProjectExtension::class.java, "main", androidSourceSetPath, isAndroidKotlinRoot
            ),
        )
    }
}
