package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.innerArguments
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class DataFrameSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dataSchemaAnnotation = resolver.getKSNameFromString("org.jetbrains.dataframe.annotations.DataSchema")
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
        val generatedFile = codeGenerator.createNewFile(
            Dependencies(false, file), file.packageName.asString(), "$className${'$'}Extensions")
        generatedFile.writer().use {
            it.appendLine("""@file:Suppress("UNCHECKED_CAST")""")
            val packageName = file.packageName.asString()
            if (packageName.isNotEmpty()) {
                it.appendLine("package $packageName")
            }
            it.appendLine()
            it.writeProperties(klass.asType(emptyList()), properties)
        }
    }

    private fun OutputStreamWriter.writeProperties(interfaceType: KSType, properties: List<KSPropertyDeclaration>) {
        val extensions = renderExtensions(
            interfaceName = interfaceType.toString(),
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
                .let { it == null || it == "ColumnName" }
                && annotationType.resolve().declaration.qualifiedName?.asString() == "org.jetbrains.dataframe.annotations.ColumnName"
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
