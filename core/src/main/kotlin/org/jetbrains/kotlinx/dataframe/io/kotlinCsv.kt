package org.jetbrains.kotlinx.dataframe.io

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.impl.TypedColumnDataCollector
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import org.jetbrains.kotlinx.dataframe.impl.api.parse
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import java.io.InputStream
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

public fun DataFrame.Companion.readDelimKotlinCsv(
    inputStream: InputStream,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame =
    csvReader().open(inputStream) {
        val sequence = readAllAsSequence()

        if (readLines != null) TODO()
        val records = sequence.toMutableList()

        val columnNames = when {
            header != null -> header
            firstLineIsHeader -> records.removeFirstOrNull() ?: emptyList()
            else -> (1..(records.firstOrNull()?.size ?: 0)).map { index -> "X$index" }
        }

        val generator = ColumnNameGenerator()
        val uniqueNames = columnNames.map { generator.addUnique(it) }

        val cols = uniqueNames.mapIndexed { colIndex, colName ->
            val defaultColType = colTypes[".default"]
            val colType = colTypes[colName] ?: defaultColType
            var hasNulls = false
            val values = records.map {
                it[colIndex].ifEmpty {
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
        cols
    }.toDataFrame()

public fun DataFrame.Companion.readDelimKotlinCsvSequential(
    inputStream: InputStream,
    header: List<String>? = null,
    colTypes: Map<String, ColType> = mapOf(),
    firstLineIsHeader: Boolean = true,
    readLines: Int? = null,
    parserOptions: ParserOptions? = null,
): AnyFrame =
    csvReader().open(inputStream) {
        val records = readAllAsSequence()
        if (readLines != null) TODO()

        var columnNames = header
        var columnCollectors: List<TypedColumnDataCollector<String?>> = emptyList()

        var isFirst = true
        for (row in records) {
            if (isFirst) {
                columnCollectors = List(row.size) { createDataCollector<String?>(type = typeOf<String?>()) }
            }

            // grab header from first row if it's not provided
            if (isFirst && header == null) {
                columnNames = if (firstLineIsHeader) {
                    row
                } else {
                    List(row.size) { i -> "X${i + 1}" }
                }
            } else {
                repeat(row.size) { i ->
                    columnCollectors[i].add(
                        row[i].ifEmpty { null },
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
        cols
    }.toDataFrame()
