package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.parsers.DataType
import io.deephaven.csv.parsers.DataType.BOOLEAN_AS_BYTE
import io.deephaven.csv.parsers.DataType.BYTE
import io.deephaven.csv.parsers.DataType.CHAR
import io.deephaven.csv.parsers.DataType.DATETIME_AS_LONG
import io.deephaven.csv.parsers.DataType.DOUBLE
import io.deephaven.csv.parsers.DataType.FLOAT
import io.deephaven.csv.parsers.DataType.INT
import io.deephaven.csv.parsers.DataType.LONG
import io.deephaven.csv.parsers.DataType.SHORT
import io.deephaven.csv.parsers.DataType.STRING
import io.deephaven.csv.parsers.DataType.TIMESTAMP_AS_LONG
import io.deephaven.csv.sinks.Sink
import io.deephaven.csv.sinks.SinkFactory
import io.deephaven.csv.sinks.Source
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Implementation of Deephaven's [Sink] and [Source] that stores data in an [ArrayList].
 *
 * The implementation is based on [Writing Your Own Data Sinks](https://github.com/deephaven/deephaven-csv/blob/main/ADVANCED.md).
 *
 * If we ever store column data unboxed / primitively, this needs to be modified.
 */
@Suppress("UNCHECKED_CAST")
internal class ListSink(val columnIndex: Int, val dataType: DataType) :
    Sink<Any>,
    Source<Any> {

    companion object {
        val SINK_FACTORY: SinkFactory = SinkFactory.ofSimple(
            // byteSinkSupplier =
            { ListSink(it, BYTE) as Sink<ByteArray> }, // unused in Parsers.DEFAULT
            // shortSinkSupplier =
            { ListSink(it, SHORT) as Sink<ShortArray> }, // unused in Parsers.DEFAULT
            // intSinkSupplier =
            { ListSink(it, INT) as Sink<IntArray> },
            // longSinkSupplier =
            { ListSink(it, LONG) as Sink<LongArray> },
            // floatSinkSupplier =
            { ListSink(it, FLOAT) as Sink<FloatArray> }, // unused in Parsers.COMPLETE and Parsers.DEFAULT
            // doubleSinkSupplier =
            { ListSink(it, DOUBLE) as Sink<DoubleArray> },
            // booleanAsByteSinkSupplier =
            { ListSink(it, BOOLEAN_AS_BYTE) as Sink<ByteArray> },
            // charSinkSupplier =
            { ListSink(it, CHAR) as Sink<CharArray> },
            // stringSinkSupplier =
            { ListSink(it, STRING) as Sink<Array<String>> },
            // dateTimeAsLongSinkSupplier =
            { ListSink(it, DATETIME_AS_LONG) as Sink<LongArray> },
            // timestampAsLongSinkSupplier =
            { ListSink(it, TIMESTAMP_AS_LONG) as Sink<LongArray> }, // unused in Parsers.COMPLETE and Parsers.DEFAULT
        )
    }

    @Suppress("MUST_BE_INITIALIZED_OR_BE_ABSTRACT", "EXPLICIT_BACKING_FIELDS_UNSUPPORTED")
    val data: List<Any?>
        field = mutableListOf()

    var hasNulls: Boolean = false
        private set

    private fun getValue(src: Any, srcIndex: Int, isNull: BooleanArray): Any? =
        if (isNull[srcIndex]) {
            hasNulls = true
            null
        } else {
            when (dataType) {
                BOOLEAN_AS_BYTE -> (src as ByteArray)[srcIndex] == 1.toByte()

                // unused in Parsers.DEFAULT
                BYTE -> (src as ByteArray)[srcIndex]

                // unused in Parsers.DEFAULT
                SHORT -> (src as ShortArray)[srcIndex]

                INT -> (src as IntArray)[srcIndex]

                LONG -> (src as LongArray)[srcIndex]

                // unused in Parsers.COMPLETE and Parsers.DEFAULT
                FLOAT -> (src as FloatArray)[srcIndex]

                DOUBLE -> (src as DoubleArray)[srcIndex]

                CHAR -> (src as CharArray)[srcIndex]

                STRING -> (src as Array<String>)[srcIndex]

                DATETIME_AS_LONG -> (src as LongArray)[srcIndex].nanoseconds
                    .toComponents { seconds, nanoseconds ->
                        LocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                    }.toKotlinLocalDateTime()

                // unused in Parsers.COMPLETE and Parsers.DEFAULT
                TIMESTAMP_AS_LONG -> (src as LongArray)[srcIndex].nanoseconds
                    .toComponents { seconds, nanoseconds ->
                        LocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                    }.toKotlinLocalDateTime()

                else -> error("unsupported parser")
            }
        }

    private fun writeAppending(
        src: Any,
        destBegin: Int,
        destEnd: Int,
        isNull: BooleanArray,
    ) {
        data as MutableList<Any?>
        while (data.size < destBegin) {
            data += null
            hasNulls = true
        }
        for ((srcIndex, _) in (destBegin..<destEnd).withIndex()) {
            data += getValue(src, srcIndex, isNull)
        }
    }

    private fun writeReplacing(
        src: Any,
        destBegin: Int,
        destEnd: Int,
        isNull: BooleanArray,
    ) {
        data as MutableList<Any?>
        for ((srcIndex, destIndex) in (destBegin..<destEnd).withIndex()) {
            data[destIndex] = getValue(src, srcIndex, isNull)
        }
    }

    override fun write(
        src: Any,
        isNull: BooleanArray,
        destBegin: Long,
        destEnd: Long,
        appending: Boolean,
    ) {
        if (destBegin == destEnd) return
        val destBeginAsInt = destBegin.toInt()
        val destEndAsInt = destEnd.toInt()
        if (appending) {
            writeAppending(src = src, destBegin = destBeginAsInt, destEnd = destEndAsInt, isNull = isNull)
        } else {
            writeReplacing(src = src, destBegin = destBeginAsInt, destEnd = destEndAsInt, isNull = isNull)
        }
    }

    override fun read(
        dest: Any,
        isNull: BooleanArray,
        srcBegin: Long,
        srcEnd: Long,
    ) {
        if (srcBegin == srcEnd) return
        val srcBeginAsInt = srcBegin.toInt()
        val srcEndAsInt = srcEnd.toInt()

        when (dataType) {
            BYTE -> {
                dest as ByteArray
                for ((srcIndex, destIndex) in (srcBeginAsInt..<srcEndAsInt).withIndex()) {
                    val value = data[srcIndex] as Byte?
                    if (value != null) dest[destIndex] = value
                    isNull[destIndex] = value == null
                }
            }

            SHORT -> {
                dest as ShortArray
                for ((srcIndex, destIndex) in (srcBeginAsInt..<srcEndAsInt).withIndex()) {
                    val value = data[srcIndex] as Short?
                    if (value != null) dest[destIndex] = value
                    isNull[destIndex] = value == null
                }
            }

            INT -> {
                dest as IntArray
                for ((srcIndex, destIndex) in (srcBeginAsInt..<srcEndAsInt).withIndex()) {
                    val value = data[srcIndex] as Int?
                    if (value != null) dest[destIndex] = value
                    isNull[destIndex] = value == null
                }
            }

            LONG -> {
                dest as LongArray
                for ((srcIndex, destIndex) in (srcBeginAsInt..<srcEndAsInt).withIndex()) {
                    val value = data[srcIndex] as Long?
                    if (value != null) dest[destIndex] = value
                    isNull[destIndex] = value == null
                }
            }

            else -> error("unsupported sink state")
        }
    }

    override fun getUnderlying(): ListSink = this
}
