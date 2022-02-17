package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.annotations.DataSchemaVisibility
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchemaByAbsolutePath
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchemaByRelativePath
import org.jetbrains.kotlinx.dataframe.codeGen.CsvOptions
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.DfReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlReader
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
        ::resolveRelativePathImports,
        ::resolveUrlImports,
        ::resolveAbsolutePathImports
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

    private fun resolveRelativePathImports(resolver: Resolver) = resolver
        .getSymbolsWithAnnotation(ImportDataSchemaByRelativePath::class.qualifiedName!!)
        .filterIsInstance<KSFile>()
        .flatMap { file ->
            if (resolutionDir != null) {
                file
                    .getAnnotationsByType(ImportDataSchemaByRelativePath::class)
                    .mapNotNull { it.toStatement(file, resolutionDir, logger) }
            } else {
                reportMissingKspArgument(file)
                emptySequence()
            }
        }

    private fun resolveUrlImports(resolver: Resolver) = resolver
        .getSymbolsWithAnnotation(ImportDataSchema::class.qualifiedName!!)
        .filterIsInstance<KSFile>()
        .flatMap { file ->
            file.getAnnotationsByType(ImportDataSchema::class).mapNotNull { it.toStatement(file, logger) }
        }

    private fun resolveAbsolutePathImports(resolver: Resolver) = resolver
        .getSymbolsWithAnnotation(ImportDataSchemaByAbsolutePath::class.qualifiedName!!)
        .filterIsInstance<KSFile>()
        .flatMap { file ->
            file.getAnnotationsByType(ImportDataSchemaByAbsolutePath::class).mapNotNull { it.toStatement(file, logger) }
        }

    private fun ImportDataSchema.toStatement(file: KSFile, logger: KSPLogger): ImportDataSchemaStatement? {
        val url = try {
            URL(this.url)
        } catch (exception: MalformedURLException) {
            logger.error("'${this.url}' is not valid URL: ${exception.message}", file)
            null
        } ?: return null
        return ImportDataSchemaStatement(
            file,
            name,
            CodeGeneratorDataSource(this.url, url),
            visibility.toMarkerVisibility(),
            normalizationDelimiters.toList(),
            withDefaultPath,
            CsvOptions(csvOptions.delimiter)
        )
    }

    private fun ImportDataSchemaByAbsolutePath.toStatement(file: KSFile, logger: KSPLogger): ImportDataSchemaStatement? {
        val data = File(absolutePath)
        val url = try {
            data.toURI().toURL()
        } catch (exception: MalformedURLException) {
            logger.error("$absolutePath is not valid URL: ${exception.message}", file)
            null
        } ?: return null
        return ImportDataSchemaStatement(
            file,
            name,
            CodeGeneratorDataSource(absolutePath, url),
            visibility.toMarkerVisibility(),
            normalizationDelimiters.toList(),
            withDefaultPath,
            CsvOptions(csvOptions.delimiter)
        )
    }

    private fun ImportDataSchemaByRelativePath.toStatement(file: KSFile, resolutionDir: String, logger: KSPLogger): ImportDataSchemaStatement? {
        val data = File(resolutionDir, path)
        val url = try {
            data.toURI().toURL()
        } catch (exception: MalformedURLException) {
            logger.error("$path is not valid URL: ${exception.message}", file)
            null
        } ?: return null
        return ImportDataSchemaStatement(
            file,
            name,
            CodeGeneratorDataSource(path, url),
            visibility.toMarkerVisibility(),
            normalizationDelimiters.toList(),
            withDefaultPath,
            CsvOptions(csvOptions.delimiter)
        )
    }

    private fun DataSchemaVisibility.toMarkerVisibility(): MarkerVisibility = when (this) {
        DataSchemaVisibility.INTERNAL -> MarkerVisibility.INTERNAL
        DataSchemaVisibility.IMPLICIT_PUBLIC -> MarkerVisibility.IMPLICIT_PUBLIC
        DataSchemaVisibility.EXPLICIT_PUBLIC -> MarkerVisibility.EXPLICIT_PUBLIC
    }

    private fun reportMissingKspArgument(file: KSFile) {
        logger.error("""
        |KSP option with key "dataframe.resolutionDir" must be set in order to use @${ImportDataSchemaByRelativePath::class.simpleName}
        |DataFrame Gradle plugin should set it by default to "project.rootDir".
        |If you do not use DataFrame Gradle plugin, configure option manually 
    """.trimMargin(), symbol = file)
    }


    fun generateDataSchema(importStatement: ImportDataSchemaStatement) {
        val packageName = importStatement.origin.packageName.asString()
        val name = importStatement.name
        val csvOptions = CsvOptions(importStatement.csvOptions.delimiter)
        val schemaFile =
            codeGenerator.createNewFile(Dependencies(true, importStatement.origin), packageName, "$name.Generated")

        val parsedDf = when (val readResult = CodeGenerator.urlReader(importStatement.dataSource.data, csvOptions)) {
            is DfReadResult.Success -> readResult
            is DfReadResult.Error -> {
                logger.error("Error while reading dataframe from data at ${importStatement.dataSource.pathRepresentation}: ${readResult.reason}")
                return
            }
        }
        val codeGenerator = CodeGenerator.create(useFqNames = false)
        val codeGenResult = codeGenerator.generate(
            parsedDf.schema,
            name,
            fields = true,
            extensionProperties = false,
            isOpen = true,
            importStatement.visibility,
            emptyList(),
            parsedDf.getReadDfMethod(importStatement.dataSource.pathRepresentation.takeIf { importStatement.withDefaultPath }),
            NameNormalizer.from(importStatement.normalizationDelimiters.toSet())
        )
        val code = codeGenResult.toStandaloneSnippet(packageName)
        schemaFile.bufferedWriter().use {
            it.write(code)
        }
    }
}
