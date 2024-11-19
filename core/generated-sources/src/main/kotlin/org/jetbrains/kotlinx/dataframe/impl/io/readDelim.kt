package org.jetbrains.kotlinx.dataframe.impl.io

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.toKType
import java.io.BufferedReader
import java.io.Reader
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

internal fun DataFrame.Companion.readDelimImpl(
    reader: Reader,
    format: CSVFormat,
    colTypes: Map<String, ColType>,
    skipLines: Int,
    readLines: Int?,
    parserOptions: ParserOptions?,
): AnyFrame {
    var reader = reader
    if (skipLines > 0) {
        reader = BufferedReader(reader)
        repeat(skipLines) { reader.readLine() }
    }

    val csvParser = format.parse(reader)
    val records = if (readLines == null) {
        csvParser.records
    } else {
        require(readLines >= 0) { "`readLines` must not be negative" }
        val records = ArrayList<CSVRecord>(readLines)
        val iter = csvParser.iterator()
        var count = readLines ?: 0
        while (iter.hasNext() && 0 < count--) {
            records.add(iter.next())
        }
        records
    }

    val columnNames = csvParser.headerNames.takeIf { it.isNotEmpty() }
        ?: (1..(records.firstOrNull()?.count() ?: 0)).map { index -> "X$index" }

    val generator = ColumnNameGenerator()
    val uniqueNames = columnNames.map { generator.addUnique(it) }

    val cols = uniqueNames.mapIndexed { colIndex, colName ->
        val defaultColType = colTypes[".default"]
        val colType = colTypes[colName] ?: defaultColType
        var hasNulls = false
        val values = records.map {
            if (it.isSet(colIndex)) {
                it[colIndex].ifEmpty {
                    hasNulls = true
                    null
                }
            } else {
                hasNulls = true
                null
            }
        }
        val column = DataColumn.createValueColumn(colName, values, typeOf<String>().withNullability(hasNulls))
        val skipTypes = when {
            colType != null ->
                // skip all types except the desired type
                ParserOptions.allTypesExcept(colType.toKType())

            else ->
                // respect the provided parser options
                parserOptions?.skipTypes ?: emptySet()
        }
        val adjustsedParserOptions = (parserOptions ?: ParserOptions())
            .copy(skipTypes = skipTypes)

        return@mapIndexed column.tryParse(adjustsedParserOptions)
    }
    return cols.toDataFrame()
}
