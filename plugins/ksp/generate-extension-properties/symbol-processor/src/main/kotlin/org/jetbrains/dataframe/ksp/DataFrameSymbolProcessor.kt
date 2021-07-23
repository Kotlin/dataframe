package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import java.io.OutputStreamWriter

class DataFrameSymbolProcessor(private val codeGenerator: CodeGenerator) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.jetbrains.dataframe.annotations.DataSchema")

        symbols
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == ClassKind.INTERFACE }
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

    private fun OutputStreamWriter.writeProperties(interfaceTypeReference: KSType, properties: List<KSPropertyDeclaration>) {
        properties.forEach {
            appendLine("""
                val org.jetbrains.dataframe.DataFrameBase<$interfaceTypeReference>.`${it.simpleName.asString()}`: org.jetbrains.dataframe.columns.DataColumn<${it.type}> get() = this["${it.simpleName.asString()}"] as org.jetbrains.dataframe.columns.DataColumn<${it.type}>
                val org.jetbrains.dataframe.DataRowBase<$interfaceTypeReference>.`${it.simpleName.asString()}`: ${it.type} get() = this["${it.simpleName.asString()}"] as ${it.type}
            """.trimIndent())
        }
    }

}
