package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.DataType
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import io.deephaven.csv.reading.CsvReader
import io.deephaven.csv.sinks.SinkFactory
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDateTime
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

    val result = CsvReader.read(specs, inputStream, SinkFactory.arrays()) // TODO this does not catch nulls

    val numRows = result.numRows().toInt()
    val cols = result.map {
        val (columnData, type) = when (it.dataType()) {
            DataType.BOOLEAN_AS_BYTE -> {
                val oneByte = 1.toByte()
                (it.data() as ByteArray).map { it == oneByte } to typeOf<Boolean>()
            }

            DataType.BYTE -> (it.data() as ByteArray).toList() to typeOf<Byte>()

            DataType.SHORT -> (it.data() as ShortArray).toList() to typeOf<Short>()

            DataType.INT -> (it.data() as IntArray).toList() to typeOf<Int>()

            DataType.LONG -> (it.data() as LongArray).toList() to typeOf<Long>()

            DataType.FLOAT -> (it.data() as FloatArray).toList() to typeOf<Float>()

            DataType.DOUBLE -> (it.data() as DoubleArray).toList() to typeOf<Double>()

            DataType.DATETIME_AS_LONG -> (it.data() as LongArray).toList().map {
                it.nanoseconds.toComponents { seconds, nanoseconds ->
                    JavaLocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                }.toKotlinLocalDateTime()
            } to typeOf<LocalDateTime>()

            DataType.CHAR -> (it.data() as CharArray).toList() to typeOf<Char>()

            DataType.STRING -> (it.data() as Array<String>).toList() to typeOf<String>()

            DataType.TIMESTAMP_AS_LONG -> (it.data() as LongArray).toList().map {
                it.nanoseconds.toComponents { seconds, nanoseconds ->
                    JavaLocalDateTime.ofEpochSecond(seconds, nanoseconds, ZoneOffset.UTC)
                }.toKotlinLocalDateTime() // TODO
            } to typeOf<LocalDateTime>()

            DataType.CUSTOM -> TODO()

            null -> error("null data type")
        }

        val defaultColType = colTypes[".default"]
        val colType = colTypes[it.name()] ?: defaultColType

        val column = DataColumn.createValueColumn(it.name(), columnData, type)

        if (it.dataType() == DataType.STRING) {
            column as ValueColumn<String>
            when (colType) {
                null -> column.tryParse(parserOptions) // TODO try to get the parsers already in the csv reader as DataType.CUSTOM

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
