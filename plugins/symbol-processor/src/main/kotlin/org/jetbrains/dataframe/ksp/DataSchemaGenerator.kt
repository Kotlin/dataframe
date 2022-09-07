package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.annotations.CsvOptions
import org.jetbrains.kotlinx.dataframe.annotations.DataSchemaVisibility
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.DfReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlCodeGenReader
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlDfReader
import org.jetbrains.kotlinx.dataframe.io.ArrowFeather
import org.jetbrains.kotlinx.dataframe.io.CSV
import org.jetbrains.kotlinx.dataframe.io.Excel
import org.jetbrains.kotlinx.dataframe.io.JSON
import org.jetbrains.kotlinx.dataframe.io.OpenApi
import org.jetbrains.kotlinx.dataframe.io.TSV
import java.io.File
import java.net.MalformedURLException
import java.net.URL

@OptIn(KspExperimental::class)
class DataSchemaGenerator(
    private val resolver: Resolver,
    private val resolutionDir: String?,
    private val logger: KSPLogger,
    private val codeGenerator: com.google.devtools.ksp.processing.CodeGenerator
) {

    fun resolveImportStatements() = listOf(
        ::resolvePathImports,
    ).flatMap { it(resolver) }

    class ImportDataSchemaStatement(
        val origin: KSFile,
        val name: String,
        val dataSource: CodeGeneratorDataSource,
        val visibility: MarkerVisibility,
        val normalizationDelimiters: List<Char>,
        val withDefaultPath: Boolean,
        val csvOptions: CsvOptions
    )

    class CodeGeneratorDataSource(val pathRepresentation: String, val data: URL)

    private fun resolvePathImports(resolver: Resolver) = resolver
        .getSymbolsWithAnnotation(ImportDataSchema::class.qualifiedName!!)
        .filterIsInstance<KSFile>()
        .flatMap { file ->
            file.getAnnotationsByType(ImportDataSchema::class).mapNotNull { it.toStatement(file, logger) }
        }

    private fun ImportDataSchema.toStatement(file: KSFile, logger: KSPLogger): ImportDataSchemaStatement? {
        val protocols = listOf("http", "https", "ftp")
        val url = if (protocols.any { path.startsWith(it, ignoreCase = true) }) {
            try {
                URL(this.path)
            } catch (exception: MalformedURLException) {
                logger.error("'${this.path}' is not valid URL: ${exception.message}", file)
                null
            }
        } else {
            val resolutionDir: String = resolutionDir ?: run {
                reportMissingKspArgument(file)
                return null
            }
            val data = File(resolutionDir, path)
            try {
                data.toURI().toURL()
            } catch (exception: MalformedURLException) {
                logger.error(
                    "Failed to convert resolved path '${data.absolutePath}' to URL: ${exception.message}",
                    file
                )
                null
            }
        } ?: return null
        return ImportDataSchemaStatement(
            file,
            name,
            CodeGeneratorDataSource(this.path, url),
            visibility.toMarkerVisibility(),
            normalizationDelimiters.toList(),
            withDefaultPath,
            csvOptions
        )
    }

    private fun DataSchemaVisibility.toMarkerVisibility(): MarkerVisibility = when (this) {
        DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
        DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
        DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
    }

    private fun reportMissingKspArgument(file: KSFile) {
        logger.error(
            """
            |KSP option with key "dataframe.resolutionDir" must be set in order to use relative path in @${ImportDataSchema::class.simpleName}
            |DataFrame Gradle plugin should set it by default to "project.projectDir".
            |If you do not use DataFrame Gradle plugin, configure option manually 
            """.trimMargin(),
            symbol = file
        )
    }

    fun generateDataSchema(importStatement: ImportDataSchemaStatement) {
        val packageName = importStatement.origin.packageName.asString()
        val name = importStatement.name
        val schemaFile =
            codeGenerator.createNewFile(Dependencies(true, importStatement.origin), packageName, "$name.Generated")

        val formats = listOf(
            CSV(delimiter = importStatement.csvOptions.delimiter),
            JSON(),
            Excel(),
            TSV(),
            ArrowFeather(),
            OpenApi(),
        )

        // first try without creating dataframe
        when (val codeGenResult = CodeGenerator.urlCodeGenReader(importStatement.dataSource.data, formats)) {
            is CodeGenerationReadResult.Success -> {
                val readDfMethod =
                    codeGenResult.getReadDfMethod(importStatement.dataSource.pathRepresentation.takeIf { importStatement.withDefaultPath })
                val code = codeGenResult.code.toStandaloneSnippet(packageName, readDfMethod.additionalImports)
                schemaFile.bufferedWriter().use {
                    it.write(code)
                }
                return
            }

            is CodeGenerationReadResult.Error -> {
                logger.warn("Error while reading types-only from data at ${importStatement.dataSource.pathRepresentation}: ${codeGenResult.reason}")
            }
        }

        // on error, try with reading dataframe first
        val parsedDf = when (val readResult = CodeGenerator.urlDfReader(importStatement.dataSource.data, formats)) {
            is DfReadResult.Error -> {
                logger.error("Error while reading dataframe from data at ${importStatement.dataSource.pathRepresentation}: ${readResult.reason}")
                return
            }

            is DfReadResult.Success -> readResult
        }

        val readDfMethod =
            parsedDf.getReadDfMethod(importStatement.dataSource.pathRepresentation.takeIf { importStatement.withDefaultPath })
        val codeGenerator = CodeGenerator.create(useFqNames = false)

        val codeGenResult = codeGenerator.generate(
            schema = parsedDf.schema,
            name = name,
            fields = true,
            extensionProperties = false,
            isOpen = true,
            visibility = importStatement.visibility,
            knownMarkers = emptyList(),
            readDfMethod = readDfMethod,
            fieldNameNormalizer = NameNormalizer.from(importStatement.normalizationDelimiters.toSet())
        )
        val code = codeGenResult.toStandaloneSnippet(packageName, readDfMethod.additionalImports)
        schemaFile.bufferedWriter().use {
            it.write(code)
        }
    }
}
