package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.ColType
import java.io.InputStream

/**
 *
 * @param inputStream Represents the CSV file to read.
 * @param delimiter The field delimiter character. ',' for CSV, '\t' for TSV.
 * @param header if empty, the header will be read from the CSV file
 *  else, if not empty, the CSV will be read as header-less with [header] as the column titles.
 *  Combine with [skipLines] if you want to overwrite a CSV header.
 * @param colTypes A map of column names to their expected [ColType]s. Can be supplied to force
 *   the parser to interpret a column as a specific type, e.g. `colTypes = mapOf("colName" to ColType.Int)`.
 * @param skipLines The number of lines to skip at the beginning of the CSV file before starting to read it.
 * @param readLines The maximum number of lines to read in total from the CSV to a DataFrame.
 * @param parserOptions Optional parsing options for columns initially read as [String].
 * @param allowMissingColumns If this set to `true`, then rows that are too short
 *   (that have fewer columns than the header row) will be interpreted as if the missing columns contained
 *   the empty string.
 * @param ignoreExcessColumns If this is set to `true`, then rows that are too
 *   long (that have more columns than the header row) will have those excess columns dropped.
 * @param quote The quote character, defaults to '"'.
 * @param ignoreSurroundingSpaces Whether to trim leading and trailing blanks from non-quoted values.
 * @param trimInsideQuoted Whether to trim leading and trailing blanks from inside quoted values.
 * @param csvSpecs Optional [CsvSpecs] object to configure additional CSV reader options.
 */
internal fun readCsvImpl(
    inputStream: InputStream,
    delimiter: Char, // should be '\t' for tsv, ',' for csv
    header: List<String> = emptyList(),
    colTypes: Map<String, ColType> = emptyMap(),
    skipLines: Long = 0L,
    readLines: Long? = null,
    parserOptions: ParserOptions? = null,
    ignoreEmptyLines: Boolean = false,
    allowMissingColumns: Boolean = false,
    ignoreExcessColumns: Boolean = false,
    quote: Char = '"',
    ignoreSurroundingSpaces: Boolean = true,
    trimInsideQuoted: Boolean = false,
    csvSpecs: CsvSpecs = CsvSpecs.builder().build(),
): DataFrame<*> {
    val csvSpecs = CsvSpecs.builder()
        .from(csvSpecs)
        .setHeader(header)
        .parsers(Parsers.DEFAULT) // BOOLEAN, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
        .setColTypes(colTypes, parserOptions)
        .nullValueLiterals(parserOptions?.nullStrings ?: defaultNullStrings)
        .headerLegalizer(::legalizeHeader)
        .setSkipLines(header.isEmpty(), skipLines)
        .numRows(readLines ?: Long.MAX_VALUE)
        .ignoreEmptyLines(ignoreEmptyLines)
        .allowMissingColumns(allowMissingColumns)
        .ignoreExcessColumns(ignoreExcessColumns)
        .delimiter(delimiter)
        .quote(quote)
        .ignoreSurroundingSpaces(ignoreSurroundingSpaces)
        .trim(trimInsideQuoted)
        .build()

    TODO()
}

private fun legalizeHeader(header: Array<String>): Array<String> {
    val generator = ColumnNameGenerator()
    return header.map { generator.addUnique(it) }.toTypedArray()
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
    if (header.isEmpty()) {
        // take header from csv
        hasHeaderRow(true)
    } else {
        hasHeaderRow(false)
            .headers(header)
    }

/**
 * Small hacky reflection-based solution to get the internal [org.jetbrains.kotlinx.dataframe.impl.api.Parsers.nulls]
 */
internal val defaultNullStrings: Set<String>
    get() {
        val clazz = Class.forName("org.jetbrains.kotlinx.dataframe.impl.api.Parsers")
        val objectInstanceField = clazz.getDeclaredField("INSTANCE")
        objectInstanceField.isAccessible = true
        val parsersObjectInstance = objectInstanceField.get(null)
        val nullsGetter = clazz.getMethod("getNulls")
        val nulls = nullsGetter.invoke(parsersObjectInstance) as Set<String>
        return nulls
    }

/**
 * Converts a [ColType] to a [Parser] from the Deephaven CSV library.
 * If no direct [Parser] exists, it defaults to [Parsers.STRING] so that [DataFrame.parse] can handle it.
 */
internal fun ColType.toCsvParser(parserOptions: ParserOptions?): Parser<*> {
    val canUseDeepHavenLocalDateTime = parserOptions == null ||
        with(parserOptions) { locale == null && dateTimePattern == null && dateTimeFormatter == null }

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
