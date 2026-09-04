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
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.nio.file.Path

@Deprecated("SupportedDataFrameFormat is deprecated. Will be ERROR in 1.0.", level = DeprecationLevel.ERROR)
@Suppress("DEPRECATION_ERROR")
public class ArrowFeather : SupportedDataFrameFormat {

    @Deprecated("SupportedDataFrameFormat is deprecated. Will be ERROR in 1.0.", level = DeprecationLevel.ERROR)
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame =
        DataFrame.readArrowFeather(stream, NullabilityOptions.Widening)

    @Deprecated("SupportedDataFrameFormat is deprecated. Will be ERROR in 1.0.", level = DeprecationLevel.ERROR)
    override fun readDataFrame(path: Path, header: List<String>): AnyFrame =
        DataFrame.readArrowFeather(path, NullabilityOptions.Widening)

    override fun acceptsExtension(ext: String): Boolean = ext == "feather"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 50000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod =
        DefaultReadArrowMethod(pathRepresentation)
}

private const val READ_ARROW_FEATHER = "readArrowFeather"

/**
 * Number of rows [readParquet] pulls from Arrow Dataset per scan batch, when no `batchSize` is given.
 *
 * Public so that the value can be referenced instead of copied — the documentation samples used to hard-code
 * their own copy of it, which silently drifted out of date.
 */
public const val ARROW_PARQUET_DEFAULT_BATCH_SIZE: Long = 32768L

private class DefaultReadArrowMethod(path: String?) :
    AbstractDefaultReadMethod(path, MethodArguments.EMPTY, READ_ARROW_FEATHER)

internal object Allocator {
    val ROOT by lazy {
        RootAllocator(Long.MAX_VALUE)
    }
}

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [channel]
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s; an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row). See [readParquet] and [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * A `Timestamp(unit, tz)` column identifies a single point on the time-line and is read as
 * [kotlin.time.Instant]; a zone-less `Timestamp(unit, null)` stays [kotlinx.datetime.LocalDateTime]. The zone
 * is display metadata — the stored values are already normalized to UTC and are not shifted on read.
 */
public fun DataFrame.Companion.readArrowIPC(
    channel: ReadableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowIPCImpl(channel, allocator, nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [channel]
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s; an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row). See [readParquet] and [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * A `Timestamp(unit, tz)` column identifies a single point on the time-line and is read as
 * [kotlin.time.Instant]; a zone-less `Timestamp(unit, null)` stays [kotlinx.datetime.LocalDateTime]. The zone
 * is display metadata — the stored values are already normalized to UTC and are not shifted on read.
 */
public fun DataFrame.Companion.readArrowFeather(
    channel: SeekableByteChannel,
    allocator: RootAllocator = Allocator.ROOT,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeatherImpl(channel, allocator, nullability)

// IPC reading block

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [file]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowIPC(
    file: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowIPC(file.toPath(), nullability)

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format)
 * data from existing file on the given [path].
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowIPC(
    path: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Files.newByteChannel(path).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [byteArray]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowIPC(
    byteArray: ByteArray,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = SeekableInMemoryByteChannel(byteArray).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [stream]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowIPC(
    stream: InputStream,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Channels.newChannel(stream).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format) data from existing [url]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
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

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-streaming-format)
 * data from the file or URL at [path].
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
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
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [file]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowFeather(
    file: File,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeather(file.toPath(), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files)
 * data from an existing file on the given [path].
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowFeather(
    path: Path,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = Files.newByteChannel(path).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [byteArray]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowFeather(
    byteArray: ByteArray,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = SeekableInMemoryByteChannel(byteArray).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [stream]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrowFeather(
    stream: InputStream,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowFeather(stream.readBytes(), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [url]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
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
 * Read [Arrow random access format](https://arrow.apache.org/java/current/ipc.html#writing-and-reading-random-access-files) data from existing [path]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
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
 * Read [Arrow any format](https://arrow.apache.org/java/current/ipc.html#reading-writing-ipc-formats) data from existing [reader]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun DataFrame.Companion.readArrow(
    reader: ArrowReader,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
): AnyFrame = readArrowImpl(reader, nullability)

/**
 * Read [Arrow any format](https://arrow.apache.org/java/current/ipc.html#reading-writing-ipc-formats) data from existing [ArrowReader]
 *
 * A `Timestamp` column that carries a time zone is read as [kotlin.time.Instant], a zone-less one as
 * [kotlinx.datetime.LocalDateTime]; see [readParquet] for the full rule.
 */
public fun ArrowReader.toDataFrame(nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
    DataFrame.Companion.readArrowImpl(this, nullability)

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [urls] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s: an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row, so an absent struct and a present all-`null` struct read the same). The same applies to
 * [readArrowIPC] and [readArrowFeather]; see [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * Timestamp columns flagged `isAdjustedToUTC = true` count time units since `1970-01-01T00:00:00Z`, so they
 * identify a single point on the time-line and are read as [kotlin.time.Instant] in every supported precision
 * (`MILLIS`, `MICROS`, `NANOS`). Zone-less timestamps identify no such point and stay
 * [kotlinx.datetime.LocalDateTime]. The original time zone is not stored in the file, so only the instant
 * survives; use `convert { … }.with { it.toLocalDateTime(zone) }` for wall-clock values.
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
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s: an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row, so an absent struct and a present all-`null` struct read the same). The same applies to
 * [readArrowIPC] and [readArrowFeather]; see [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * Timestamp columns flagged `isAdjustedToUTC = true` count time units since `1970-01-01T00:00:00Z`, so they
 * identify a single point on the time-line and are read as [kotlin.time.Instant] in every supported precision
 * (`MILLIS`, `MICROS`, `NANOS`). Zone-less timestamps identify no such point and stay
 * [kotlinx.datetime.LocalDateTime]. The original time zone is not stored in the file, so only the instant
 * survives; use `convert { … }.with { it.toLocalDateTime(zone) }` for wall-clock values.
 */
public fun DataFrame.Companion.readParquet(
    vararg strUrls: String,
    nullability: NullabilityOptions = NullabilityOptions.Infer,
    batchSize: Long = ARROW_PARQUET_DEFAULT_BATCH_SIZE,
): AnyFrame = readArrowDatasetImpl(arrayOf(*strUrls), FileFormat.PARQUET, nullability, batchSize)

/**
 * Read [Parquet](https://parquet.apache.org/) data from existing [paths] by using [Arrow Dataset](https://arrow.apache.org/docs/java/dataset.html)
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s: an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row, so an absent struct and a present all-`null` struct read the same). The same applies to
 * [readArrowIPC] and [readArrowFeather]; see [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * Timestamp columns flagged `isAdjustedToUTC = true` count time units since `1970-01-01T00:00:00Z`, so they
 * identify a single point on the time-line and are read as [kotlin.time.Instant] in every supported precision
 * (`MILLIS`, `MICROS`, `NANOS`). Zone-less timestamps identify no such point and stay
 * [kotlinx.datetime.LocalDateTime]. The original time zone is not stored in the file, so only the instant
 * survives; use `convert { … }.with { it.toLocalDateTime(zone) }` for wall-clock values.
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
 *
 * Nested Arrow `Struct` columns are read as [ColumnGroup]s: an optional (nullable) struct becomes a column
 * group whose child columns are nullable and hold `null` where the struct is absent (a column group is never
 * `null` per row, so an absent struct and a present all-`null` struct read the same). The same applies to
 * [readArrowIPC] and [readArrowFeather]; see [issue #536](https://github.com/Kotlin/dataframe/issues/536).
 *
 * Timestamp columns flagged `isAdjustedToUTC = true` count time units since `1970-01-01T00:00:00Z`, so they
 * identify a single point on the time-line and are read as [kotlin.time.Instant] in every supported precision
 * (`MILLIS`, `MICROS`, `NANOS`). Zone-less timestamps identify no such point and stay
 * [kotlinx.datetime.LocalDateTime]. The original time zone is not stored in the file, so only the instant
 * survives; use `convert { … }.with { it.toLocalDateTime(zone) }` for wall-clock values.
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
