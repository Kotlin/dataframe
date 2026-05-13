package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import java.io.File
import java.io.FileNotFoundException
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

public data class DataSourceInfo(
    public val type: DataSourceType,
    public val extension: String? = null,
    // TODO, Apache Tika?
    public val mimeType: String? = null,
)

public sealed class DataSourceType(public open val kType: KType) {
    /** Like a path, file, or URL. */
    public data class Reference(override val kType: KType) : DataSourceType(kType)

    /** Actual data, like a String, ByteArray, InputStream */
    public data class InMemory(override val kType: KType) : DataSourceType(kType)

    public companion object {
        public inline fun <reified T> reference(): Reference = Reference(kType = typeOf<T>())

        public inline fun <reified T> inMemory(): InMemory = InMemory(kType = typeOf<T>())
    }
}

/**
 * NOTE: Needs to have fully qualified name in
 * resources/META-INF/services/org.jetbrains.kotlinx.dataframe.io.NewSupportedDataFrameFormat
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
    val tries = mutableMapOf<String, Throwable>()
    formats.sortedBy { it.testOrder }.forEach {
        if (!it.acceptsSource(sourceInfo, options)) return@forEach
        try {
            val df = it.readDataFrameOrNull(source, sourceInfo, options)
            if (df != null) return df
        } catch (e: FileNotFoundException) {
            throw e
        } catch (e: Exception) {
            tries[it::class.simpleName!!] = e
        }
    }
    throw IllegalArgumentException("Unknown DataFrame source $source, $sourceInfo; Tried $tries")
}

public fun DataFrame.Companion.readReference(
    reference: Any,
    type: KType,
    options: DataFrameReadOptions? = null,
): AnyFrame =
    readDataFrameImpl(
        source = reference,
        sourceInfo = DataSourceInfo(
            type = DataSourceType.Reference(type.withNullability(false)),
            extension = reference.extensionOrNull(),
            mimeType = null, // TODO, Apache Tika?
        ),
        options = options,
    )

public inline fun <reified R : Any> DataFrame.Companion.readReference(
    reference: R,
    options: DataFrameReadOptions? = null,
): AnyFrame =
    readReference(
        reference = reference,
        type = typeOf<R>(),
        options = options,
    )

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

public fun DataFrame.Companion.readFromData(data: Any, type: KType, options: DataFrameReadOptions? = null): AnyFrame =
    readDataFrameImpl(
        source = data,
        sourceInfo = DataSourceInfo(
            type = DataSourceType.InMemory(type.withNullability(false)),
            mimeType = null, // TODO, Apache Tika?
        ),
        options = options,
    )

public inline fun <reified R : Any> DataFrame.Companion.readFromData(
    data: R,
    options: DataFrameReadOptions? = null,
): AnyFrame =
    readFromData(
        data = data,
        type = typeOf<R>(),
        options = options,
    )
