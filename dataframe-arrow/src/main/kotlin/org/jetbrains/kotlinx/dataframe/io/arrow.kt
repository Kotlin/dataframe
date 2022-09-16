package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.BigIntVector
import org.apache.arrow.vector.BitVector
import org.apache.arrow.vector.DateDayVector
import org.apache.arrow.vector.DateMilliVector
import org.apache.arrow.vector.Decimal256Vector
import org.apache.arrow.vector.DecimalVector
import org.apache.arrow.vector.DurationVector
import org.apache.arrow.vector.Float4Vector
import org.apache.arrow.vector.Float8Vector
import org.apache.arrow.vector.IntVector
import org.apache.arrow.vector.LargeVarBinaryVector
import org.apache.arrow.vector.LargeVarCharVector
import org.apache.arrow.vector.SmallIntVector
import org.apache.arrow.vector.TimeMicroVector
import org.apache.arrow.vector.TimeMilliVector
import org.apache.arrow.vector.TimeNanoVector
import org.apache.arrow.vector.TimeSecVector
import org.apache.arrow.vector.TinyIntVector
import org.apache.arrow.vector.UInt1Vector
import org.apache.arrow.vector.UInt2Vector
import org.apache.arrow.vector.UInt4Vector
import org.apache.arrow.vector.UInt8Vector
import org.apache.arrow.vector.VarBinaryVector
import org.apache.arrow.vector.VarCharVector
import org.apache.arrow.vector.VectorSchemaRoot
import org.apache.arrow.vector.complex.StructVector
import org.apache.arrow.vector.ipc.ArrowFileReader
import org.apache.arrow.vector.ipc.ArrowStreamReader
import org.apache.arrow.vector.types.pojo.Field
import org.apache.arrow.vector.util.DateUtility
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.jetbrains.kotlinx.dataframe.AnyBaseCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.NullabilityOptions
import org.jetbrains.kotlinx.dataframe.api.applyNullability
import org.jetbrains.kotlinx.dataframe.api.NullabilityException
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import org.jetbrains.kotlinx.dataframe.impl.asList
import java.io.File
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public class ArrowFeather : SupportedFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readArrowFeather(stream, NullabilityOptions.Widening)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readArrowFeather(file, NullabilityOptions.Widening)

    override fun acceptsExtension(ext: String): Boolean = ext == "feather"

    override val testOrder: Int = 50000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        return DefaultReadArrowMethod(pathRepresentation)
    }
}

private const val readArrowFeather = "readArrowFeather"

private class DefaultReadArrowMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, readArrowFeather)

internal object Allocator {
    val ROOT by lazy {
        RootAllocator(Long.MAX_VALUE)
    }
}

/**
 * same as [Iterable<DataFrame<T>>.concat()] without internal type guessing (all batches should have the same schema)
 */
internal fun <T> Iterable<DataFrame<T>>.concatKeepingSchema(): DataFrame<T> {
    val dataFrames = asList()
    when (dataFrames.size) {
        0 -> return emptyDataFrame()
        1 -> return dataFrames[0]
    }

    val columnNames = dataFrames.first().columnNames()

    val columns = columnNames.map { name ->
        val values = dataFrames.flatMap { it.getColumn(name).values() }
        DataColumn.createValueColumn(name, values, dataFrames.first().getColumn(name).type())
    }
    return dataFrameOf(columns).cast()
}

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [channel]
 */
public fun DataFrame.Companion.readArrowIPC(channel: ReadableByteChannel, allocator: RootAllocator = Allocator.ROOT, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame {
    ArrowStreamReader(channel, allocator).use { reader ->
        val dfs = buildList {
            val root = reader.vectorSchemaRoot
            val schema = root.schema
            while (reader.loadNextBatch()) {
                val df = schema.fields.map { f -> readField(root, f, nullability) }.toDataFrame()
                add(df)
            }
        }
        return dfs.concatKeepingSchema()
    }
}

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [channel]
 */
public fun DataFrame.Companion.readArrowFeather(channel: SeekableByteChannel, allocator: RootAllocator = Allocator.ROOT, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame {
    ArrowFileReader(channel, allocator).use { reader ->
        val dfs = buildList {
            reader.recordBlocks.forEach { block ->
                reader.loadRecordBatch(block)
                val root = reader.vectorSchemaRoot
                val schema = root.schema
                val df = schema.fields.map { f -> readField(root, f, nullability) }.toDataFrame()
                add(df)
            }
        }
        return dfs.concatKeepingSchema()
    }
}

private fun BitVector.values(range: IntRange): List<Boolean?> = range.map { getObject(it) }

private fun UInt1Vector.values(range: IntRange): List<Short?> = range.map { getObjectNoOverflow(it) }
private fun UInt2Vector.values(range: IntRange): List<Int?> = range.map { getObject(it)?.code }
private fun UInt4Vector.values(range: IntRange): List<Long?> = range.map { getObjectNoOverflow(it) }
private fun UInt8Vector.values(range: IntRange): List<BigInteger?> = range.map { getObjectNoOverflow(it) }

private fun TinyIntVector.values(range: IntRange): List<Byte?> = range.map { getObject(it) }
private fun SmallIntVector.values(range: IntRange): List<Short?> = range.map { getObject(it) }
private fun IntVector.values(range: IntRange): List<Int?> = range.map { getObject(it) }
private fun BigIntVector.values(range: IntRange): List<Long?> = range.map { getObject(it) }

private fun DecimalVector.values(range: IntRange): List<BigDecimal?> = range.map { getObject(it) }
private fun Decimal256Vector.values(range: IntRange): List<BigDecimal?> = range.map { getObject(it) }

private fun Float4Vector.values(range: IntRange): List<Float?> = range.map { getObject(it) }
private fun Float8Vector.values(range: IntRange): List<Double?> = range.map { getObject(it) }

private fun DurationVector.values(range: IntRange): List<Duration?> = range.map { getObject(it) }
private fun DateDayVector.values(range: IntRange): List<LocalDate?> = range.map {
    if (getObject(it) == null) null else
    DateUtility.getLocalDateTimeFromEpochMilli(getObject(it).toLong() * DateUtility.daysToStandardMillis).toLocalDate()
}
private fun DateMilliVector.values(range: IntRange): List<LocalDateTime?> = range.map { getObject(it) }

private fun TimeNanoVector.values(range: IntRange): List<LocalTime?> = range.mapIndexed { i, it ->
    if (isNull(i)) {
        null
    } else {
        LocalTime.ofNanoOfDay(get(it))
    }
}
private fun TimeMicroVector.values(range: IntRange): List<LocalTime?> = range.mapIndexed { i, it ->
    if (isNull(i)) {
        null
    } else {
        LocalTime.ofNanoOfDay(getObject(it) * 1000)
    }
}
private fun TimeMilliVector.values(range: IntRange): List<LocalTime?> = range.mapIndexed { i, it ->
    if (isNull(i)) {
        null
    } else {
        LocalTime.ofNanoOfDay(get(it).toLong() * 1000_000)
    }
}
private fun TimeSecVector.values(range: IntRange): List<LocalTime?> = range.map { getObject(it)?.let {LocalTime.ofSecondOfDay(it.toLong())}  }

private fun StructVector.values(range: IntRange): List<Map<String, Any?>?> = range.map { getObject(it) }

private fun VarCharVector.values(range: IntRange): List<String?> = range.map {
    if (isNull(it)) {
        null
    } else {
        String(get(it))
    }
}

private fun VarBinaryVector.values(range: IntRange): List<ByteArray?> = range.map {
    if (isNull(it)) {
        null
    } else {
        get(it)
    }
}

private fun LargeVarBinaryVector.values(range: IntRange): List<ByteArray?> = range.map {
    if (isNull(it)) {
        null
    } else {
        get(it)
    }
}

private fun LargeVarCharVector.values(range: IntRange): List<String?> = range.map {
    if (isNull(it)) {
        null
    } else {
        String(get(it))
    }
}

private inline fun <reified T> List<T?>.withTypeNullable(expectedNulls: Boolean, nullabilityOptions: NullabilityOptions): Pair<List<T?>, KType> {
    val nullable = nullabilityOptions.applyNullability(this, expectedNulls)
    return this to typeOf<T>().withNullability(nullable)
}

private fun readField(root: VectorSchemaRoot, field: Field, nullability: NullabilityOptions): AnyBaseCol {
    try {
        val range = 0 until root.rowCount
        val (list, type) = when (val vector = root.getVector(field)) {
            is VarCharVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is LargeVarCharVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is VarBinaryVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is LargeVarBinaryVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is BitVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is SmallIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is TinyIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is UInt1Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is UInt2Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is UInt4Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is UInt8Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is IntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is BigIntVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is DecimalVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is Decimal256Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is Float8Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is Float4Vector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is DurationVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is DateDayVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is DateMilliVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is TimeNanoVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is TimeMicroVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is TimeMilliVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is TimeSecVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            is StructVector -> vector.values(range).withTypeNullable(field.isNullable, nullability)
            else -> {
                TODO("not fully implemented")
            }
        }
        return DataColumn.createValueColumn(field.name, list, type, Infer.None)
    } catch (unexpectedNull: NullabilityException) {
        throw IllegalArgumentException("Column `${field.name}` should be not nullable but has nulls")
    }
}

// IPC reading block

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [file]
 */
public fun DataFrame.Companion.readArrowIPC(file: File, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        Files.newByteChannel(file.toPath()).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [byteArray]
 */
public fun DataFrame.Companion.readArrowIPC(byteArray: ByteArray, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        SeekableInMemoryByteChannel(byteArray).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [stream]
 */
public fun DataFrame.Companion.readArrowIPC(stream: InputStream, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        Channels.newChannel(stream).use { readArrowIPC(it, nullability = nullability) }

/**
 * Read [Arrow interprocess streaming format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-streaming-format) data from existing [url]
 */
public fun DataFrame.Companion.readArrowIPC(url: URL, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
    when {
        isFile(url) -> readArrowIPC(urlAsFile(url), nullability)
        isProtocolSupported(url) -> url.openStream().use { readArrowIPC(it, nullability) }
        else -> {
            throw IllegalArgumentException("Invalid protocol for url $url")
        }
    }

public fun DataFrame.Companion.readArrowIPC(path: String, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame = if (isURL(path)) {
    readArrowIPC(URL(path), nullability)
} else {
    readArrowIPC(File(path), nullability)
}

// Feather reading block

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [file]
 */
public fun DataFrame.Companion.readArrowFeather(file: File, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        Files.newByteChannel(file.toPath()).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [byteArray]
 */
public fun DataFrame.Companion.readArrowFeather(byteArray: ByteArray, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        SeekableInMemoryByteChannel(byteArray).use { readArrowFeather(it, nullability = nullability) }

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [stream]
 */
public fun DataFrame.Companion.readArrowFeather(stream: InputStream, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
        readArrowFeather(stream.readBytes(), nullability)

/**
 * Read [Arrow random access format](https://arrow.apache.org/docs/java/ipc.html#writing-and-reading-random-access-files) data from existing [url]
 */
public fun DataFrame.Companion.readArrowFeather(url: URL, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame =
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
public fun DataFrame.Companion.readArrowFeather(path: String, nullability: NullabilityOptions = NullabilityOptions.Infer): AnyFrame = if (isURL(path)) {
    readArrowFeather(URL(path), nullability)
} else {
    readArrowFeather(File(path), nullability)
}
