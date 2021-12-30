package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import com.google.devtools.ksp.validate
import org.jetbrains.dataframe.impl.codeGen.CodeGenerator
import org.jetbrains.kotlinx.dataframe.annotations.DataSchemaVisibility
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchema
import org.jetbrains.kotlinx.dataframe.annotations.ImportDataSchemaByAbsolutePath
import org.jetbrains.kotlinx.dataframe.codeGen.CsvOptions
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.NameNormalizer
import org.jetbrains.kotlinx.dataframe.impl.codeGen.DfReadResult
import org.jetbrains.kotlinx.dataframe.impl.codeGen.from
import org.jetbrains.kotlinx.dataframe.impl.codeGen.toStandaloneSnippet
import org.jetbrains.kotlinx.dataframe.impl.codeGen.urlReader
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.MalformedURLException
import java.net.URL
import com.google.devtools.ksp.processing.CodeGenerator as KspCodeGenerator

@OptIn(KspExperimental::class)
class DataFrameSymbolProcessor(
    private val codeGenerator: KspCodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private companion object {
        val EXPECTED_VISIBILITIES = setOf(Visibility.PUBLIC, Visibility.INTERNAL)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dataSchemaAnnotation = resolver.getKSNameFromString(DataFrameNames.DATA_SCHEMA)
        val symbols = resolver.getSymbolsWithAnnotation(dataSchemaAnnotation.asString())

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull {
                if (it.validate()) {
                    it.toDataSchemaDeclarationOrNull()
                } else {
                    null
                }
            }
            .forEach {
                val file = it.origin.containingFile ?: return@forEach
                generate(file, it.origin, it.properties)
            }

        val importStatements = buildList<ImportDataSchemaStatement> {
            this += resolver
                .getSymbolsWithAnnotation(ImportDataSchema::class.qualifiedName!!)
                .filterIsInstance<KSFile>()
                .flatMap { file ->
                    file.getAnnotationsByType(ImportDataSchema::class).mapNotNull { it.toStatement(file) }
                }

            this += resolver
                .getSymbolsWithAnnotation(ImportDataSchemaByAbsolutePath::class.qualifiedName!!)
                .filterIsInstance<KSFile>()
                .flatMap { file ->
                    file.getAnnotationsByType(ImportDataSchemaByAbsolutePath::class).mapNotNull { it.toStatement(file) }
                }
        }

        importStatements.forEach { importedSchema ->
            val packageName = importedSchema.origin.packageName.asString()
            val name = importedSchema.name
            val csvOptions = CsvOptions(importedSchema.csvOptions.delimiter)
            val schemaFile = codeGenerator.createNewFile(Dependencies(true, importedSchema.origin), packageName, "$name.Generated")

            val parsedDf = when (val readResult = CodeGenerator.urlReader(importedSchema.data, csvOptions)) {
                is DfReadResult.Success -> readResult
                is DfReadResult.Error -> {
                    logger.error("Error while reading dataframe from data at ${importedSchema.data.toExternalForm()}: ${readResult.reason}")
                    return@forEach
                }
            }
            val codeGenerator = CodeGenerator.create(useFqNames = false)
            val codeGenResult = codeGenerator.generate(
                parsedDf.schema,
                name,
                fields = true,
                extensionProperties = false,
                isOpen = true,
                importedSchema.visibility,
                emptyList(),
                parsedDf.getReadDfMethod(importedSchema.data.toExternalForm().takeIf { importedSchema.withDefaultPath }),
                NameNormalizer.from(importedSchema.normalizationDelimiters.toSet())
            )
            val code = codeGenResult.toStandaloneSnippet(packageName)
            schemaFile.bufferedWriter().use {
                it.write(code)
            }
        }

        return emptyList()
    }

    private class ImportDataSchemaStatement(
        val origin: KSFile,
        val name: String,
        val data: URL,
        val visibility: MarkerVisibility,
        val normalizationDelimiters: List<Char>,
        val withDefaultPath: Boolean,
        val csvOptions: CsvOptions
    )

    private fun ImportDataSchema.toStatement(file: KSFile): ImportDataSchemaStatement? {
        val url = try {
            URL(this.url)
        } catch (exception: MalformedURLException) {
            logger.error("'${this.url}' is not valid URL: ${exception.message}", file)
            return null
        }
        return ImportDataSchemaStatement(
            file,
            name,
            url,
            visibility.toMarkerVisibility(),
            normalizationDelimiters.toList(),
            withDefaultPath,
            CsvOptions(csvOptions.delimiter)
        )
    }

    private fun ImportDataSchemaByAbsolutePath.toStatement(file: KSFile): ImportDataSchemaStatement? {
        val data = File(absolutePath)
        val url = try {
            data.toURI().toURL()
        } catch (exception: MalformedURLException) {
            logger.error("$absolutePath is not valid URL: ${exception.message}", file)
            return null
        }
        return ImportDataSchemaStatement(
            file,
            name,
            url,
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

    private fun KSClassDeclaration.toDataSchemaDeclarationOrNull(): DataSchemaDeclaration? {
        return when {
             isClassOrInterface() && effectivelyPublicOrInternal() -> {
                DataSchemaDeclaration(
                    this,
                    declarations
                        .filterIsInstance<KSPropertyDeclaration>()
                        .map { KSAnnotatedWithType(it, it.simpleName, it.type) }
                        .toList()
                )
            }
            else -> null
        }
    }

    private fun KSClassDeclaration.isClassOrInterface() = classKind == ClassKind.INTERFACE || classKind == ClassKind.CLASS

    private fun KSClassDeclaration.effectivelyPublicOrInternal(): Boolean {
        return effectivelyPublicOrInternalOrNull(dataSchema = this) != null
    }
    private fun KSDeclaration.effectivelyPublicOrInternalOrNull(dataSchema: KSClassDeclaration): Visibility? {
        val visibility = getVisibility()
        if (visibility !in EXPECTED_VISIBILITIES) {
            val message = buildString {
                append("DataSchema declaration ${dataSchema.nameString} at ${dataSchema.location} should be $EXPECTED_VISIBILITIES")
                if (this@effectivelyPublicOrInternalOrNull != dataSchema) {
                    append(", but it's parent $nameString is $visibility")
                } else {
                    append("but is $visibility")
                }
            }
            logger.error(message)
            return null
        }

        return when (val parentDeclaration = parentDeclaration) {
            null -> visibility
            else -> when (parentDeclaration.effectivelyPublicOrInternalOrNull(dataSchema)) {
                Visibility.PUBLIC -> visibility
                Visibility.INTERNAL -> Visibility.INTERNAL
                null -> null
                else -> null
            }
        }
    }

    private val KSDeclaration.nameString get() = (qualifiedName ?: simpleName).asString()

    private class DataSchemaDeclaration(
        val origin: KSClassDeclaration,
        val properties: List<KSAnnotatedWithType>
    )

    private class KSAnnotatedWithType(
        private val declaration: KSAnnotated,
        val simpleName: KSName,
        val type: KSTypeReference
    ) : KSAnnotated by declaration

    private fun generate(file: KSFile, dataSchema: KSClassDeclaration, properties: List<KSAnnotatedWithType>) {
        val packageName = file.packageName.asString()
        val fileName = getFileName(dataSchema)
        val generatedFile = codeGenerator.createNewFile(Dependencies(false, file), packageName, fileName)
        try {
            generatedFile.writer().use {
                it.appendLine("""@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")""")
                if (packageName.isNotEmpty()) {
                    it.appendLine("package $packageName")
                }
                it.writeImports()
                val extensions = renderExtensions(
                    declaration = dataSchema,
                    interfaceName = dataSchema.getQualifiedNameOrThrow(),
                    visibility = getMarkerVisibility(dataSchema),
                    properties.map { property ->
                        Property(getColumnName(property), property.simpleName.asString(), property.type)
                    }
                )
                it.appendLine(extensions)
            }
        } catch (e: IOException) {
            throw IOException("Error writing $fileName generated from declaration at ${file.location}", e)
        }
    }

    private fun OutputStreamWriter.writeImports() {
        appendLine("import org.jetbrains.kotlinx.dataframe.annotations.*")
        appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsContainer")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataColumn")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataFrame")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataRow")
        appendLine("import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup")
        appendLine()
    }

    private fun getFileName(dataSchema: KSClassDeclaration) =
        if (dataSchema.isTopLevel) {
            val simpleName = dataSchema.simpleName.asString()
            "$simpleName${'$'}Extensions"
        } else {
            val fqName = dataSchema.getQualifiedNameOrThrow()
            "${fqName}${'$'}Extensions"
        }

    private val KSDeclaration.isTopLevel get() = parentDeclaration == null

    private fun getMarkerVisibility(dataSchema: KSClassDeclaration) =
        when (val visibility = dataSchema.getVisibility()) {
            Visibility.PUBLIC -> if (dataSchema.modifiers.contains(Modifier.PUBLIC)) {
                MarkerVisibility.EXPLICIT_PUBLIC
            } else {
                MarkerVisibility.IMPLICIT_PUBLIC
            }
            Visibility.INTERNAL -> MarkerVisibility.INTERNAL
            Visibility.PRIVATE, Visibility.PROTECTED, Visibility.LOCAL, Visibility.JAVA_PACKAGE -> {
                error("DataSchema declaration should have $EXPECTED_VISIBILITIES, but was $visibility")
            }
        }

    private fun getColumnName(property: KSAnnotatedWithType): String {
        val columnNameAnnotation =  property.annotations.firstOrNull { annotation ->
            val annotationType = annotation.annotationType
            (annotationType.element as? KSClassifierReference)?.referencedName()
                .let { it == null || it == DataFrameNames.SHORT_COLUMN_NAME }
                && annotationType.resolve().declaration.qualifiedName?.asString() == DataFrameNames.COLUMN_NAME
        }
        return if (columnNameAnnotation != null) {
            when (val arg = columnNameAnnotation.arguments.singleOrNull()) {
                null -> argumentMismatchError(property, columnNameAnnotation.arguments)
                else -> (arg.value as? String) ?: typeMismatchError(property, arg)
            }
        } else {
            property.simpleName.asString()
        }
    }

    private fun typeMismatchError(property: KSAnnotatedWithType, arg: KSValueArgument): Nothing {
        error("Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got ${arg.value}")
    }

    private fun argumentMismatchError(property: KSAnnotatedWithType, args: List<KSValueArgument>): Nothing {
        error("Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got $args")
    }
}
