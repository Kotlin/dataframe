package org.jetbrains.dataframe.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.dataframe.impl.codeGen.CodeGenResult
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadCsvMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadJsonMethod
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.io.SupportedFormats
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson
import java.io.File
import java.net.URL
import java.nio.file.Paths

abstract class GenerateDataSchemaTask : DefaultTask() {

    @get:Input
    abstract val data: Property<Any>

    @get:Input
    abstract val csvOptions: Property<CsvOptions>

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
        val (df, format) = readDataFrame(data.get(), csvOptions)
        val codeGenerator = CodeGenerator.create(useFqNames = false)
        val delimiters = delimiters.get()
        val codeGenResult = codeGenerator.generate(
            schema = df.schema(),
            name = interfaceName.get(),
            fields = true,
            extensionProperties = false,
            isOpen = true,
            visibility = when (schemaVisibility.get()) {
                DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
                DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
                DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
            },
            readDfMethod = stringOf(data.get()).let {
                val defaultPath = it.takeIf { defaultPath.get() }
                when (format) {
                    SupportedFormats.JSON -> DefaultReadJsonMethod(defaultPath)
                    SupportedFormats.CSV -> {
                        val (delimiter) = csvOptions
                        DefaultReadCsvMethod(defaultPath, delimiter)
                    }
                }
            },
            fieldNameNormalizer = NameNormalizer.from(delimiters)
        )
        val escapedPackageName = escapePackageName(packageName.get())

        val dataSchema = dataSchema.get()
        dataSchema.writeText(buildSourceFileContent(escapedPackageName, codeGenResult))
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

    private fun readDataFrame(data: Any, csvOptions: CsvOptions): Pair<AnyFrame, SupportedFormats> {
        fun guessFormat(url: String): SupportedFormats? = when {
            url.endsWith(".csv") -> SupportedFormats.CSV
            url.endsWith(".json") -> SupportedFormats.JSON
            else -> null
        }
        fun readCSV(url: URL) = run {
            val (delimiter) = csvOptions
            DataFrame.readCSV(url, delimiter = delimiter) to SupportedFormats.CSV
        }
        fun readJson(url: URL) = DataFrame.readJson(url) to SupportedFormats.JSON
        return try {
            when (guessFormat(url.path)) {
                SupportedFormats.CSV -> readCSV(url)
                SupportedFormats.JSON -> readJson(url)
                else -> try {
                    readCSV(url)
                } catch (e: Exception) {
                    readJson(url)
                }
            }
        } catch (e: Exception) {
            throw Exception("Error while reading dataframe from data at $url", e)
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

    private fun buildSourceFileContent(escapedPackageName: String, codeGenResult: CodeGenResult): String {
        return buildString {
            if (escapedPackageName.isNotEmpty()) {
                appendLine("package $escapedPackageName")
                appendLine()
            }
            appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsContainer")
            appendLine("import org.jetbrains.kotlinx.dataframe.DataColumn")
            appendLine("import org.jetbrains.kotlinx.dataframe.DataFrame")
            appendLine("import org.jetbrains.kotlinx.dataframe.DataRow")
            appendLine("import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup")
            appendLine("import org.jetbrains.kotlinx.dataframe.annotations.ColumnName")
            appendLine("import org.jetbrains.kotlinx.dataframe.annotations.DataSchema")
            appendLine("import org.jetbrains.kotlinx.dataframe.api.cast")
            appendLine("import org.jetbrains.kotlinx.dataframe.io.readJson")
            appendLine("import org.jetbrains.kotlinx.dataframe.io.readCSV")
            appendLine()
            appendLine(codeGenResult.code.declarations)
        }
    }
}
