package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.ColType
import java.io.InputStream

/**
 *
 * @param inputStream Represents the CSV file to read.
 * @param delimiter The field delimiter character. ',' for CSV, '\t' for TSV.
 * @param header if empty, the header will be read from the CSV file
 *  else, if not empty, the CSV will be read as header-less with [header] as the column titles.
 *  Combine with [skipLines] if you want to overwrite a CSV header.
 * @param colTypes
 * @param skipLines The number of lines to skip at the beginning of the CSV file.
 * @param readLines The number of lines to read from the CSV to a DataFrame.
 * @param parserOptions Optional parsing options for columns initially read as [String]
 * @param quote The quote character, defaults to '"'.
 */
internal fun readCsvImpl(
    inputStream: InputStream,
    delimiter: Char, // should be '\t' for tsv, ',' for csv
    header: List<String> = emptyList(),
    colTypes: Map<String, ColType> = emptyMap(),
    skipLines: Long = 0L,
    readLines: Long? = null,
    parserOptions: ParserOptions? = null,
    duplicate: Boolean = false,
    quote: Char = '"',
    ignoreSurroundingSpaces: Boolean = true,
    trimInsideQuoted: Boolean = false,
): DataFrame<*> {
    val csvSpecs = CsvSpecs.builder()
        .delimiter(delimiter)
        .setHeader(header)
        .setSkipLines(header.isEmpty(), skipLines)
        .setColTypes(colTypes, parserOptions)
        .parsers(Parsers.DEFAULT) // BOOLEAN, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
        .nullValueLiterals(parserOptions?.nullStrings ?: nullStrings)
        .quote(quote)
        .numRows(readLines ?: Long.MAX_VALUE)
        .headerLegalizer { TODO() }
//        .allowMissingColumns()
        .build()

    TODO()
}

private fun CsvSpecs.Builder.setColTypes(colTypes: Map<String, ColType>, parserOptions: ParserOptions?) =
    if (colTypes.isEmpty()) {
        this
    } else {
        colTypes.entries.fold(this) { it, (colName, colType) ->
            it.putParserForName(colName, colType.toCsvParser(parserOptions))
        }
    }

private fun CsvSpecs.Builder.setSkipLines(takeHeaderFromCsv: Boolean, skipLines: Long): CsvSpecs.Builder =
    if (takeHeaderFromCsv) {
        skipHeaderRows(skipLines)
    } else {
        skipRows(skipLines)
    }

private fun CsvSpecs.Builder.setHeader(header: List<String>): CsvSpecs.Builder =
    if (header.isEmpty() /* take header from csv */) {
        hasHeaderRow(true)
    } else {
        hasHeaderRow(false)
            .headers(header)
    }

/**
 * Small hacky reflection-based solution to get the internal [org.jetbrains.kotlinx.dataframe.impl.api.Parsers.nulls]
 */
internal val nullStrings: Set<String>
    get() {
        val clazz = Class.forName("org.jetbrains.kotlinx.dataframe.impl.api.Parsers")
        val objectInstanceField = clazz.getDeclaredField("INSTANCE")
        objectInstanceField.isAccessible = true
        val parsersObjectInstance = objectInstanceField.get(null)
        val nullsGetter = clazz.getMethod("getNulls")
        val nulls = nullsGetter.invoke(parsersObjectInstance) as Set<String>
        return nulls
    }

internal fun ColType.toCsvParser(parserOptions: ParserOptions?): Parser<*> {
    val canUseDeepHavenLocalDateTime = parserOptions == null ||
        parserOptions.run { locale == null && dateTimePattern == null && dateTimeFormatter == null }

    return when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Char -> Parsers.CHAR
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.String -> Parsers.STRING
        ColType.LocalDateTime -> if (canUseDeepHavenLocalDateTime) Parsers.DATETIME else Parsers.STRING
        else -> Parsers.STRING
    }
}
