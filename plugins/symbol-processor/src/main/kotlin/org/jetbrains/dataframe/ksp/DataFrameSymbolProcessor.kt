package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class DataFrameSymbolProcessor(
    private val codeGenerator: com.google.devtools.ksp.processing.CodeGenerator,
    private val logger: KSPLogger,
    private val resolutionDir: String?,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val extensionsGenerator = ExtensionsGenerator(resolver, codeGenerator, logger)
        val dataSchemas = extensionsGenerator.resolveValidDataSchemaDeclarations()
        dataSchemas.forEach {
            val file = it.origin.containingFile ?: return@forEach
            extensionsGenerator.generateExtensions(file, it.origin, it.properties)
        }

        val dataSchemaGenerator = DataSchemaGenerator(resolver, resolutionDir, logger, codeGenerator)
        val importStatements = dataSchemaGenerator.resolveImportStatements()
        importStatements.forEach { importStatement ->
            dataSchemaGenerator.generateDataSchema(importStatement)
        }

        return emptyList()
    }
}
