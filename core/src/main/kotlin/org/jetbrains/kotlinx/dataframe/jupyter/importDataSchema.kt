package org.jetbrains.kotlinx.dataframe.jupyter

import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URL

public class ImportDataSchema(public val url: URL) {
    public constructor(path: String) : this(URL(path))
    public constructor(file: File) : this(file.toURI().toURL())
}

public fun importDataSchema(url: URL): ImportDataSchema = ImportDataSchema(url)
public fun importDataSchema(path: String): ImportDataSchema = ImportDataSchema(path)
public fun importDataSchema(file: File): ImportDataSchema = ImportDataSchema(file)

@Language("kts")
internal val importDataSchema = """
    /** Import the type-only data schema from [url]. */
    fun importDataSchema(url: URL, name: String) {
        val formats = listOf(
            OpenApi(),
        )
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
    fun importDataSchema(path: String, name: String): Unit = importDataSchema(URL(path), name)

    /** Import the type-only data schema from [file]. */
    fun importDataSchema(file: File, name: String): Unit = importDataSchema(file.toURI().toURL(), name)
""".trimIndent()
