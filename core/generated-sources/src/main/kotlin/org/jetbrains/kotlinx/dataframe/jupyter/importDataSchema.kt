package org.jetbrains.kotlinx.dataframe.jupyter

import org.intellij.lang.annotations.Language
import java.net.URI
import java.net.URL
import java.nio.file.Path

public class ImportDataSchema(public val url: URL) {
    public constructor(path: String) : this(URI(path).toURL())
    public constructor(path: Path) : this(path.toUri().toURL())
}

public fun importDataSchema(url: URL): ImportDataSchema = ImportDataSchema(url)

public fun importDataSchema(path: String): ImportDataSchema = ImportDataSchema(path)

public fun importDataSchema(path: Path): ImportDataSchema = ImportDataSchema(path)

@Language("kts")
internal val importDataSchema =
    """
    /** Import the type-only data schema from [url]. */
    fun importDataSchema(url: URL, name: String) {
        val formats = listOfNotNull(
            if (dataFrameConfig.enableExperimentalOpenApi) OpenApi() else null,
        )
        
        require(formats.isNotEmpty()) { 
            "importDataSchema() did not find any supported type-only data schema generation providers (`SupportedCodeGenerationFormat`). If you were looking for OpenAPI 3.0.0 types, set `%use dataframe(..., enableExperimentalOpenApi=true)`." 
        }   
        
        val codeGenResult = org.jetbrains.dataframe.impl.codeGen.CodeGenerator.urlCodeGenReader(url, formats)
        when (codeGenResult) {
            is org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult.Success -> {
                val readDfMethod = codeGenResult.getReadDfMethod(url.toExternalForm())
                val code = readDfMethod.additionalImports.joinToString("\n") + 
                    "\n" + 
                    codeGenResult.code.converter(name)

                EXECUTE(code)
                DISPLAY("Data schema successfully imported as ${'$'}name")
            }

            is org.jetbrains.kotlinx.dataframe.impl.codeGen.CodeGenerationReadResult.Error -> {
                DISPLAY("Failed to read data schema from ${'$'}url: ${'$'}{codeGenResult.reason}")
            }
        }
    }

    /** Import the type-only data schema from [path]. */
    fun importDataSchema(path: String, name: String): Unit = importDataSchema(URI(path).toURL(), name)

    /** Import the type-only data schema from [file]. */
    fun importDataSchema(file: File, name: String): Unit = importDataSchema(file.toURI().toURL(), name)
    """.trimIndent()
