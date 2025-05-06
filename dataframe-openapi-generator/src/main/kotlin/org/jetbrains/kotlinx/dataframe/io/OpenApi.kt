package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.File
import java.io.InputStream

/**
 * Allows for OpenApi type schemas to be converted to [DataSchema] interfaces.
 */
public class OpenApi : SupportedCodeGenerationFormat {

    public fun readCodeForGeneration(
        text: String,
        name: String,
        extensionProperties: Boolean = false,
        generateHelperCompanionObject: Boolean,
    ): List<Code> =
        readOpenApiAsString(
            openApiAsString = text,
            name = name,
            extensionProperties = extensionProperties,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    override fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        generateHelperCompanionObject: Boolean,
    ): List<Code> =
        readOpenApiAsString(
            openApiAsString = stream.bufferedReader().readText(),
            name = name,
            extensionProperties = false,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    public fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        extensionProperties: Boolean,
        generateHelperCompanionObject: Boolean,
    ): List<Code> =
        readOpenApiAsString(
            openApiAsString = stream.bufferedReader().readText(),
            name = name,
            extensionProperties = extensionProperties,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    override fun readCodeForGeneration(file: File, name: String, generateHelperCompanionObject: Boolean): List<Code> =
        readOpenApiAsString(
            openApiAsString = file.readText(),
            name = name,
            extensionProperties = false,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    public fun readCodeForGeneration(
        file: File,
        name: String,
        extensionProperties: Boolean,
        generateHelperCompanionObject: Boolean,
    ): List<Code> {
        val code = readOpenApiAsString(
            openApiAsString = file.readText(),
            name = name,
            extensionProperties = extensionProperties,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )
        return code
    }

    override fun acceptsExtension(ext: String): Boolean = ext in listOf("yaml", "yml", "json")

    // Needed for distinguishing between JSON and OpenAPI JSON
    override fun acceptsSample(sample: SupportedFormatSample): Boolean =
        try {
            when (sample) {
                is SupportedFormatSample.DataString -> isOpenApiStr(sample.sampleData)
                is SupportedFormatSample.DataFile -> isOpenApi(sample.sampleFile)
                is SupportedFormatSample.PathString -> isOpenApi(sample.samplePath)
                is SupportedFormatSample.DataUrl -> isOpenApi(sample.sampleUrl)
            }
        } catch (_: Exception) {
            false
        }

    override val testOrder: Int = 9_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod = DefaultReadOpenApiMethod
}
