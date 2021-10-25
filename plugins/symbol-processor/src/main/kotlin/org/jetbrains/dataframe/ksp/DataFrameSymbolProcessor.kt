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
        val EXPECTED_VISIBILITIES = listOf(Visibility.PUBLIC, Visibility.INTERNAL)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dataSchemaAnnotation = resolver.getKSNameFromString(DataFrameNames.DATA_SCHEMA)
        val symbols = resolver.getSymbolsWithAnnotation(dataSchemaAnnotation.asString())

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull {
                if (it.classKind == ClassKind.INTERFACE && it.validate()) {
                    if (it.typeParameters.isNotEmpty()) {
                        logger.error("@${dataSchemaAnnotation.getShortName()} interface should not have type parameters", it)
                        null
                    } else {
                        it
                    }
                } else {
                    null
                }
            }
            .forEach {
                val file = it.containingFile ?: return@forEach
                generate(file, it, it.declarations.filterIsInstance<KSPropertyDeclaration>().toList())
            }

        return emptyList()
    }

    private fun generate(file: KSFile, klass: KSClassDeclaration, properties: List<KSPropertyDeclaration>) {
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
                it.appendLine("""@file:Suppress("UNCHECKED_CAST")""")
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

    private fun OutputStreamWriter.writeProperties(interfaceType: KSType, interfaceName: String, properties: List<KSPropertyDeclaration>) {
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

    private fun getColumnName(property: KSPropertyDeclaration): String {
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

    private fun typeMismatchError(property: KSPropertyDeclaration, arg: KSValueArgument): Nothing {
        error("Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got ${arg.value}")
    }

    private fun argumentMismatchError(property: KSPropertyDeclaration, args: List<KSValueArgument>): Nothing {
        error("Expected one argument of type String in annotation ColumnName on property ${property.simpleName}, but got $args")
    }
}
