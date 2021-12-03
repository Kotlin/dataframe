package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import java.io.IOException
import java.io.OutputStreamWriter

class DataFrameSymbolProcessor(
    private val codeGenerator: CodeGenerator,
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
                    if (it.typeParameters.isNotEmpty()) {
                        logger.error("@${dataSchemaAnnotation.getShortName()} interface should not have type parameters", it)
                        null
                    } else {
                        it.toDataSchemaDeclarationOrNull()
                    }
                } else {
                    null
                }
            }
            .forEach {
                val file = it.origin.containingFile ?: return@forEach
                generate(file, it.origin, it.properties)
            }

        return emptyList()
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

    val KSDeclaration.nameString get() = (qualifiedName ?: simpleName).asString()

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

    private class DataSchemaDeclaration(
        val origin: KSClassDeclaration,
        val properties: List<KSAnnotatedWithType>
    )

    class KSAnnotatedWithType(
        private val declaration: KSAnnotated,
        val simpleName: KSName,
        val type: KSTypeReference
    ) : KSAnnotated by declaration

    private fun generate(file: KSFile, klass: KSClassDeclaration, properties: List<KSAnnotatedWithType>) {
        val className: String = klass.simpleName.asString()
        val packageName = file.packageName.asString()
        val fileName = if (klass.parentDeclaration == null) {
            "$className${'$'}Extensions"
        } else {
            val name = klass.qualifiedName?.asString() ?: error("@DataSchema declaration at ${klass.location} must have name")
            "${name}${'$'}Extensions"
        }
        val generatedFile = codeGenerator.createNewFile(
            Dependencies(false, file), packageName, fileName
        )
        try {
            generatedFile.writer().use {
                it.appendLine("""@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")""")
                if (packageName.isNotEmpty()) {
                    it.appendLine("package $packageName")
                }
                it.appendLine("import org.jetbrains.kotlinx.dataframe.annotations.*")
                it.appendLine("import org.jetbrains.kotlinx.dataframe.ColumnsContainer")
                it.appendLine("import org.jetbrains.kotlinx.dataframe.DataColumn")
                it.appendLine("import org.jetbrains.kotlinx.dataframe.DataFrame")
                it.appendLine("import org.jetbrains.kotlinx.dataframe.DataRow")
                it.appendLine("import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup")
                it.appendLine()
                val name = klass.qualifiedName ?: error("@DataSchema declaration at ${klass.location} must have name")
                it.writeProperties(klass.asType(emptyList()), name.asString(), properties)
            }
        } catch (e: IOException) {
            throw IOException("Error writing ${fileName} generated from declaration at ${file.location}", e)
        }
    }

    private fun OutputStreamWriter.writeProperties(interfaceType: KSType, interfaceName: String, properties: List<KSAnnotatedWithType>) {
        val visibility = when (val visibility = interfaceType.declaration.getVisibility()) {
            Visibility.PUBLIC -> if (interfaceType.declaration.modifiers.contains(Modifier.PUBLIC)) {
                MarkerVisibility.EXPLICIT_PUBLIC
            } else {
                MarkerVisibility.IMPLICIT_PUBLIC
            }
            Visibility.INTERNAL -> MarkerVisibility.INTERNAL
            Visibility.PRIVATE, Visibility.PROTECTED, Visibility.LOCAL, Visibility.JAVA_PACKAGE -> {
                error("DataSchema declaration should have $EXPECTED_VISIBILITIES, but was $visibility")
            }
        }
        val extensions = renderExtensions(
            interfaceName = interfaceName,
            visibility = visibility,
            properties.map { property -> Property(getColumnName(property), property.simpleName.asString(), render(property.type)) }
        )
        appendLine(extensions)
    }

    private fun render(typeReference: KSTypeReference): RenderedType {
        val type = typeReference.resolve()
        val fqName = type.declaration.qualifiedName?.asString() ?: error("")
        val renderedArguments = if (type.innerArguments.isNotEmpty()) {
            type.innerArguments.joinToString(", ") { render(it) }
        } else {
            null
        }
        return RenderedType(fqName, renderedArguments, type.isMarkedNullable)
    }

    private fun render(typeArgument: KSTypeArgument): String {
        return when (val variance = typeArgument.variance) {
            Variance.STAR -> variance.label
            Variance.INVARIANT -> renderRecursively(typeArgument.type ?: error("typeArgument.type should only be null for Variance.STAR"))
            Variance.COVARIANT, Variance.CONTRAVARIANT -> "${variance.label} ${renderRecursively(typeArgument.type ?: error("typeArgument.type should only be null for Variance.STAR"))}"
        }
    }

    private fun renderRecursively(typeReference: KSTypeReference): String {
        val type = typeReference.resolve()
        val fqName = type.declaration.qualifiedName?.asString() ?: error("")
        return buildString {
            append(fqName)
            if (type.innerArguments.isNotEmpty()) {
                append("<")
                val renderedArguments = type.innerArguments.joinToString(", ") { render(it) }
                append(renderedArguments)
                append(">")
            }
            if (type.isMarkedNullable) {
                append("?")
            }
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
