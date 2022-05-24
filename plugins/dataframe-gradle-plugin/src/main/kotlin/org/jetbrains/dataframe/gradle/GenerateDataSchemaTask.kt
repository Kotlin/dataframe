package org.jetbrains.dataframe.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.DfReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlReader
import org.jetbrains.kotlinx.dataframe.io.ArrowFeather
import org.jetbrains.kotlinx.dataframe.io.CSV
import org.jetbrains.kotlinx.dataframe.io.Excel
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.io.TSV
import java.io.File
import java.net.URL
import java.nio.file.Paths

abstract class GenerateDataSchemaTask : DefaultTask() {

    @get:Input
    abstract val data: Property<Any>

    @get:Input
    abstract val csvOptions: Property<CsvOptionsDsl>

    @get:Input
    abstract val src: Property<File>

    @get:Input
    abstract val interfaceName: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val schemaVisibility: Property<DataSchemaVisibility>

    @get:Input
    abstract val defaultPath: Property<Boolean>

    @get:Input
    abstract val delimiters: SetProperty<Char>

    @Suppress("LeakingThis")
    @get:OutputFile
    val dataSchema: Provider<File> = packageName.zip(interfaceName) { packageName, interfaceName ->
        val packagePath = packageName.replace('.', File.separatorChar)
        Paths.get(src.get().absolutePath, packagePath, "$interfaceName.Generated.kt").toFile()
    }

    @TaskAction
    fun generate() {
        val csvOptions = csvOptions.get()
        val url = urlOf(data.get())
        val formats = listOf(
            CSV(delimiter = csvOptions.delimiter),
            JSON(),
            Excel(),
            TSV(),
            ArrowFeather()
        )
        val res = when (val readResult = CodeGenerator.urlReader(url, formats)) {
            is DfReadResult.Success -> readResult
            is DfReadResult.Error -> throw Exception("Error while reading dataframe from data at $url", readResult.reason)
        }
        if (res.format is ArrowFeather) {
            val arrowDependency = project.configurations.asSequence()
                .mapNotNull { configuration ->
                    configuration.allDependencies.find { it.group?.equals("org.jetbrains.kotlinx") ?: false && it.name == "dataframe-arrow"  }
                }
                .firstOrNull()

            if (arrowDependency == null) {
                project.logger.warn("Add dependency on \"org.jetbrains.kotlinx:dataframe-arrow\" to compile schema ${interfaceName.get()} generated from ${data.get()}")
            }
        }
        val codeGenerator = CodeGenerator.create(useFqNames = false)
        val delimiters = delimiters.get()
        val readDfMethod = res.getReadDfMethod(stringOf(data.get()))
        val codeGenResult = codeGenerator.generate(
            schema = res.schema,
            name = interfaceName.get(),
            fields = true,
            extensionProperties = false,
            isOpen = true,
            visibility = when (schemaVisibility.get()) {
                DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
                DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
                DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
            },
            readDfMethod = readDfMethod,
            fieldNameNormalizer = NameNormalizer.from(delimiters)
        )
        val escapedPackageName = escapePackageName(packageName.get())

        val dataSchema = dataSchema.get()
        dataSchema.writeText(codeGenResult.toStandaloneSnippet(escapedPackageName, readDfMethod.additionalImports))
    }

    private fun stringOf(data: Any): String {
        return when (data) {
            is File -> data.toRelativeString(base = project.projectDir)
            is URL -> data.toExternalForm()
            is String -> data
            else -> unsupportedType()
        }
    }

    private fun escapePackageName(packageName: String): String {
        // See RegexExpectationsTest
        return if (packageName.isNotEmpty()) {
            packageName.split(NameChecker.PACKAGE_IDENTIFIER_DELIMITER)
                .joinToString(".") { part -> "`$part`" }
        } else {
            packageName
        }
    }

    private fun urlOf(data: Any): URL {
        fun isURL(fileOrUrl: String): Boolean = listOf("http:", "https:", "ftp:").any { fileOrUrl.startsWith(it) }

        return when (data) {
            is File -> data.toURI()
            is URL -> data.toURI()
            is String -> when {
                isURL(data) -> URL(data).toURI()
                else -> project.file(data).toURI()
            }
            else -> unsupportedType()
        }.toURL()
    }

    private fun unsupportedType(): Nothing =
        throw IllegalArgumentException("data for schema \"${interfaceName.get()}\" must be File, URL or String")
}
