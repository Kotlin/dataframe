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
            extension.schemas.forEach { schema ->
                val interfaceName = schema.name?.substringAfterLast('.') ?: fileName(schema.data)?.capitalize()

                val packageName = schema.name
                    ?.let { name -> extractPackageName(name) }
                    ?: schema.packageName
                    ?: extension.packageName
                    ?: inferPackageName()

                val task = target.tasks.create("generate${interfaceName}", GenerateDataSchemaTask::class.java) {
                    src.convention(defaultSrc)
                    data.set(schema.data)
                    this.interfaceName.set(interfaceName)
                    this.packageName.set(packageName)
                    generateExtensionProperties.set(extension.generateExtensionProperties)
                    src.set(schema.src)
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

    private fun extractPackageName(fqName: String): String? {
        val packageName = fqName
            .substringBeforeLast('.')
            .takeIf { it != fqName }
        if (packageName != null) {
            check(packageName.isPackageJvmIdentifier()) {
                "Package part '$packageName' of name='$fqName' is not a valid package identifier for Kotlin JVM"
            }
        }
        return packageName
    }

    private fun Project.inferPackageName(): String? {
        return KOTLIN_EXTENSIONS
            .map {
                extensions
                    .findByType(it.extensionClass)
                    ?.sourceSets
                    ?.getByName(it.name)
                    ?.let { sourceSet -> inferPackageName(sourceSet) }
            }
            .filterNotNull()
            .firstOrNull()
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

    private fun registerGeneratedSources(target: Project): File? {
        return KOTLIN_EXTENSIONS
            .map { it.apply(target) }
            .filterNotNull()
            .firstOrNull()
    }

    private class SourceSetConfiguration<T: KotlinProjectExtension>(
        val extensionClass: Class<T>, val name: String, val path: String
    ) {
        fun apply(project: Project): File? {
            return project.extensions.findByType(extensionClass)?.let {
                it.sourceSets.findByName(name)?.let { sourceSet ->
                    project.file(path)
                }
            }
        }
    }

    private companion object {
        private val KOTLIN_EXTENSIONS = sequenceOf(
            SourceSetConfiguration(KotlinJvmProjectExtension::class.java, "main", "src/main/kotlin"),
            SourceSetConfiguration(KotlinMultiplatformExtension::class.java, "jvmMain", "src/jvmMain/kotlin"),
        )

        private val KOTLIN_PLUGINS = listOf(
            "org.jetbrains.kotlin.jvm",
            "org.jetbrains.kotlin.android",
            "org.jetbrains.kotlin.multiplatform"
        )
    }
}
