package org.jetbrains.dataframe.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.DfReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlDfReader
import java.io.File
import java.lang.RuntimeException
import java.net.URL
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager
import org.jetbrains.kotlinx.dataframe.io.ArrowFeather
import org.jetbrains.kotlinx.dataframe.io.CSV
import org.jetbrains.kotlinx.dataframe.io.Excel
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.io.OpenApi
import org.jetbrains.kotlinx.dataframe.io.TSV
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlQuery
import org.jetbrains.kotlinx.dataframe.io.getSchemaForSqlTable
import org.jetbrains.kotlinx.dataframe.io.isURL
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

abstract class GenerateDataSchemaTask : DefaultTask() {

    @get:Input
    abstract val data: Property<Any>

    @get:Input
    abstract val csvOptions: Property<CsvOptionsDsl>

    @get:Input
    abstract val jsonOptions: Property<JsonOptionsDsl>

    @get:Input
    abstract val jdbcOptions: Property<JdbcOptionsDsl>

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
        val jsonOptions = jsonOptions.get()
        val jdbcOptions = jdbcOptions.get()
        val schemaFile = dataSchema.get()
        val escapedPackageName = escapePackageName(packageName.get())

        val rawUrl = data.get().toString()

        // revisit architecture for an addition of the new data source https://github.com/Kotlin/dataframe/issues/450
        if (rawUrl.startsWith("jdbc")) {
            val connection = DriverManager.getConnection(rawUrl, jdbcOptions.user, jdbcOptions.password)
            connection.use {
                val schema = generateSchemaByJdbcOptions(jdbcOptions, connection)

                val codeGenerator = CodeGenerator.create(useFqNames = false)

                val additionalImports: List<String> = listOf()

                val delimiters = delimiters.get()
                val codeGenResult = codeGenerator.generate(
                    schema = schema,
                    name = interfaceName.get(),
                    fields = true,
                    extensionProperties = false,
                    isOpen = true,
                    visibility = when (schemaVisibility.get()) {
                        DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
                        DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
                        DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
                        else -> MarkerVisibility.IMPLICIT_PUBLIC
                    },
                    readDfMethod = null,
                    fieldNameNormalizer = NameNormalizer.from(delimiters),
                )

                schemaFile.writeText(codeGenResult.toStandaloneSnippet(escapedPackageName, additionalImports))
                return
            }
        } else {
            val url = urlOf(data.get())

            val formats = listOf(
                CSV(delimiter = csvOptions.delimiter),
                JSON(typeClashTactic = jsonOptions.typeClashTactic, keyValuePaths = jsonOptions.keyValuePaths),
                Excel(),
                TSV(),
                ArrowFeather(),
                OpenApi(),
            )

            // first try without creating dataframe
            when (val codeGenResult = CodeGenerator.urlCodeGenReader(url, interfaceName.get(), formats, false)) {
                is CodeGenerationReadResult.Success -> {
                    val readDfMethod = codeGenResult.getReadDfMethod(stringOf(data.get()))
                    val code = codeGenResult
                        .code
                        .toStandaloneSnippet(escapedPackageName, readDfMethod.additionalImports)

                    schemaFile.bufferedWriter().use {
                        it.write(code)
                    }
                    return
                }

                is CodeGenerationReadResult.Error ->
                    logger.warn("Error while reading types-only from data at $url: ${codeGenResult.reason}")
            }

            // on error, try with reading dataframe first
            val parsedDf = when (val readResult = CodeGenerator.urlDfReader(url, formats)) {
                is DfReadResult.Error -> throw Exception(
                    "Error while reading dataframe from data at $url",
                    readResult.reason
                )

                is DfReadResult.Success -> readResult
            }

            val codeGenerator = CodeGenerator.create(useFqNames = false)
            val delimiters = delimiters.get()
            val readDfMethod = parsedDf.getReadDfMethod(stringOf(data.get()))
            val codeGenResult = codeGenerator.generate(
                schema = parsedDf.schema,
                name = interfaceName.get(),
                fields = true,
                extensionProperties = false,
                isOpen = true,
                visibility = when (schemaVisibility.get()) {
                    DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
                    DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
                    DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
                    else -> MarkerVisibility.IMPLICIT_PUBLIC
                },
                readDfMethod = readDfMethod,
                fieldNameNormalizer = NameNormalizer.from(delimiters),
            )
            schemaFile.writeText(codeGenResult.toStandaloneSnippet(escapedPackageName, readDfMethod.additionalImports))
        }
    }

    private fun generateSchemaByJdbcOptions(
        jdbcOptions: JdbcOptionsDsl,
        connection: Connection
    ): DataFrameSchema {
        logger.debug("Table name: ${jdbcOptions.tableName}")
        logger.debug("SQL query: ${jdbcOptions.sqlQuery}")

        return if (jdbcOptions.tableName.isNotBlank())
            DataFrame.getSchemaForSqlTable(connection, jdbcOptions.tableName)
        else if(jdbcOptions.sqlQuery.isNotBlank())
            DataFrame.getSchemaForSqlQuery(connection, jdbcOptions.sqlQuery)
        else throw RuntimeException("Table name: ${jdbcOptions.tableName}, " +
            "SQL query: ${jdbcOptions.sqlQuery} both are empty! " +
            "Populate 'tableName' or 'sqlQuery' in jdbcOptions with value to generate schema " +
            "for SQL table or result of SQL query!")
    }

    private fun stringOf(data: Any): String =
        when (data) {
            is File -> data.absolutePath
            is URL -> data.toExternalForm()
            is String ->
                when {
                    isURL(data) -> stringOf(URL(data))
                    else -> {
                        val relativeFile = project.file(data)
                        val absoluteFile = File(data)
                        stringOf(if (relativeFile.exists()) relativeFile else absoluteFile)
                    }
                }

            else -> unsupportedType()
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

    private fun urlOf(data: Any): URL =
        when (data) {
            is File -> data.toURI()
            is URL -> data.toURI()
            is String -> when {
                isURL(data) -> URL(data).toURI()
                else -> {
                    val relativeFile = project.file(data)
                    val absoluteFile = File(data)

                    if (relativeFile.exists()) {
                        relativeFile
                    } else {
                        absoluteFile
                    }.toURI()
                }
            }

            else -> unsupportedType()
        }.toURL()

    private fun unsupportedType(): Nothing =
        throw IllegalArgumentException("data for schema \"${interfaceName.get()}\" must be File, URL or String")
}
