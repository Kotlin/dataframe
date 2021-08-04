package org.jetbrains.dataframe.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File
import java.net.URL

class SchemaGeneratorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<SchemaGeneratorExtension>("dataframes")
        target.afterEvaluate {
            val appliedPlugin = KOTLIN_EXTENSIONS
                .mapNotNull {
                    target.extensions.findByType(it.extensionClass)?.let { ext -> AppliedPlugin(ext, it) }
                }
                .firstOrNull()

            val kspPluginEvidence = target.plugins.findPlugin(KspPluginApplier::class.java)

            if (appliedPlugin == null) {
                target.logger.warn("Schema generator plugin applied, but no Kotlin plugin was found")
            }

            val generationTasks = extension.schemas.map { schema ->
                createTask(
                    target,
                    extension,
                    appliedPlugin,
                    schema,
                    kspPluginEvidence
                )
            }
            val generateAll = target.tasks.create("generateDataFrames") {
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
        kspPluginEvidence: KspPluginApplier?
    ): Task {
        val interfaceName = getInterfaceName(schema)
        fun propertyError(property: String): Nothing {
            error("No supported Kotlin plugin was found. Please apply one or specify $property for task $interfaceName explicitly")
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
                if (kspPluginEvidence != null) {
                    sourceSet.kotlin.srcDir("build/generated/ksp/$sourceSetName/kotlin/")
                }
                val path = appliedPlugin.sourceSetConfiguration.getKotlinRoot(
                    sourceSet.kotlin.sourceDirectories,
                    sourceSetName
                )
                target.file(path)
            }

        val packageName = schema.name?.let { name -> extractPackageName(name) }
            ?: schema.packageName
            ?: extension.packageName
            ?: run {
                (appliedPlugin ?: propertyError("packageName"))
                inferPackageName(src)
            }

        return target.tasks.create("generateDataFrame${interfaceName}", GenerateDataSchemaTask::class.java) {
            data.set(schema.data)
            this.interfaceName.set(interfaceName)
            this.packageName.set(packageName)
            this.src.set(src)
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

    private class AppliedPlugin(
        val kotlinExtension: KotlinProjectExtension,
        val sourceSetConfiguration: SourceSetConfiguration<*>
    )

    private class SourceSetConfiguration<T : KotlinProjectExtension>(
        val extensionClass: Class<T>,
        val defaultSourceSet: String,
    ) {
        fun getKotlinRoot(sourceDirectories: FileCollection, sourceSetName: String): File {
            val isKotlinRoot: (File) -> Boolean = { f -> f.absolutePath.contains("/src/${sourceSetName}/kotlin") }
            val genericRoot = sourceDirectories.find { isKotlinRoot(it) }
            if (genericRoot != null) return genericRoot
            val androidSpecificRoot = if (extensionClass == KotlinAndroidProjectExtension::class.java) {
                val isAndroidKotlinRoot: (File) -> Boolean =
                    { f -> f.absolutePath.contains("/src/${sourceSetName}/java") }
                sourceDirectories.find { isAndroidKotlinRoot(it) }
            } else {
                error("Directory 'src/$sourceSetName/kotlin' was not found in $sourceSetName. Please, specify 'src' explicitly")
            }
            return androidSpecificRoot
                ?: error(
                    "Directory 'src/$sourceSetName/kotlin' or 'src/$sourceSetName/java' was not found in $sourceSetName. Please, specify 'src' explicitly"
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

    private fun inferPackageName(root: File): String? {
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
    }
}
