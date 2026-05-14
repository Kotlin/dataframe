package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readSource
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.util.ServiceLoader
import kotlin.io.extension
import kotlin.io.path.extension
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public interface DataFrameReadOptions

public interface DataFrameReadSource {
    public fun readDataFrameOrNull(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions? = null,
    ): DataFrame<*>?

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
    // TODO, Apache Tika?
    public val mimeType: String? = null,
)

/**
 * NOTE: Needs to have fully qualified name in
 * resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.DataFrameReadSource
 * to be detected here.
 */
internal val newSupportedFormats: List<DataFrameReadSource> by lazy {
    ServiceLoader.load(DataFrameReadSource::class.java)
        .toList()
        .distinct()
        .sortedBy { it.testOrder }
}

internal fun readDataFrameImpl(
    source: Any,
    sourceInfo: DataSourceInfo,
    options: DataFrameReadOptions? = null,
    formats: List<DataFrameReadSource> = newSupportedFormats,
): AnyFrame {
    if (source is String) {
        val url = asUrlOrNull(source)
        if (url != null) {
            return readDataFrameImpl(
                source = url,
                sourceInfo = sourceInfo.copy(
                    kType = typeOf<URL>(),
                ),
                options = options,
                formats = formats,
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

    val tries = mutableMapOf<String, Throwable>()
    formats.sortedBy { it.testOrder }.forEach {
        if (!it.acceptsSource(sourceInfo, options)) return@forEach
        try {
            val df = it.readDataFrameOrNull(getSource(), sourceInfo, options)
            if (df != null) return df
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: Exception) {
            tries[it::class.simpleName!!] = e
        }
    }
    throw IllegalArgumentException("Unknown DataFrame source $source, $sourceInfo; Tried $tries")
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
public fun DataFrame.Companion.readSource(source: Any, type: KType, options: DataFrameReadOptions? = null): AnyFrame =
    readDataFrameImpl(
        source = source,
        sourceInfo = DataSourceInfo(
            kType = type.withNullability(false),
            extension = source.extensionOrNull(),
            mimeType = null, // TODO, Apache Tika?
        ),
        options = options,
    )

public inline fun <reified R : Any> DataFrame.Companion.readSource(
    source: R,
    options: DataFrameReadOptions? = null,
): AnyFrame = readSource(source = source, type = typeOf<R>(), options = options)

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
    }

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
