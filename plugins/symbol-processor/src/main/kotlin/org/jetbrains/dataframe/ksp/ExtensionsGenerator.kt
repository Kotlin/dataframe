package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import com.google.devtools.ksp.validate
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import java.io.IOException
import java.io.OutputStreamWriter

class ExtensionsGenerator(
    private val resolver: Resolver,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) {
    private companion object {
        val EXPECTED_VISIBILITIES = setOf(Visibility.PUBLIC, Visibility.INTERNAL)
    }

    fun resolveDataSchemaDeclarations(): Pair<Sequence<DataSchemaDeclaration>, List<KSClassDeclaration>> {
        val dataSchemaAnnotation = resolver.getKSNameFromString(DataFrameNames.DATA_SCHEMA)
        val symbols = resolver.getSymbolsWithAnnotation(dataSchemaAnnotation.asString())

        val (validDeclarations, invalidDeclarations) = symbols
            .filterIsInstance<KSClassDeclaration>()
            .partition { it.validate() }

        val preprocessedDeclarations = validDeclarations
            .asSequence()
            .mapNotNull { it.toDataSchemaDeclarationOrNull() }

        return Pair(preprocessedDeclarations, invalidDeclarations)
    }

    class DataSchemaDeclaration(val origin: KSClassDeclaration, val properties: List<KSAnnotatedWithType>)

    class KSAnnotatedWithType(
        private val declaration: KSAnnotated,
        val simpleName: KSName,
        val type: KSTypeReference,
    ) : KSAnnotated by declaration

    private fun KSClassDeclaration.toDataSchemaDeclarationOrNull(): DataSchemaDeclaration? =
        when {
            isClassOrInterface() && effectivelyPublicOrInternal() -> {
                DataSchemaDeclaration(
                    origin = this,
                    properties = getAllProperties()
                        .map { KSAnnotatedWithType(it, it.simpleName, it.type) }
                        .toList(),
                )
            }

            else -> null
        }

    private fun KSClassDeclaration.isClassOrInterface() =
        classKind == ClassKind.INTERFACE || classKind == ClassKind.CLASS

    private fun KSClassDeclaration.effectivelyPublicOrInternal(): Boolean =
        effectivelyPublicOrInternalOrNull(dataSchema = this) != null

    private fun KSDeclaration.effectivelyPublicOrInternalOrNull(dataSchema: KSClassDeclaration): Visibility? {
        val visibility = getVisibility()
        if (visibility !in EXPECTED_VISIBILITIES) {
            val message = buildString {
                append(
                    "DataSchema declaration ${dataSchema.nameString} at ${dataSchema.location} should be $EXPECTED_VISIBILITIES",
                )
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

    fun generateExtensions(file: KSFile, dataSchema: KSClassDeclaration, properties: List<KSAnnotatedWithType>) {
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
                    properties = properties.map { property ->
                        Property(getColumnName(property), property.simpleName.asString(), property.type)
                    },
                )
                it.appendLine(extensions)
            }
        } catch (e: IOException) {
            throw IOException("Error writing $fileName generated from declaration at ${file.location}", e)
        }
    }

    private fun OutputStreamWriter.writeImports() {
        appendLine("import org.jetbrains.kotlinx.dataframe.annotations.*")
        appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsScope")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataColumn")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataFrame")
        appendLine("import org.jetbrains.kotlinx.dataframe.DataRow")
        appendLine("import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup")
        appendLine()
    }

    private fun getFileName(dataSchema: KSClassDeclaration, suffix: String = "Extensions") =
        if (dataSchema.isTopLevel) {
            val simpleName = dataSchema.simpleName.asString()
            "$simpleName${'$'}$suffix"
        } else {
            val fqName = dataSchema.getQualifiedNameOrThrow()
            "${fqName}${'$'}$suffix"
        }

    private val KSDeclaration.isTopLevel get() = parentDeclaration == null

    private fun getMarkerVisibility(dataSchema: KSClassDeclaration) =
        when (val visibility = dataSchema.getVisibility()) {
            Visibility.PUBLIC ->
                if (dataSchema.modifiers.contains(Modifier.PUBLIC)) {
                    MarkerVisibility.EXPLICIT_PUBLIC
                } else {
                    MarkerVisibility.IMPLICIT_PUBLIC
                }

            Visibility.INTERNAL ->
                MarkerVisibility.INTERNAL

            Visibility.PRIVATE, Visibility.PROTECTED, Visibility.LOCAL, Visibility.JAVA_PACKAGE ->
                error("DataSchema declaration should have $EXPECTED_VISIBILITIES, but was $visibility")
        }

    private fun getColumnName(property: KSAnnotatedWithType): String {
        val columnNameAnnotation = property.annotations.firstOrNull { annotation ->
            val annotationType = annotation.annotationType

            val typeIsColumnNameOrNull = (annotationType.element as? KSClassifierReference)
                ?.referencedName()
                ?.let { it == DataFrameNames.SHORT_COLUMN_NAME } != false

            val declarationIsColumnName = annotationType
                .resolve()
                .declaration
                .qualifiedName
                ?.asString() == DataFrameNames.COLUMN_NAME

            typeIsColumnNameOrNull && declarationIsColumnName
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
        error(
            "Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got ${arg.value}",
        )
    }

    private fun argumentMismatchError(property: KSAnnotatedWithType, args: List<KSValueArgument>): Nothing {
        error(
            "Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got $args",
        )
    }
}
