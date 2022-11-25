package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths

class SchemaGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("dataframes")
        extension.project = target
        target.afterEvaluate {
            val appliedPlugin = KOTLIN_EXTENSIONS
                .mapNotNull {
                    target.extensions.findByType(it.extensionClass)?.let { ext -> AppliedPlugin(ext, it) }
                }
                .firstOrNull()

            if (appliedPlugin == null) {
                target.logger.warn("Schema generator plugin applied, but no Kotlin plugin was found")
            }

            val generationTasks = extension.schemas.map { createTask(target, extension, appliedPlugin, it) }
            val generateAll = target.tasks.create("generateDataFrames") {
                group = GROUP
                dependsOn(*generationTasks.toTypedArray())
            }
            tasks.withType<KotlinCompile> {
                dependsOn(generateAll)
            }
        }
    }

    private fun createTask(
        target: Project,
        extension: SchemaGeneratorExtension,
        appliedPlugin: AppliedPlugin?,
        schema: Schema,
    ): Task {
        val interfaceName = getInterfaceName(schema)
        fun propertyError(property: String): Nothing {
            error("No supported Kotlin plugin was found. Please apply one or specify property $property for schema $interfaceName explicitly")
        }

        val sourceSetName by lazy {
            schema.sourceSet
                ?: extension.sourceSet
                ?: (appliedPlugin ?: propertyError("sourceSet")).sourceSetConfiguration.defaultSourceSet
        }

        val src: File = schema.src
            ?: run {
                appliedPlugin ?: propertyError("src")
                val sourceSet = appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSetName)
                val src = target.file(Paths.get("build/generated/dataframe/", sourceSetName, "kotlin").toFile())
                sourceSet.kotlin.srcDir(src)
                src
            }

        val packageName = schema.name?.let { name -> extractPackageName(name) }
            ?: schema.packageName
            ?: extension.packageName
            ?: run {
                (appliedPlugin ?: propertyError("packageName"))
                val sourceSet = appliedPlugin.kotlinExtension.sourceSets.getByName(sourceSetName)
                val path = appliedPlugin.sourceSetConfiguration.getKotlinRoot(
                    sourceSet.kotlin.sourceDirectories,
                    sourceSetName
                )
                val src = target.file(path)
                inferPackageName(src)
            }

        val visibility = schema.visibility
            ?: extension.visibility
            ?: run {
                if (appliedPlugin != null) {
                    when (appliedPlugin.kotlinExtension.explicitApi) {
                        null -> DataSchemaVisibility.IMPLICIT_PUBLIC
                        ExplicitApiMode.Strict -> DataSchemaVisibility.EXPLICIT_PUBLIC
                        ExplicitApiMode.Warning -> DataSchemaVisibility.EXPLICIT_PUBLIC
                        ExplicitApiMode.Disabled -> DataSchemaVisibility.IMPLICIT_PUBLIC
                    }
                } else {
                    DataSchemaVisibility.IMPLICIT_PUBLIC
                }
            }

        val defaultPath = schema.defaultPath ?: extension.defaultPath ?: true
        val delimiters = schema.withNormalizationBy ?: extension.withNormalizationBy ?: setOf('\t', ' ', '_')

        return target.tasks.create("generateDataFrame${interfaceName}", GenerateDataSchemaTask::class.java) {
            group = GROUP
            data.set(schema.data)
            this.interfaceName.set(interfaceName)
            this.packageName.set(packageName)
            this.src.set(src)
            this.schemaVisibility.set(visibility)
            this.csvOptions.set(schema.csvOptions)
            this.jsonOptions.set(schema.jsonOptions)
            this.defaultPath.set(defaultPath)
            this.delimiters.set(delimiters)
        }
    }

    private fun getInterfaceName(schema: Schema): String? {
        val rawName = schema.name?.substringAfterLast('.')
            ?: fileName(schema.data)
                ?.toCamelCaseByDelimiters(delimiters)
                ?.capitalize()
                ?.removeSurrounding("`")
            ?: return null
        NameChecker.checkValidIdentifier(rawName)
        return rawName
    }

    private val delimiters = "[\\s_]".toRegex()

    private class AppliedPlugin(
        val kotlinExtension: KotlinProjectExtension,
        val sourceSetConfiguration: SourceSetConfiguration<*>,
    )

    private class SourceSetConfiguration<T : KotlinProjectExtension>(
        val extensionClass: Class<T>,
        val defaultSourceSet: String,
    ) {
        fun getKotlinRoot(sourceDirectories: FileCollection, sourceSetName: String): File {
            fun sourceSet(lang: String) = Paths.get("src", sourceSetName, lang)
            val ktSet = sourceSet("kotlin")
            val javaSet = sourceSet("java")
            val isKotlinRoot: (Path) -> Boolean = { f -> f.endsWith(ktSet) }
            val genericRoot = sourceDirectories.find { isKotlinRoot(it.toPath()) }
            if (genericRoot != null) return genericRoot
            val androidSpecificRoot = if (extensionClass == KotlinAndroidProjectExtension::class.java) {
                val isAndroidKotlinRoot: (Path) -> Boolean = { f -> f.endsWith(javaSet) }
                sourceDirectories.find { isAndroidKotlinRoot(it.toPath()) }
            } else {
                error("Directory '$ktSet' was not found in $sourceSetName. Please, specify 'src' explicitly")
            }
            return androidSpecificRoot
                ?: error(
                    "Directory '$ktSet' or '$javaSet' was not found in $sourceSetName. Please, specify 'src' explicitly"
                )
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
            NameChecker.checkValidPackageName(packageName)
        }
        return packageName
    }

    private fun inferPackageName(root: File): String {
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
        private val KOTLIN_EXTENSIONS = sequenceOf(
            SourceSetConfiguration(KotlinJvmProjectExtension::class.java, "main"),
            SourceSetConfiguration(KotlinMultiplatformExtension::class.java, "jvmMain"),
            SourceSetConfiguration(KotlinAndroidProjectExtension::class.java, "main"),
        )
        private const val GROUP = "dataframe"
    }
}
