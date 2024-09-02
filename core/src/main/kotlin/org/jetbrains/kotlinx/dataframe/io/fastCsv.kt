package org.jetbrains.kotlinx.dataframe

import de.siegmar.fastcsv.reader.CsvReader
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.TypedColumnDataCollector
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.toType
import java.io.BufferedReader
import java.io.Reader
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public fun DataFrame.Companion.readDelimFastCsv(
    reader: Reader,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame {
    var reader = reader
    if (header != null && firstLineIsHeader) {
        reader = BufferedReader(reader)
        reader.readLine()
    }

    val csvParser = CsvReader.builder()
        .build(reader)

    val records = if (readLines == null) {
        csvParser.toMutableList()
    } else {
        require(readLines >= 0) { "`readLines` must not be negative" }
        val iter = csvParser.iterator()
        var count = readLines ?: 0
        buildList {
            while (iter.hasNext() && 0 < count--) {
                add(iter.next())
            }
        }.toMutableList()
    }

    val columnNames = when {
        header != null -> header
        firstLineIsHeader -> records.removeFirstOrNull()?.fields ?: emptyList()
        else -> (1..(records.firstOrNull()?.fieldCount ?: 0)).map { index -> "X$index" }
    }

    val generator = ColumnNameGenerator()
    val uniqueNames = columnNames.map { generator.addUnique(it) }

    val cols = uniqueNames.mapIndexed { colIndex, colName ->
        val defaultColType = colTypes[".default"]
        val colType = colTypes[colName] ?: defaultColType
        var hasNulls = false
        val values = records.map {
            it.getField(colIndex).ifEmpty {
                hasNulls = true
                null
            }
        }
        val column = DataColumn.createValueColumn(colName, values, typeOf<String>().withNullability(hasNulls))
        when (colType) {
            null -> column.tryParse(parserOptions)

            else -> {
                val parser = Parsers[colType.toType()]!!
                column.parse(parser, parserOptions)
            }
        }
    }
    return cols.toDataFrame()
}

public fun DataFrame.Companion.readDelimFastCsvSequential(
    reader: Reader,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame {
    var reader = reader
    if (header != null && firstLineIsHeader) {
        reader = BufferedReader(reader)
        reader.readLine()
    }

    val csvParser = CsvReader.builder()
        .build(reader)

    val records = if (readLines == null) {
        csvParser.asSequence()
    } else {
        require(readLines >= 0) { "`readLines` must not be negative" }
        val iter = csvParser.iterator()
        var count = readLines ?: 0
        sequence {
            while (iter.hasNext() && 0 < count--) {
                yield(iter.next())
            }
        }
    }

    var columnNames = header
    var columnCollectors: List<TypedColumnDataCollector<String?>> = emptyList()

    var isFirst = true
    for (row in records) {
        if (isFirst) {
            columnCollectors = List(row.fieldCount) { createDataCollector<String?>(type = typeOf<String?>()) }
        }

        // grab header from first row if it's not provided
        if (isFirst && header == null) {
            columnNames = if (firstLineIsHeader) {
                row.fields
            } else {
                List(row.fieldCount) { i -> "X${i + 1}" }
            }
        } else {
            repeat(row.fieldCount) { i ->
                columnCollectors[i].add(
                    row.getField(i).ifEmpty { null },
                )
            }
        }
        isFirst = false
    }

    val generator = ColumnNameGenerator()
    val uniqueNames = columnNames?.map { generator.addUnique(it) }.orEmpty()

    val defaultColType = colTypes[".default"]
    val cols = columnCollectors.mapIndexed { i, col ->
        val colName = uniqueNames[i]
        val column = col.toColumn(colName) // already infers nullability

        when (val colType = colTypes[colName] ?: defaultColType) {
            null -> column.tryParse(parserOptions)

            else -> {
                val parser = Parsers[colType.toType()]!!
                column.parse(parser, parserOptions)
            }
        }
    }
    return cols.toDataFrame()
}
