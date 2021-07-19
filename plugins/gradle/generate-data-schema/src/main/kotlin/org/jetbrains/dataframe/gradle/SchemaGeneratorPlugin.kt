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
            val defaultSrc by lazy { extension.sourceSet?.let { findKotlinRootForSourceSet(target, it) } }
            val generationTasks = mutableListOf<GenerateDataSchemaTask>()
            extension.schemas.forEach { schema ->
                val interfaceName = schema.name?.substringAfterLast('.') ?: fileName(schema.data)?.capitalize()

                val packageName = schema.name
                    ?.let { name -> extractPackageName(name) }
                    ?: schema.packageName
                    ?: extension.packageName
                    ?: inferPackageName()

                val src = schema.src
                    ?: schema.sourceSet?.let { sourceSet -> findKotlinRootForSourceSet(target, sourceSet) }
                    ?: defaultSrc
                    ?: (findDefaultRoot(target) ?: error("Couldn't find default root. Please specify src for task $interfaceName"))

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
                    ?.getByName(it.defaultSourceSet)
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

    private fun findKotlinRootForSourceSet(target: Project, sourceSet: String): File {
        return KOTLIN_EXTENSIONS
            .map { it.apply(target, sourceSet) }
            .filterNotNull()
            .firstOrNull()
            ?: error("SourceSet $sourceSet not found in $target")
    }

    private fun findDefaultRoot(project: Project): File? {
        for (sourceSetConfiguration in KOTLIN_EXTENSIONS) {
            val extension = project.extensions.findByType(sourceSetConfiguration.extensionClass) ?: continue
            extension.sourceSets.getByName(sourceSetConfiguration.defaultSourceSet)
            return project.file(sourceSetConfiguration.path)
        }
        return null
    }

    private class SourceSetConfiguration<T: KotlinProjectExtension>(
        val extensionClass: Class<T>, val defaultSourceSet: String, val path: String
    ) {
        fun apply(project: Project, sourceSet: String?): File? {
            return project.extensions.findByType(extensionClass)?.let {
                it.sourceSets.findByName(sourceSet ?: defaultSourceSet)?.let { sourceSet ->
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
