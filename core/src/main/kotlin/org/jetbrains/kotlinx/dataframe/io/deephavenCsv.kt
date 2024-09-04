package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.DataType
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import io.deephaven.csv.reading.CsvReader
import io.deephaven.csv.sinks.Sink
import io.deephaven.csv.sinks.SinkFactory
import io.deephaven.csv.sinks.Source
import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet
import it.unimi.dsi.fastutil.ints.IntSortedSet
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import org.jetbrains.kotlinx.dataframe.impl.columns.*
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.PrimitiveArrayList
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.BOOLEAN
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.BYTE
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.CHAR
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.DOUBLE
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.FLOAT
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.INT
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.LONG
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.SHORT
import org.jetbrains.kotlinx.dataframe.io.DeepHavenColumnDataHolderImpl.SinkState.STRING
import java.io.File
import java.io.InputStream
import java.time.ZoneOffset
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration.Companion.nanoseconds
import java.time.LocalDateTime as JavaLocalDateTime

public fun main() {
    val folder = File(
        "/mnt/data/Download/Age-sex-by-ethnic-group-grouped-total-responses-census-usually-resident-population-counts-2006-2013-2018-Censuses-RC-TA-SA2-DHB",
    )
    val mediumFile = File(folder, "DimenLookupArea8277.csv")
    val largeFile = File(folder, "Data8277.csv")

    val file = mediumFile
//    val file = largeFile

    val df1 = DataFrame.readDelimDeephavenCsv(file.inputStream())
        .also { it.print(borders = true, columnTypes = true, rowsLimit = 20) }
}

public fun DataFrame.Companion.readDelimDeephavenCsv(
    inputStream: InputStream,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Long? = null,
    parserOptions: ParserOptions = ParserOptions(),
): AnyFrame {
    val specs = CsvSpecs.builder()
        .hasHeaderRow(firstLineIsHeader)
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

    val parserOptions = parserOptions.copy(
        parsersToSkip = parserOptions.parsersToSkip +
            listOf(
                typeOf<Double>(),
                typeOf<Float>(),
                typeOf<Int>(),
                typeOf<Long>(),
                typeOf<Short>(),
                typeOf<Byte>(),
                typeOf<Boolean>(),
                typeOf<Char>(),
                typeOf<LocalDate>(),
                typeOf<LocalDateTime>(),
                typeOf<Instant>(),
            ),
    )

    val result = CsvReader.read(specs, inputStream, DeepHavenColumnDataHolderImpl.sinkFactory)

    val cols = result.map {
        val data = it.data() as DeepHavenColumnDataHolderImpl<*>

        val type: KType
        val columnData = when (it.dataType()) {
            DataType.BOOLEAN_AS_BYTE -> {
                data.replaceList {
                    it as PrimitiveArrayList<Byte>
                    val oneByte = 1.toByte()
                    PrimitiveArrayList<Boolean>(
                        BooleanArrayList(BooleanArray(it.size) { i -> it.getByte(i) == oneByte }),
                    )
                }
                type = typeOf<Boolean>()
                data
            }

            DataType.BYTE -> {
                type = typeOf<Byte>()
                data
            }

            DataType.SHORT -> {
                type = typeOf<Short>()
                data
            }

            DataType.INT -> {
                type = typeOf<Int>()
                data
            }

            DataType.LONG -> {
                type = typeOf<Long>()
                data
            }

            DataType.FLOAT -> {
                type = typeOf<Float>()
                data
            }

            DataType.DOUBLE -> {
                type = typeOf<Double>()
                data
            }

            DataType.DATETIME_AS_LONG, DataType.TIMESTAMP_AS_LONG -> { // TODO
                data.replaceList {
                    it as PrimitiveArrayList<Long>
                    it.mapIndexed { index, long ->
                        if (data.isNull(index)) {
                            null
                        } else {
                            long.nanoseconds.toComponents { seconds, nanoseconds ->
                                JavaLocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                            }.toKotlinLocalDateTime()
                        }
                    }.toMutableList()
                }
                data.switchToBoxedList()
                type = typeOf<LocalDateTime>()
                data
            }

            DataType.CHAR -> {
                type = typeOf<Char>()
                data
            }

            DataType.STRING -> {
                type = typeOf<String>()
                data
            }

            DataType.CUSTOM -> TODO()

            null -> error("null data type")
        }

        val defaultColType = colTypes[".default"]
        val colType = colTypes[it.name()] ?: defaultColType

        val hasNulls = data.hasNulls()
        val column = DataColumn.createValueColumn(it.name(), columnData, type.withNullability(hasNulls))

        if (it.dataType() == DataType.STRING) {
            column as ValueColumn<String>
            when (colType) {
                // TODO try to get the parsers already in the csv reader as DataType.CUSTOM
                null -> column.tryParse(parserOptions)

                else -> {
                    val parser = org.jetbrains.kotlinx.dataframe.impl.api.Parsers[colType.toType()]!!
                    column.parse(parser, parserOptions)
                }
            }
        } else {
            column
        }
    }
    return cols.toDataFrame()
}

internal fun ColType.toParser(): Parser<*> =
    when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.BigDecimal -> TODO()
        ColType.LocalDate -> TODO()
        ColType.LocalTime -> TODO()
        ColType.LocalDateTime -> Parsers.DATETIME
        ColType.String -> Parsers.STRING
    }

internal class DeepHavenColumnDataHolderImpl<T>(
    list: MutableList<T> = PrimitiveArrayList<Any>() as MutableList<T>,
    distinct: Lazy<Set<T>>? = null,
    zeroValue: Any? = Undefined,
    nullIndices: IntSortedSet = IntAVLTreeSet(),
    val columnIndex: Int,
    private val sinkState: SinkState,
) : ColumnDataHolderImpl<T>(
        list = list,
        distinct = distinct,
        zeroValue = zeroValue,
        nullIndices = nullIndices,
    ),
    Sink<Any>,
    Source<Any> {

    companion object {
        @Suppress("UNCHECKED_CAST")
        val sinkFactory: SinkFactory = SinkFactory.ofSimple(
            // byteSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Byte>(
                    zeroValue = zeroValueFor(0.toByte()),
                    columnIndex = it,
                    sinkState = BYTE,
                ) as Sink<ByteArray>
            },
            // shortSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Short>(
                    zeroValue = zeroValueFor(0.toShort()),
                    columnIndex = it,
                    sinkState = SHORT,
                ) as Sink<ShortArray>
            },
            // intSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Int>(
                    zeroValue = zeroValueFor(0.toInt()),
                    columnIndex = it,
                    sinkState = INT,
                ) as Sink<IntArray>
            },
            // longSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Long>(
                    zeroValue = zeroValueFor(0.toLong()),
                    columnIndex = it,
                    sinkState = LONG,
                ) as Sink<LongArray>
            },
            // floatSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Float>(
                    zeroValue = zeroValueFor(0.toFloat()),
                    columnIndex = it,
                    sinkState = FLOAT,
                ) as Sink<FloatArray>
            },
            // doubleSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Double>(
                    zeroValue = zeroValueFor(0.toDouble()),
                    columnIndex = it,
                    sinkState = DOUBLE,
                ) as Sink<DoubleArray>
            },
            // booleanAsByteSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Byte>(
                    zeroValue = zeroValueFor(0.toByte()),
                    columnIndex = it,
                    sinkState = BYTE,
                ) as Sink<ByteArray>
            },
            // charSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Char>(
                    zeroValue = zeroValueFor(0.toChar()),
                    columnIndex = it,
                    sinkState = CHAR,
                ) as Sink<CharArray>
            },
            // stringSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<String>(
                    zeroValue = zeroValueFor(""),
                    columnIndex = it,
                    sinkState = STRING,
                ) as Sink<Array<String>>
            },
            // dateTimeAsLongSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Long>(
                    zeroValue = zeroValueFor(0.toLong()),
                    columnIndex = it,
                    sinkState = LONG,
                ) as Sink<LongArray>
            },
            // timestampAsLongSinkSupplier =
            {
                DeepHavenColumnDataHolderImpl<Long>(
                    zeroValue = zeroValueFor(0.toLong()),
                    columnIndex = it,
                    sinkState = LONG,
                ) as Sink<LongArray>
            },
        )
    }

    enum class SinkState {
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        CHAR,
        STRING,
    }

    /**
     * Replaces the list with the given list.
     * CAREFUL: nulls are not updated.
     */
    fun replaceList(updateList: (MutableList<T>) -> MutableList<*>) {
        list = updateList(list) as MutableList<T>
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
        val srcSize = (srcEnd - srcBegin).toInt()
        if (!usesPrimitiveArrayList) error("Unsupported as source")
        when (sinkState) {
            BYTE ->
                (list as PrimitiveArrayList<Byte>)
                    .asByteArrayList()
                    .getElements(srcBeginAsInt, dest as ByteArray, 0, srcSize)

            SHORT ->
                (list as PrimitiveArrayList<Short>)
                    .asShortArrayList()
                    .getElements(srcBeginAsInt, dest as ShortArray, 0, srcSize)

            INT ->
                (list as PrimitiveArrayList<Int>)
                    .asIntArrayList()
                    .getElements(srcBeginAsInt, dest as IntArray, 0, srcSize)

            LONG ->
                (list as PrimitiveArrayList<Long>)
                    .asLongArrayList()
                    .getElements(srcBeginAsInt, dest as LongArray, 0, srcSize)

            else -> error("Unsupported as source")
        }

        isNull.fill(false)
        nullIndices.subSet(srcBeginAsInt, srcEndAsInt).forEach {
            isNull[it - srcBeginAsInt] = true
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
        val destSize = (destEnd - destBegin).toInt()
        if (appending) {
            while (size < destBegin) {
                add(null as T)
            }
            // TODO could be even more optimized with array copy
            for ((srcIndex, _) in (destBegin..<destEnd).withIndex()) {
                if (isNull[srcIndex]) {
                    add(null as T)
                } else {
                    when (sinkState) {
                        BOOLEAN -> add((src as BooleanArray)[srcIndex])
                        BYTE -> add((src as ByteArray)[srcIndex])
                        SHORT -> add((src as ShortArray)[srcIndex])
                        INT -> add((src as IntArray)[srcIndex])
                        LONG -> add((src as LongArray)[srcIndex])
                        FLOAT -> add((src as FloatArray)[srcIndex])
                        DOUBLE -> add((src as DoubleArray)[srcIndex])
                        CHAR -> add((src as CharArray)[srcIndex])
                        STRING -> add((src as Array<String>)[srcIndex] as T)
                    }
                }
            }
        } else {
            // replacing
            when (sinkState) {
                BOOLEAN -> (list as PrimitiveArrayList<Boolean>)
                    .asBooleanArrayList()
                    .setElements(destBeginAsInt, src as BooleanArray, 0, destSize)

                BYTE -> (list as PrimitiveArrayList<Byte>)
                    .asByteArrayList()
                    .setElements(destBeginAsInt, src as ByteArray, 0, destSize)

                SHORT -> (list as PrimitiveArrayList<Short>)
                    .asShortArrayList()
                    .setElements(destBeginAsInt, src as ShortArray, 0, destSize)

                INT -> (list as PrimitiveArrayList<Int>)
                    .asIntArrayList()
                    .setElements(destBeginAsInt, src as IntArray, 0, destSize)

                LONG -> (list as PrimitiveArrayList<Long>)
                    .asLongArrayList()
                    .setElements(destBeginAsInt, src as LongArray, 0, destSize)

                FLOAT -> (list as PrimitiveArrayList<Float>)
                    .asFloatArrayList()
                    .setElements(destBeginAsInt, src as FloatArray, 0, destSize)

                DOUBLE -> (list as PrimitiveArrayList<Double>)
                    .asDoubleArrayList()
                    .setElements(destBeginAsInt, src as DoubleArray, 0, destSize)

                CHAR -> (list as PrimitiveArrayList<Char>)
                    .asCharArrayList()
                    .setElements(destBeginAsInt, src as CharArray, 0, destSize)

                else -> (list as MutableList<Any?>).let {
                    for ((srcIndex, destIndex) in (destBegin..<destEnd).withIndex()) {
                        if (isNull[srcIndex]) {
                            it[destIndex.toInt()] = null
                        } else {
                            it[destIndex.toInt()] = (src as Array<Any?>)[srcIndex]
                        }
                    }
                }
            }

            for ((srcIndex, destIndex) in (destBegin..<destEnd).withIndex()) {
                if (isNull[srcIndex]) {
                    set(destIndex.toInt(), null as T)
                }
            }
        }
    }

    override fun getUnderlying(): ColumnDataHolder<T> = this
}
