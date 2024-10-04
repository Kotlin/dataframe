package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.DataType
import io.deephaven.csv.parsers.DataType.BOOLEAN_AS_BYTE
import io.deephaven.csv.parsers.DataType.BYTE
import io.deephaven.csv.parsers.DataType.CHAR
import io.deephaven.csv.parsers.DataType.CUSTOM
import io.deephaven.csv.parsers.DataType.DATETIME_AS_LONG
import io.deephaven.csv.parsers.DataType.DOUBLE
import io.deephaven.csv.parsers.DataType.FLOAT
import io.deephaven.csv.parsers.DataType.INT
import io.deephaven.csv.parsers.DataType.LONG
import io.deephaven.csv.parsers.DataType.SHORT
import io.deephaven.csv.parsers.DataType.STRING
import io.deephaven.csv.parsers.DataType.TIMESTAMP_AS_LONG
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import io.deephaven.csv.reading.CsvReader
import io.deephaven.csv.sinks.Sink
import io.deephaven.csv.sinks.SinkFactory
import io.deephaven.csv.sinks.Source
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import java.io.File
import java.io.InputStream
import java.time.ZoneOffset
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.nanoseconds
import java.time.LocalDateTime as JavaLocalDateTime
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers as DfParsers

public fun main() {
    val folder = File(
        "/mnt/data/Download/Age-sex-by-ethnic-group-grouped-total-responses-census-usually-resident-population-counts-2006-2013-2018-Censuses-RC-TA-SA2-DHB",
    )
    val mediumFile = File(folder, "DimenLookupArea8277.csv")
    val largeFile = File(folder, "Data8277.csv")

//    val file = mediumFile
    val file = largeFile

    val df1 = DataFrame.readDelimDeephavenCsv(file.inputStream())
        .also { it.print(borders = true, columnTypes = true, rowsLimit = 20) }
}

private val typesDeephavenAlreadyParses = setOf(
    typeOf<Boolean>(),
    typeOf<Byte>(),
    typeOf<Short>(),
    typeOf<Int>(),
    typeOf<Long>(),
    typeOf<Float>(),
    typeOf<Double>(),
    typeOf<LocalDateTime>(),
    typeOf<JavaLocalDateTime>(),
    typeOf<Char>(),
)

public fun DataFrame.Companion.readDelimDeephavenCsv(
    inputStream: InputStream,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Long? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame {
    val specs = CsvSpecs.builder()
        .hasHeaderRow(firstLineIsHeader)
        .parsers(Parsers.COMPLETE) // BOOLEAN, BYTE, SHORT, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
        .let { if (header == null) it else it.headers(header) }
        .let { if (readLines == null) it else it.numRows(readLines) }
        .let {
            if (colTypes.isEmpty()) {
                it
            } else {
                colTypes.entries.fold(it) { it, (name, type) ->
                    it.putParserForName(name, type.toParser())
                }
            }
        }
        .build()

    // edit the parser options to skip types that are already parsed by deephaven
    var parserOptions = parserOptions ?: ParserOptions()
    parserOptions = parserOptions.copy(
        skipTypes = parserOptions.skipTypes + typesDeephavenAlreadyParses,
    )

    val result = CsvReader.read(specs, inputStream, ListSink.sinkFactory)
    val cols =
        runBlocking {
            result.map {
                async {
                    val (columnData, type) =
                        when (it.dataType()) {
                            BOOLEAN_AS_BYTE -> (it.data() as List<Boolean>) to typeOf<Boolean>()
                            BYTE -> (it.data() as List<Byte>) to typeOf<Byte>()
                            SHORT -> (it.data() as List<Short>) to typeOf<Short>()
                            INT -> (it.data() as List<Int>) to typeOf<Int>()
                            LONG -> (it.data() as List<Long>) to typeOf<Long>()
                            FLOAT -> (it.data() as List<Float>) to typeOf<Float>()
                            DOUBLE -> (it.data() as List<Double>) to typeOf<Double>()
                            DATETIME_AS_LONG -> (it.data() as List<LocalDateTime>) to typeOf<LocalDateTime>()
                            CHAR -> (it.data() as List<Char>) to typeOf<Char>()
                            STRING -> (it.data() as List<String>) to typeOf<String>()
                            TIMESTAMP_AS_LONG -> (it.data() as List<LocalDateTime>) to typeOf<LocalDateTime>()
                            CUSTOM -> TODO()
                            null -> error("null data type")
                        }

                    val defaultColType = colTypes[".default"]
                    val colType = colTypes[it.name()] ?: defaultColType

                    val column = DataColumn.createValueColumn(it.name(), columnData, type)

                    if (it.dataType() == STRING) {
                        column as ValueColumn<String>
                        when (colType) {
                            null -> column.tryParse(parserOptions)

                            else -> {
                                val parser = DfParsers[colType.toType()]!!
                                column.parse(parser, parserOptions)
                            }
                        }
                    } else {
                        column
                    }
                }
            }.awaitAll()
        }
    return cols.toDataFrame()
}

// TODO let user choose beween timestamps and localdatetimes
internal fun ColType.toParser(): Parser<*> =
    when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.BigDecimal -> Parsers.STRING
        ColType.LocalDate -> Parsers.STRING
        ColType.LocalTime -> Parsers.STRING
        ColType.LocalDateTime -> Parsers.DATETIME
        ColType.String -> Parsers.STRING
    }

internal interface SinkSource<T : Any> :
    Sink<T>,
    Source<T>

/**
 * Implementation of [Sink] and [Source] that stores data in an [ArrayList].
 *
 * If we ever store column data unboxed, this needs to be modified.
 */
private class ListSink(val columnIndex: Int, val dataType: DataType) : SinkSource<Any> {

    companion object {
        val sinkFactory = SinkFactory.of(
            // byteSinkSupplier =
            { ListSink(it, BYTE) as SinkSource<ByteArray> },
            // shortSinkSupplier =
            { ListSink(it, SHORT) as SinkSource<ShortArray> },
            // intSinkSupplier =
            { ListSink(it, INT) as SinkSource<IntArray> },
            // longSinkSupplier =
            { ListSink(it, LONG) as SinkSource<LongArray> },
            // floatSinkSupplier =
            { ListSink(it, FLOAT) as SinkSource<FloatArray> }, // unused in Parsers.COMPLETE
            // doubleSinkSupplier =
            { ListSink(it, DOUBLE) as SinkSource<DoubleArray> },
            // booleanAsByteSinkSupplier =
            { ListSink(it, BOOLEAN_AS_BYTE) as SinkSource<ByteArray> },
            // charSinkSupplier =
            { ListSink(it, CHAR) as SinkSource<CharArray> },
            // stringSinkSupplier =
            { ListSink(it, STRING) as SinkSource<Array<String>> },
            // dateTimeAsLongSinkSupplier =
            { ListSink(it, DATETIME_AS_LONG) as SinkSource<LongArray> },
            // timestampAsLongSinkSupplier =
            { ListSink(it, TIMESTAMP_AS_LONG) as SinkSource<LongArray> }, // unused in Parsers.COMPLETE
        )
    }

    val data: MutableList<Any?> = mutableListOf()

    private fun getValue(src: Any, srcIndex: Int, isNull: BooleanArray): Any? =
        if (isNull[srcIndex]) {
            null
        } else {
            when (dataType) {
                BOOLEAN_AS_BYTE -> (src as ByteArray)[srcIndex] == 1.toByte()

                BYTE -> (src as ByteArray)[srcIndex]

                SHORT -> (src as ShortArray)[srcIndex]

                INT -> (src as IntArray)[srcIndex]

                LONG -> (src as LongArray)[srcIndex]

                // unused in Parsers.COMPLETE
                FLOAT -> (src as FloatArray)[srcIndex]

                DOUBLE -> (src as DoubleArray)[srcIndex]

                CHAR -> (src as CharArray)[srcIndex]

                STRING -> (src as Array<String>)[srcIndex]

                DATETIME_AS_LONG -> (src as LongArray)[srcIndex].nanoseconds
                    .toComponents { seconds, nanoseconds ->
                        JavaLocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                    }.toKotlinLocalDateTime()

                // unused in Parsers.COMPLETE
                TIMESTAMP_AS_LONG -> (src as LongArray)[srcIndex].nanoseconds
                    .toComponents { seconds, nanoseconds ->
                        JavaLocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
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
        while (data.size < destBegin) {
            data += null
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
            writeAppending(src, destBeginAsInt, destEndAsInt, isNull)
        } else {
            writeReplacing(src, destBeginAsInt, destEndAsInt, isNull)
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

    override fun getUnderlying(): List<*> = data
}
