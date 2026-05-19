package org.jetbrains.kotlinx.dataframe.io

import org.apache.tika.detect.DefaultDetector
import org.apache.tika.io.TikaInputStream
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.TikaCoreProperties
import org.apache.tika.mime.MediaType
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.CodeString
import org.jetbrains.kotlinx.dataframe.api.generateInterfaces
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.util.ServiceLoader
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public interface DataFrameReadOptions

public interface DataFrameReadSource {
    /**
     * The set of source [KType]s this format knows how to read. The framework uses this in the default
     * [acceptsSource] implementation, and overriding `acceptsSource` implementations should still consult it
     * so that adding a new supported type only requires updating this set.
     *
     * Note: a `String` *reference* (path/URL) is normalized to a [URL] by `readSourceImpl` before any format
     * is invoked, so only include `String` here when raw text content is a legitimate input (e.g., JSON/CSV
     * text). For binary formats, leave `String` out.
     */
    public val supportedTypes: Set<KType>

    public fun readDataFrameOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions? = null,
    ): DataFrame<*>?

    /**
     * Read just the [DataFrameSchema] for [source].
     *
     * The default implementation reads the full DataFrame and calls [DataFrame.schema]. Override when the
     * source format can introspect types without materializing rows (e.g., JDBC metadata queries, Parquet/Arrow
     * file footers, OpenAPI specs).
     */
    public fun readDataFrameSchemaOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions? = null,
    ): DataFrameSchema? = readDataFrameOrNull(source, sourceInfo, options)?.schema()

    public fun readDataSchemaCodeOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        name: String,
        options: DataFrameReadOptions? = null,
    ): CodeString? =
        readDataFrameSchemaOrNull(source, sourceInfo, options)
            ?.generateInterfaces(name)

    public fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean

    // `DataFrame.Companion.read` methods uses this to sort list of all supported formats in ascending order (-1, 2, 10)
    // sorted list is used to test if any format can read given input
    public val testOrder: Int
}

/**
 * Description of a source passed to [DataFrameReadSource]. Carries the static [kType] of the value and
 * optional [extension]/[mimeType] hints, both of which may be `null` when the source is in-memory content
 * with no reasonable file-extension/MIME interpretation (e.g., a raw [String], [InputStream], [java.sql.Connection],
 * etc.).
 */
public data class DataSourceInfo(
    public val kType: KType,
    public val extension: String? = null,
    public val mimeType: String? = null,
) {
    init {
        if (mimeType != null) {
            println()
        }
    }
}

/**
 * NOTE: Needs to have fully qualified name in
 * resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.DataFrameReadSource
 * to be detected here.
 */
@PublishedApi
internal val newSupportedFormats: List<DataFrameReadSource> by lazy {
    ServiceLoader.load(DataFrameReadSource::class.java)
        .toList()
        .distinct()
        .sortedBy { it.testOrder }
}

internal val dataFrameReadSourceByType: Map<KType, List<DataFrameReadSource>> by lazy {
    buildMap<KType, MutableList<DataFrameReadSource>> {
        newSupportedFormats.forEach { format ->
            format.supportedTypes.forEach { type ->
                getOrPut(type) { mutableListOf() }.let {
                    if (format !in it) it += format
                }

                // special String -> URL case
                if (type == typeOf<URL>()) {
                    getOrPut(typeOf<String>()) { mutableListOf() }.let {
                        if (format !in it) it += format
                    }
                }
            }
        }
        values.forEach {
            it.sortBy { it.testOrder }
        }
    }
}

/**
 * Shared dispatch loop for [readDataFrameImpl] and [readDataFrameSchemaImpl]: handles String→URL
 * normalization, InputStream buffering, sorted iteration, and error aggregation. The per-format read
 * operation is supplied as [readOrNull]; [resultKind] is used only in the "unknown source" error message.
 *
 * @param [readOrNull] [DataFrameReadSource.readDataFrameOrNull] or [DataFrameReadSource.readDataFrameSchemaOrNull]
 *   Potentially, this could also return another type, like a GeoDataFrame.
 */
internal fun <T : Any> readSourceImpl(
    source: Any,
    sourceType: KType,
    options: DataFrameReadOptions?,
    formats: List<DataFrameReadSource>,
    resultKind: String,
    readOrNull: DataFrameReadSource.(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ) -> T?,
): T {
    if (source is String) {
        val url = asUrlOrNull(source)
        if (url != null) {
            return readSourceImpl(
                source = url,
                sourceType = typeOf<URL>(),
                options = options,
                formats = formats,
                resultKind = resultKind,
                readOrNull = readOrNull,
            )
        }
    }

    // Some sources can only be read once, like InputStreams, so we need to buffer them
    var bufferedSource: Any? = null

    fun getSource(): Any =
        when (source) {
            is InputStream -> {
                if (bufferedSource == null) bufferedSource = source.readBytes()
                ByteArrayInputStream(bufferedSource as ByteArray)
            }

            else -> source
        }

    val sourceInfo = DataSourceInfo(
        kType = sourceType,
        extension = getSource().extensionOrNull(),
        mimeType = getSource().mimeTypeOrNull(),
    )

    val tries = mutableMapOf<String, Throwable>()
    formats.sortedBy { it.testOrder }.forEach {
        if (!it.acceptsSource(sourceInfo, options)) return@forEach
        try {
            val result = it.readOrNull(getSource(), sourceInfo, options)
            if (result != null) return result
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: Exception) {
            tries[it::class.simpleName!!] = e
        }
    }
    throw IllegalArgumentException("Unknown $resultKind source $source, $sourceInfo; Tried $tries")
}

/**
 * Unified entry point for the [DataFrameReadSource] framework: passes [source] through every registered
 * format until one reads it.
 *
 * For a [String] that points to an existing file or a recognized URL (`http://`, `https://`, `ftp://`),
 * the source is normalized to a [URL] so the file-extension hint can be used to disambiguate formats. Any
 * other [String] is treated as in-memory content (raw JSON/CSV/etc.).
 *
 * Named [readSource] rather than `read` to avoid shadowing the legacy `DataFrame.read(File/URL/Path/String, header)`
 * entries in `guess.kt` that use the older [SupportedDataFrameFormat] system. Once the legacy entries are
 * retired, this can be renamed to `read`.
 */
public fun DataFrame.Companion.readSource(
    source: Any,
    type: KType,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): AnyFrame =
    readSourceImpl(
        source = source,
        sourceType = type.withNullability(false),
        options = options,
        formats = formats,
        resultKind = "DataFrame",
        readOrNull = DataFrameReadSource::readDataFrameOrNull,
    )

public inline fun <reified R : Any> DataRow.Companion.readSource(
    source: R,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): AnyRow = readSource(source = source, type = typeOf<R>(), options = options, formats = formats)

public fun DataRow.Companion.readSource(
    source: Any,
    type: KType,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): AnyRow =
    readSourceImpl(
        source = source,
        sourceType = type.withNullability(false),
        options = options,
        formats = formats,
        resultKind = "DataRow",
        readOrNull = { source, sourceInfo, options ->
            readDataFrameOrNull(source, sourceInfo, options)?.single()
        },
    )

public inline fun <reified R : Any> DataFrame.Companion.readSource(
    source: R,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): AnyFrame =
    readSource(
        source = source,
        type = typeOf<R>(),
        options = options,
        formats = formats,
    )

/**
 * Schema-only counterpart of [DataFrame.Companion.readSource]: dispatches through every registered
 * [DataFrameReadSource] and returns the resulting [DataFrameSchema] without materializing rows when the
 * format supports it (e.g., JDBC). Formats with no fast schema path fall back to reading the full DataFrame
 * and calling [DataFrame.schema].
 */
public fun DataFrameSchema.Companion.readSource(
    source: Any,
    type: KType,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): DataFrameSchema =
    readSourceImpl(
        source = source,
        sourceType = type.withNullability(false),
        options = options,
        formats = formats,
        resultKind = "DataFrameSchema",
        readOrNull = DataFrameReadSource::readDataFrameSchemaOrNull,
    )

public inline fun <reified R : Any> DataFrameSchema.Companion.readSource(
    source: R,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): DataFrameSchema =
    readSource(
        source = source,
        type = typeOf<R>(),
        options = options,
        formats = formats,
    )

/**
 * Code-generation counterpart of [DataFrame.Companion.readSource]: dispatches through every registered
 * [DataFrameReadSource] and returns a [CodeString] containing the generated `@DataSchema` interface
 * declarations (plus enums/typealiases for formats like OpenAPI). The [name] is the marker name used for
 * the top-level generated interface.
 *
 * The default implementation in [DataFrameReadSource.readDataSchemaCodeOrNull] runs
 * [DataFrameSchema.generateInterfaces] on the format's [DataFrameReadSource.readDataFrameSchemaOrNull]
 * result; formats that produce richer code (OpenAPI markers, enums, typealiases) override the method
 * directly.
 */
public fun CodeString.Companion.readSource(
    source: Any,
    type: KType,
    name: String,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): CodeString =
    readSourceImpl(
        source = source,
        sourceType = type.withNullability(false),
        options = options,
        formats = formats,
        resultKind = "CodeString",
        readOrNull = { src, info, opts ->
            readDataSchemaCodeOrNull(src, info, name, opts)
        },
    )

public inline fun <reified R : Any> CodeString.Companion.readSource(
    source: R,
    name: String,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): CodeString =
    readSource(
        source = source,
        type = typeOf<R>(),
        name = name,
        options = options,
        formats = formats,
    )

private val tikaDetector by lazy { DefaultDetector() }

internal fun Any.mimeTypeOrNull(): String? {
    val inputStream = try {
        when (this) {
            is Path -> TikaInputStream.get(this)

            is File ->
                @Suppress("DEPRECATION")
                TikaInputStream.get(this)

            is URL -> TikaInputStream.get(this)

            is InputStream -> TikaInputStream.get(this)

            is ByteArray -> TikaInputStream.get(this)

            else -> null
        }
    } catch (_: IOException) {
        null
    } ?: return null

    val metadata = Metadata().apply {
        if (inputStream.hasFile()) {
            add(TikaCoreProperties.RESOURCE_NAME_KEY, inputStream.path.name)
        }
    }
    return try {
        val detected = tikaDetector.detect(inputStream, metadata)
        return when {
            detected == MediaType.OCTET_STREAM -> null
            detected == MediaType.TEXT_PLAIN -> null
            detected == MediaType.EMPTY -> null
            detected.toString().isEmpty() -> null
            else -> detected.toString()
        }
    } catch (_: IOException) {
        null
    }
}

internal fun Any.extensionOrNull(): String? =
    when (this) {
        is Path -> extension

        is File -> extension

        is URL -> path.takeIf { it.isNotBlank() }?.substringAfterLast('.')

        is String -> try {
            asUrl(this).extensionOrNull()
        } catch (_: Exception) {
            null
        }

        else -> null
    }?.lowercase()

/**
 * Non-throwing variant of [asUrl]: returns the [URL] iff [string] is a recognized URL (`http`/`https`/`ftp`)
 * or an existing file path. Used by [readSource] to decide whether a [String] should be treated as a reference
 * or as raw content.
 */
internal fun asUrlOrNull(string: String): URL? =
    when {
        isUrl(string) -> try {
            URI(string).toURL()
        } catch (_: Exception) {
            null
        }

        else -> {
            val file = try {
                File(string)
            } catch (_: Exception) {
                null
            }
            if (file != null && file.exists() && file.isFile) {
                file.toURI().toURL()
            } else {
                null
            }
        }
    }
