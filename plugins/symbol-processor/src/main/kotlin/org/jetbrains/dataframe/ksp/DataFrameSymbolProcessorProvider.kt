package org.jetbrains.dataframe.ksp

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

internal const val DATAFRAME_IMPORTED_SCHEMAS_OUTPUT = "dataframe.importedSchemasOutput"

class DataFrameSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val configuration = DataFrameConfiguration(
            resolutionDir = environment.options["dataframe.resolutionDir"],
            importedSchemasOutput = environment.options[DATAFRAME_IMPORTED_SCHEMAS_OUTPUT],
            experimentalImportSchema = environment.options["dataframe.experimentalImportSchema"].equals(
                "true",
                ignoreCase = true,
            ),
            debug = environment.options["dataframe.debug"].equals("true", ignoreCase = true),
        )
        return DataFrameSymbolProcessor(
            environment.codeGenerator,
            environment.logger,
            configuration,
        )
    }
}

data class DataFrameConfiguration(
    val resolutionDir: String?,
    val importedSchemasOutput: String?,
    val experimentalImportSchema: Boolean,
    val debug: Boolean,
)
