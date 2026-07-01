package org.jetbrains.kotlinx.dataframe.io

import io.swagger.v3.parser.core.models.AuthorizationValue
import io.swagger.v3.parser.core.models.ParseOptions
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.codeGen.Code
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

/**
 * [DataFrameReadSource] for OpenAPI specifications.
 *
 * OpenAPI doesn't produce a `DataFrame` or a single `DataFrameSchema` — its output is a multi-marker code
 * blob (interfaces + enums + typealiases). Only [readDataSchemaCode] is overridden; the DataFrame
 * and Schema methods return a failed [Result], so calling `DataFrame.readSource(openapiFile)` falls
 * through to JSON, while `CodeString.readSource(openapiFile, name)` dispatches here.
 *
 * `.yaml`/`.yml` files are unambiguously OpenAPI; `.json` files are disambiguated at read time by
 * [isOpenApiStr] failing early when the JSON isn't actually an OpenAPI spec, letting the framework
 * fall through to the JSON format for plain data.
 */
public class OpenApi2 : DataFrameReadSource {

    public data class ReadOptions(
        val auth: List<AuthorizationValue>?,
        val parseOptions: ParseOptions?,
        val extensionProperties: Boolean,
        val generateHelperCompanionObject: Boolean,
        val visibility: MarkerVisibility,
    ) : DataFrameReadOptions {
        public companion object {
            public operator fun invoke(
                auth: List<AuthorizationValue>? = null,
                parseOptions: ParseOptions? = null,
                extensionProperties: Boolean = false,
                generateHelperCompanionObject: Boolean = false,
                visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
            ): ReadOptions =
                ReadOptions(
                    auth = auth,
                    parseOptions = parseOptions,
                    extensionProperties = extensionProperties,
                    generateHelperCompanionObject = generateHelperCompanionObject,
                    visibility = visibility,
                )
        }
    }

    override val supportedReadingTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>(), typeOf<String>(), typeOf<InputStream>())

    public companion object {
        internal val EXTENSIONS: Set<String> = setOf("yaml", "yml", "json")
        internal val MIME_TYPES = setOf(
            "application/vnd.oai.openapi",
            "application/vnd.oai.openapi+json",
            "application/vnd.oai.openapi.yaml",
            "application/vnd.oai.openapi+yaml",
            "text/x-yaml",
            "text/yaml",
            "application/x-yaml",
            "application/yaml",
            "application/x-json",
            "application/json",
            "text/x-json",
            "text/json",
        )
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        val ext = sourceInfo.extension?.lowercase()
        if (ext != null && ext !in EXTENSIONS) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in MIME_TYPES) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    // OpenAPI doesn't produce a DataFrame.
    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> = Result.failure(UnsupportedOperationException("OpenAPI does not produce a DataFrame"))

    // ...nor a single DataFrameSchema, it can produce enums, typealiases, etc.
    // so it only supports readDataSchemaCode()
    override fun readDataFrameSchema(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrameSchema> =
        Result.failure(UnsupportedOperationException("OpenAPI does not produce a single DataFrameSchema"))

    override fun readDataSchemaCode(
        source: Any,
        sourceInfo: DataSourceInfo,
        name: String,
        options: DataFrameReadOptions?,
    ): Result<CodeString> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            val kType = sourceInfo.kType

            // Resolve to OpenAPI-spec text, returning null if the content isn't OpenAPI.
            val text: String = when {
                kType.isSubtypeOf(typeOf<URL>()) -> {
                    if (!isOpenApi(source as URL)) {
                        return Result.failure(IllegalStateException("URL does not point to an OpenAPI spec"))
                    }
                    source.readText()
                }

                kType.isSubtypeOf(typeOf<Path>()) -> {
                    if (!isOpenApi(source as Path)) {
                        return Result.failure(IllegalStateException("Path does not point to an OpenAPI spec"))
                    }
                    source.readText()
                }

                kType.isSubtypeOf(typeOf<File>()) -> {
                    if (!isOpenApi((source as File).toPath())) {
                        return Result.failure(IllegalStateException("File does not point to an OpenAPI spec"))
                    }
                    source.readText()
                }

                kType.isSubtypeOf(typeOf<String>()) -> {
                    if (!isOpenApiStr(source as String)) {
                        return Result.failure(IllegalStateException("String content is not an OpenAPI spec"))
                    }
                    source
                }

                kType.isSubtypeOf(typeOf<InputStream>()) -> {
                    val text = (source as InputStream).bufferedReader().readText()
                    if (!isOpenApiStr(text)) {
                        return Result.failure(IllegalStateException("InputStream content is not an OpenAPI spec"))
                    }
                    text
                }

                else -> error("Unsupported source type: $kType")
            }

            CodeString(
                readOpenApiAsString(
                    openApiAsString = text,
                    name = name,
                    auth = opts.auth,
                    options = opts.parseOptions,
                    extensionProperties = opts.extensionProperties,
                    generateHelperCompanionObject = opts.generateHelperCompanionObject,
                    visibility = opts.visibility,
                ),
            )
        }

    // Run before Json (10_000) so .json files get the OpenAPI content check first.
    override val testOrder: Int = 9_000

    override fun toString(): String = "OpenApi"
}

public val DataFrameReadOptions.Companion.OpenApi: org.jetbrains.kotlinx.dataframe.io.OpenApi2.ReadOptions.Companion
    get() = org.jetbrains.kotlinx.dataframe.io.OpenApi2.ReadOptions.Companion

/**
 * Allows for OpenApi type schemas to be converted to [DataSchema] interfaces.
 */
public class OpenApi : SupportedCodeGenerationFormat {

    public fun readCodeForGeneration(
        text: String,
        name: String,
        extensionProperties: Boolean = false,
        generateHelperCompanionObject: Boolean,
    ): Code =
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
    ): Code =
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
    ): Code =
        readOpenApiAsString(
            openApiAsString = stream.bufferedReader().readText(),
            name = name,
            extensionProperties = extensionProperties,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    override fun readCodeForGeneration(file: File, name: String, generateHelperCompanionObject: Boolean): Code =
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
    ): Code =
        readOpenApiAsString(
            openApiAsString = file.readText(),
            name = name,
            extensionProperties = extensionProperties,
            generateHelperCompanionObject = generateHelperCompanionObject,
        )

    override fun acceptsExtension(ext: String): Boolean = ext in listOf("yaml", "yml", "json")

    // Needed for distinguishing between JSON and OpenAPI JSON
    override fun acceptsSample(sample: SupportedFormatSample): Boolean =
        try {
            when (sample) {
                is SupportedFormatSample.DataString -> isOpenApiStr(sample.sampleData)
                is SupportedFormatSample.DataPath -> isOpenApi(sample.samplePath)
                is SupportedFormatSample.PathString -> isOpenApi(sample.samplePath)
                is SupportedFormatSample.DataUrl -> isOpenApi(sample.sampleUrl)
                is SupportedFormatSample.DataFile -> isOpenApi(sample.sampleFile.toPath())
            }
        } catch (_: Exception) {
            false
        }

    override val testOrder: Int = 9_000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod = DefaultReadOpenApiMethod
}
