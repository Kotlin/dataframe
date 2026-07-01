package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.dataset.file.FileFormat
import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.ipc.ArrowReader
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.nio.file.Path
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

public class ArrowFeather : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readArrowFeather(stream, NullabilityOptions.Widening)

    override fun readDataFrame(path: Path, header: List<String>): AnyFrame =
        DataFrame.readArrowFeather(path, NullabilityOptions.Widening)

    override fun acceptsExtension(ext: String): Boolean = ext == "feather"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 50000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadArrowMethod(pathRepresentation)
}

/**
 * [DataFrameReadSource] for [Arrow Feather files][DataFrame.readArrowFeather] (random-access IPC format).
 *
 * Supported source types:
 *  - References: [URL], [Path], [File]
 *  - In-memory: [SeekableByteChannel], [ByteArray], [InputStream], [ArrowReader]
 *
 * Default-accepts the `.feather` extension. To read with no extension hint (e.g., an [InputStream]) pass
 * an [ReadOptions] instance to disambiguate from text formats.
 */
public class ArrowFeatherNEW : DataFrameReadSource {

    public data class ReadOptions(val nullability: NullabilityOptions) : DataFrameReadOptions {
        public companion object {
            public operator fun invoke(
                nullability: NullabilityOptions = NullabilityOptions.Infer,
            ): ReadOptions =
                ReadOptions(
                    nullability = nullability,
                )
        }
    }

    override val supportedReadingTypes: Set<KType> =
        setOf(
            typeOf<URL>(),
            typeOf<Path>(),
            typeOf<File>(),
            typeOf<SeekableByteChannel>(),
            typeOf<ByteArray>(),
            typeOf<InputStream>(),
            typeOf<ArrowReader>(),
        )

    public companion object {
        internal const val EXTENSION: String = "feather"
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        if (sourceInfo.extension?.lowercase()?.equals(EXTENSION) == false) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            val kType = sourceInfo.kType

            // ArrowReader is exclusive; check before more general types.
            if (kType.isSubTypeOf<ArrowReader>()) {
                return@runCatching DataFrame.readArrow(source as ArrowReader, opts.nullability)
            }

            val url: URL? = when {
                kType.isSubTypeOf<URL>() -> source as? URL
                kType.isSubTypeOf<Path>() -> (source as? Path)?.toUri()?.toURL()
                kType.isSubTypeOf<File>() -> (source as? File)?.toPath()?.toUri()?.toURL()
                else -> null
            }
            if (url != null) {
                return@runCatching DataFrame.readArrowFeather(url, opts.nullability)
            }

            return@runCatching when {
                kType.isSubTypeOf<SeekableByteChannel>() ->
                    DataFrame.readArrowFeather(source as SeekableByteChannel, nullability = opts.nullability)

                kType.isSubTypeOf<ByteArray>() ->
                    DataFrame.readArrowFeather(source as ByteArray, opts.nullability)

                kType.isSubTypeOf<InputStream>() ->
                    DataFrame.readArrowFeather(source as InputStream, opts.nullability)

                else -> {
                    // return the exception without throwing it; cheaper
                    @Suppress("RedundantReturnKeyword")
                    return Result.failure(IllegalStateException("Cannot read source of type $kType as Arrow Feather"))
                }
            }
        }

    override val testOrder: Int = 60_000

    override fun toString(): String = "ArrowFeather"
}

public val DataFrameReadOptions.Companion.ArrowFeather:
    org.jetbrains.kotlinx.dataframe.io.ArrowFeatherNEW.ReadOptions.Companion
    get() = org.jetbrains.kotlinx.dataframe.io.ArrowFeatherNEW.ReadOptions.Companion

/**
 * [DataFrameReadSource] for [Arrow IPC streaming files][DataFrame.readArrowIPC].
 *
 * Supported source types:
 *  - References: [URL], [Path], [File]
 *  - In-memory: [InputStream], [ByteArray], [ReadableByteChannel], [ArrowReader]
 *
 * There's no widely-standardized extension for IPC streaming files (`.arrow` is most common but is also
 * used for random-access Feather), so this format accepts the `.arrow` extension. If your `.arrow` file is
 * actually random-access (Feather), prefer [ArrowFeatherNEW] — both formats will match `.arrow`, but
 * [ArrowFeatherNEW] runs first by [testOrder] and a Feather read of a streaming-format file will throw,
 * letting the framework fall through to [ArrowIPC].
 */
public class ArrowIPC : DataFrameReadSource {

    public data class ReadOptions(
        val allocator: RootAllocator,
        val nullability: NullabilityOptions,
    ) : DataFrameReadOptions {
        public companion object {
            public operator fun invoke(
                allocator: RootAllocator = Allocator.ROOT,
                nullability: NullabilityOptions = NullabilityOptions.Infer,
            ): ReadOptions =
                ReadOptions(
                    allocator = allocator,
                    nullability = nullability,
                )
        }
    }

    override val supportedReadingTypes: Set<KType> =
        setOf(
            typeOf<URL>(),
            typeOf<Path>(),
            typeOf<File>(),
            typeOf<InputStream>(),
            typeOf<ByteArray>(),
            typeOf<ReadableByteChannel>(),
            typeOf<ArrowReader>(),
        )

    public companion object {
        internal const val EXTENSION: String = "arrow"
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        if (sourceInfo.extension?.lowercase()?.equals(EXTENSION) == false) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            val kType = sourceInfo.kType

            if (kType.isSubTypeOf<ArrowReader>()) {
                return@runCatching DataFrame.readArrow(source as ArrowReader, opts.nullability)
            }

            val url: URL? = when {
                kType.isSubTypeOf<URL>() -> source as? URL
                kType.isSubTypeOf<Path>() -> (source as? Path)?.toUri()?.toURL()
                kType.isSubTypeOf<File>() -> (source as? File)?.toPath()?.toUri()?.toURL()
                else -> null
            }
            if (url != null) {
                return@runCatching DataFrame.readArrowIPC(url, opts.nullability)
            }

            return@runCatching when {
                kType.isSubTypeOf<ReadableByteChannel>() ->
                    DataFrame.readArrowIPC(source as ReadableByteChannel, opts.allocator, opts.nullability)

                kType.isSubTypeOf<ByteArray>() ->
                    DataFrame.readArrowIPC(source as ByteArray, opts.nullability)

                kType.isSubTypeOf<InputStream>() ->
                    DataFrame.readArrowIPC(source as InputStream, opts.nullability)

                else -> {
                    // return the exception without throwing it; cheaper
                    @Suppress("RedundantReturnKeyword")
                    return Result.failure(IllegalStateException("Cannot read source of type $kType as Arrow IPC"))
                }
            }
        }

    // Runs after ArrowFeatherNEW so that `.feather` files get the random-access reader first.
    // Both accept `.arrow`; if Feather reading throws on an IPC streaming file the framework falls
    // through to here.
    override val testOrder: Int = 60_100

    override fun toString(): String = "ArrowIPC"
}

public val DataFrameReadOptions.Companion.ArrowIPC: org.jetbrains.kotlinx.dataframe.io.ArrowIPC.ReadOptions.Companion
    get() = org.jetbrains.kotlinx.dataframe.io.ArrowIPC.ReadOptions.Companion

/**
 * [DataFrameReadSource] for Apache Parquet files (read via Arrow Dataset).
 *
 * Arrow Dataset only consumes URIs, so only reference-style sources are supported:
 *  - References: [URL], [Path], [File]
 *
 * TODO? Multi-file Parquet datasets (vararg in [DataFrame.readParquet]) aren't covered by this single-source API;
 * use [DataFrame.readParquet] directly for those.
 */
public class Parquet : DataFrameReadSource {

    public data class ReadOptions(
        val nullability: NullabilityOptions,
        val batchSize: Long,
    ) : DataFrameReadOptions {
        public companion object {
            public operator fun invoke(
                nullability: NullabilityOptions = NullabilityOptions.Infer,
                batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
            ): ReadOptions =
                ReadOptions(
                    nullability = nullability,
                    batchSize = batchSize,
                )
        }
    }

    override val supportedReadingTypes: Set<KType> =
        setOf(typeOf<URL>(), typeOf<Path>(), typeOf<File>())

    public companion object {
        internal const val EXTENSION: String = "parquet"
        internal val MIME_TYPES = setOf(
            "application/x-parquet",
            "application/parquet",
        )
    }

    override fun acceptsSource(sourceInfo: DataSourceInfo, options: DataFrameReadOptions?): Boolean {
        if (options != null && options !is ReadOptions) return false
        if (sourceInfo.extension?.lowercase()?.equals(EXTENSION) == false) return false
        if (sourceInfo.mimeType != null && sourceInfo.mimeType !in MIME_TYPES) return false
        return supportedReadingTypes.any { sourceInfo.kType.isSubtypeOf(it) }
    }

    override fun readDataFrame(
        source: Any,
        sourceInfo: DataSourceInfo,
        options: DataFrameReadOptions?,
    ): Result<DataFrame<*>> =
        runCatching {
            val opts = (options ?: ReadOptions()) as ReadOptions
            val kType = sourceInfo.kType
            return@runCatching when {
                kType.isSubTypeOf<URL>() ->
                    DataFrame.readParquet(
                        source as URL,
                        nullability = opts.nullability,
                        batchSize = opts.batchSize,
                    )

                kType.isSubTypeOf<Path>() ->
                    DataFrame.readParquet(
                        source as Path,
                        nullability = opts.nullability,
                        batchSize = opts.batchSize,
                    )

                kType.isSubTypeOf<File>() ->
                    DataFrame.readParquet(
                        source as File,
                        nullability = opts.nullability,
                        batchSize = opts.batchSize,
                    )

                else -> {
                    // return the exception without throwing it; cheaper
                    @Suppress("RedundantReturnKeyword")
                    return Result.failure(IllegalStateException("Cannot read source of type $kType as Parquet"))
                }
            }
        }

    override val testOrder: Int = 60_500

    override fun toString(): String = "Parquet"
}

public val DataFrameReadOptions.Companion.Parquet: org.jetbrains.kotlinx.dataframe.io.Parquet.ReadOptions.Companion
    get() = org.jetbrains.kotlinx.dataframe.io.Parquet.ReadOptions.Companion

private inline fun <reified T> KType.isSubTypeOf(): Boolean = this.isSubtypeOf(typeOf<T>())

private const val READ_ARROW_FEATHER = "readArrowFeather"

internal const val ARROW_PARQUET_DEFAULT_BATCH_SIZE = 32768L

private class DefaultReadArrowMethod(path: String?) :
    AbstractDefaultReadMethod(path, MethodArguments.EMPTY, READ_ARROW_FEATHER)

internal object Allocator {
    val ROOT by lazy {
        RootAllocator(Long.MAX_VALUE)
    }
}

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [channel]
 */
public fun DataFrame.Companion.readArrowIPC(
    channel: ReadableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowIPCImpl(channel, allocator, nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [channel]
 */
public fun DataFrame.Companion.readArrowFeather(
    channel: SeekableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeatherImpl(channel, allocator, nullability)

// IPC reading block

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [file]
 */
public fun DataFrame.Companion.readArrowIPC(
    file: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowIPC(file.toPath(), nullability)

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format)
 * data from existing file on the given [path].
 */
public fun DataFrame.Companion.readArrowIPC(
    path: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Files.newByteChannel(path).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [byteArray]
 */
public fun DataFrame.Companion.readArrowIPC(
    byteArray: ByteArray,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = SeekableInMemoryByteChannel(byteArray).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [stream]
 */
public fun DataFrame.Companion.readArrowIPC(
    stream: InputStream,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Channels.newChannel(stream).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [url]
 */
public fun DataFrame.Companion.readArrowIPC(
    url: URL,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame =
    when {
        isFile(url) -> readArrowIPC(urlAsFile(url), nullability)

        isProtocolSupported(url) -> url.openStream().use { readArrowIPC(it, nullability) }

        else -> {
            throw IllegalArgumentException("Invalid protocol for url $url")
        }
    }

public fun DataFrame.Companion.readArrowIPC(
    path: String,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame =
    if (isUrl(path)) {
        readArrowIPC(URI(path).toURL(), nullability)
    } else {
        readArrowIPC(File(path), nullability)
    }

// Feather reading block

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [file]
 */
public fun DataFrame.Companion.readArrowFeather(
    file: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeather(file.toPath(), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files)
 * data from an existing file on the given [path].
 */
public fun DataFrame.Companion.readArrowFeather(
    path: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Files.newByteChannel(path).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [byteArray]
 */
public fun DataFrame.Companion.readArrowFeather(
    byteArray: ByteArray,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = SeekableInMemoryByteChannel(byteArray).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [stream]
 */
public fun DataFrame.Companion.readArrowFeather(
    stream: InputStream,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeather(stream.readBytes(), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [url]
 */
public fun DataFrame.Companion.readArrowFeather(
    url: URL,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame =
    when {
        isFile(url) -> readArrowFeather(urlAsFile(url), nullability)

        isProtocolSupported(url) -> readArrowFeather(url.readBytes(), nullability)

        else -> {
            throw IllegalArgumentException("Invalid protocol for url $url")
        }
    }

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [path]
 */
public fun DataFrame.Companion.readArrowFeather(
    path: String,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame =
    if (isUrl(path)) {
        readArrowFeather(URI(path).toURL(), nullability)
    } else {
        readArrowFeather(File(path), nullability)
    }

/**
 * Read [Arrow any format](https://arrow.apache.org/docs/java/ipc.html#reading-writing-ipc-formats) data from existing [reader]
 */
public fun DataFrame.Companion.readArrow(
    reader: ArrowReader,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowImpl(reader, nullability)

/**
 * Read [Arrow any format](https://arrow.apache.org/docs/java/ipc.html#reading-writing-ipc-formats) data from existing [ArrowReader]
 */
public fun ArrowReader.toDataFrame(nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
    DataFrame.Companion.readArrowImpl(this, nullability)

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [urls] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 */
public fun DataFrame.Companion.readParquet(
    vararg urls: URL,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame =
    readArrowDatasetImpl(
        urls.map {
            it.toString()
        }.toTypedArray(),
        FileFormat.PARQUET,
        nullability,
        batchSize,
    )

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [strUrls] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 */
public fun DataFrame.Companion.readParquet(
    vararg strUrls: String,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame = readArrowDatasetImpl(arrayOf(*strUrls), FileFormat.PARQUET, nullability, batchSize)

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [paths] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 */
public fun DataFrame.Companion.readParquet(
    vararg paths: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame =
    readArrowDatasetImpl(
        paths.map {
            it.toUri().toString()
        }.toTypedArray(),
        FileFormat.PARQUET,
        nullability,
        batchSize,
    )

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [files] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 */
public fun DataFrame.Companion.readParquet(
    vararg files: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame =
    readArrowDatasetImpl(
        files.map {
            it.toURI().toString()
        }.toTypedArray(),
        FileFormat.PARQUET,
        nullability,
        batchSize,
    )
