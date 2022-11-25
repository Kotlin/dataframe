package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.jupyter.api.Code
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
    ): Code = readOpenApiAsString(
        openApiAsString = text,
        name = name,
        extensionProperties = extensionProperties,
        generateHelperCompanionObject = generateHelperCompanionObject,
    )

    override fun readCodeForGeneration(
        stream: InputStream,
        name: String,
        generateHelperCompanionObject: Boolean,
    ): Code = readOpenApiAsString(
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
    ): Code = readOpenApiAsString(
        openApiAsString = stream.bufferedReader().readText(),
        name = name,
        extensionProperties = extensionProperties,
        generateHelperCompanionObject = generateHelperCompanionObject,
    )

    override fun readCodeForGeneration(
        file: File,
        name: String,
        generateHelperCompanionObject: Boolean,
    ): Code = readOpenApiAsString(
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
    ): Code = readOpenApiAsString(
        openApiAsString = file.readText(),
        name = name,
        extensionProperties = extensionProperties,
        generateHelperCompanionObject = generateHelperCompanionObject,
    )

    override fun acceptsExtension(ext: String): Boolean = ext in listOf("yaml", "yml", "json")

    // Needed for distinguishing between JSON and OpenAPI JSON
    override fun acceptsSample(sample: SupportedFormatSample): Boolean =
        when (sample) {
            is SupportedFormatSample.DataString -> isOpenApiStr(sample.sampleData)
            is SupportedFormatSample.File -> isOpenApi(sample.sampleFile)
            is SupportedFormatSample.PathString -> isOpenApi(sample.samplePath)
            is SupportedFormatSample.URL -> isOpenApi(sample.sampleUrl)
        }

    override val testOrder: Int = 9_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod = DefaultReadOpenApiMethod
}


