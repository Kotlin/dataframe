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
 * blob (interfaces + enums + typealiases). Only [readDataSchemaCodeOrNull] is overridden; the DataFrame
 * and Schema methods return `null` (via the interface's defaults), so calling
 * `DataFrame.readSource(openapiFile)` falls through to JSON, while `CodeString.readSource(openapiFile, name)`
 * dispatches here.
 *
 * `.yaml`/`.yml` files are unambiguously OpenAPI; `.json` files are disambiguated at read time by
 * [isOpenApiStr] returning null early when the JSON isn't actually an OpenAPI spec, letting the framework
 * fall through to the JSON format for plain data.
 */
public class OpenApi2 : DataFrameReadSource {

    public data class Options(
        val auth: List<AuthorizationValue>? = null,
        val parseOptions: ParseOptions? = null,
        val extensionProperties: Boolean = false,
        val generateHelperCompanionObject: Boolean = false,
        val visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    ) : DataFrameReadOptions

    override val supportedTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>(), typeOf<String>(), typeOf<InputStream>())

    public companion object {
        internal val EXTENSIONS: Set<String> = setOf("yaml", "yml", "json")
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is Options) return false
        val ext = sourceInfo.extension?.lowercase()
        if (ext != null && ext !in EXTENSIONS) return false
        return supportedTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    // OpenAPI doesn't produce a DataFrame.
    override fun readDataFrameOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): DataFrame<*>? = null

    // ...nor a single DataFrameSchema, it can produce enums, typealiases, etc.
    // so it only supports readDataSchemaCodeOrNull()
    override fun readDataFrameSchemaOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): DataFrameSchema? = null

    override fun readDataSchemaCodeOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        name: String,
        options: DataFrameReadOptions?,
    ): CodeString? {
        val opts = (options ?: Options()) as Options
        val kType = sourceInfo.kType

        // Resolve to OpenAPI-spec text, returning null if the content isn't OpenAPI.
        val text: String = when {
            kType.isSubtypeOf(typeOf<URL>()) -> {
                val url = (source as? URL) ?: return null
                if (!isOpenApi(url)) return null
                url.readText()
            }

            kType.isSubtypeOf(typeOf<Path>()) -> {
                val path = (source as? Path) ?: return null
                if (!isOpenApi(path)) return null
                path.readText()
            }

            kType.isSubtypeOf(typeOf<File>()) -> {
                val file = (source as? File) ?: return null
                if (!isOpenApi(file.toPath())) return null
                file.readText()
            }

            kType.isSubtypeOf(typeOf<String>()) -> {
                val text = (source as? String) ?: return null
                if (!isOpenApiStr(text)) return null
                text
            }

            kType.isSubtypeOf(typeOf<InputStream>()) -> {
                val text = (source as? InputStream)?.bufferedReader()?.readText() ?: return null
                if (!isOpenApiStr(text)) return null
                text
            }

            else -> return null
        }

        return CodeString(
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
