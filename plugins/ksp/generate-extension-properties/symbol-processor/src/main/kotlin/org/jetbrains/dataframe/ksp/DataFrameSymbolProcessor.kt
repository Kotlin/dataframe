package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter

class DataFrameSymbolProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.jetbrains.dataframe.annotations.DataSchema")

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE && it.validate() }
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
            it.appendLine("package ${file.packageName.asString()}")
            it.appendLine()
            it.writeProperties(klass.asType(emptyList()), properties)
        }
    }

    private fun OutputStreamWriter.writeProperties(interfaceType: KSType, properties: List<KSPropertyDeclaration>) {
        properties.forEach { property ->
            val declaration = property.type.resolve().declaration
            val fqnType = declaration.qualifiedName?.asString()
                ?: error("Missing qualifiedName for declaration ${declaration.simpleName}")
            val propertyName = property.simpleName.asString()
            val columnName = propertyName
            appendLine(
                """
                val $fqnDataFrameBase<$interfaceType>.`$propertyName`: $fqnDataColumn<${fqnType}> get() = this["$columnName"] as $fqnDataColumn<${fqnType}>
                val $fqnDataRowBase<$interfaceType>.`$propertyName`: $fqnType get() = this["$columnName"] as $fqnType
                """.trimIndent()
            )
        }
    }

    private companion object {
        private const val fqnDataFrameBase = "org.jetbrains.dataframe.DataFrameBase"
        private const val fqnDataRowBase = "org.jetbrains.dataframe.DataRowBase"
        private const val fqnDataColumn = "org.jetbrains.dataframe.columns.DataColumn"
    }
}
