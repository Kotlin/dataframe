package org.jetbrains.kotlinx.dataframe.io

import org.apache.arrow.memory.RootAllocator
import org.apache.arrow.vector.BigIntVector
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
import org.jetbrains.kotlinx.dataframe.AnyBaseColumn
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Infer
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
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
import java.time.LocalDateTime
import kotlin.reflect.typeOf

internal object Allocator {
    val ROOT by lazy {
        RootAllocator(Long.MAX_VALUE)
    }
}

private fun readArrow(channel: ReadableByteChannel, allocator: RootAllocator = Allocator.ROOT): AnyFrame {
    ArrowStreamReader(channel, allocator).use { reader ->
        val dfs = buildList {
            val root = reader.vectorSchemaRoot
            val schema = root.schema
            while (reader.loadNextBatch()) {
                val df = schema.fields.map { f -> readField(root, f) }.toDataFrame()
                add(df)
            }
        }
        return dfs.concat()
    }
}

private fun readArrow(channel: SeekableByteChannel, allocator: RootAllocator = Allocator.ROOT): AnyFrame {
    ArrowFileReader(channel, allocator).use { reader ->
        val dfs = buildList {
            reader.recordBlocks.forEach { block ->
                reader.loadRecordBatch(block)
                val root = reader.vectorSchemaRoot
                val schema = root.schema
                val df = schema.fields.map { f -> readField(root, f) }.toDataFrame()
                add(df)
            }
        }
        return dfs.concat()
    }
}

private fun UInt1Vector.values(range: IntRange): List<Byte?> = range.map { getObject(it) }
private fun UInt2Vector.values(range: IntRange): List<Char?> = range.map { getObject(it) }
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
private fun TimeNanoVector.values(range: IntRange): List<Long?> = range.map { getObject(it) }
private fun TimeMicroVector.values(range: IntRange): List<Long?> = range.map { getObject(it) }
private fun TimeMilliVector.values(range: IntRange): List<LocalDateTime?> = range.map { getObject(it) }
private fun TimeSecVector.values(range: IntRange): List<Int?> = range.map { getObject(it) }
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

private inline fun <reified T> List<T>.withType() = this to typeOf<T>()

private fun readField(root: VectorSchemaRoot, field: Field): AnyBaseColumn {
    val range = 0 until root.rowCount
    val (list, type) = when (val vector = root.getVector(field)) {
        is VarCharVector -> vector.values(range).withType()
        is LargeVarCharVector -> vector.values(range).withType()
        is VarBinaryVector -> vector.values(range).withType()
        is LargeVarBinaryVector -> vector.values(range).withType()
        is SmallIntVector -> vector.values(range).withType()
        is TinyIntVector -> vector.values(range).withType()
        is UInt1Vector -> vector.values(range).withType()
        is UInt2Vector -> vector.values(range).withType()
        is UInt4Vector -> vector.values(range).withType()
        is UInt8Vector -> vector.values(range).withType()
        is IntVector -> vector.values(range).withType()
        is BigIntVector -> vector.values(range).withType()
        is DecimalVector -> vector.values(range).withType()
        is Decimal256Vector -> vector.values(range).withType()
        is Float8Vector -> vector.values(range).withType()
        is Float4Vector -> vector.values(range).withType()
        is DurationVector -> vector.values(range).withType()
        is TimeNanoVector -> vector.values(range).withType()
        is TimeMicroVector -> vector.values(range).withType()
        is TimeMilliVector -> vector.values(range).withType()
        is TimeSecVector -> vector.values(range).withType()
        is StructVector -> vector.values(range).withType()
        else -> {
            TODO("not fully implemented")
        }
    }
    return DataColumn.createValueColumn(field.name, list, type, Infer.Nulls)
}

public fun DataFrame.Companion.readArrow(file: File): AnyFrame {
    return Files.newByteChannel(file.toPath()).use { readArrow(it) }
}

public fun DataFrame.Companion.readArrow(stream: InputStream): AnyFrame = Channels.newChannel(stream).use { readArrow(it) }

public fun DataFrame.Companion.readArrow(url: URL): AnyFrame =
    when {
        url.isFile() -> readArrow(url.asFile())
        url.isProtocolSupported() -> url.openStream().use { readArrow(it) }
        else -> {
            throw IllegalArgumentException("Invalid protocol for url $url")
        }
    }

public fun DataFrame.Companion.readArrow(path: String): AnyFrame = if (path.isURL()) {
    readArrow(URL(path))
} else {
    readArrow(File(path))
}
