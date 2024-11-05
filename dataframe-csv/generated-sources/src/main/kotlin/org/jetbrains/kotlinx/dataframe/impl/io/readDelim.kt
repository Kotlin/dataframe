package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
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
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.parsers.Parsers
import io.deephaven.csv.reading.CsvReader
import io.deephaven.csv.util.CsvReaderException
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_NULL_STRINGS
import org.jetbrains.kotlinx.dataframe.io.skippingBomCharacters
import org.jetbrains.kotlinx.dataframe.io.toKType
import org.jetbrains.kotlinx.dataframe.io.useDecompressed
import java.io.InputStream
import java.math.BigDecimal
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration

/**
 * Implementation to read delimiter-separated data from an [InputStream] based on the Deephaven CSV library.
 *
 * @param inputStream Represents the file to read.
 * @param delimiter The field delimiter character. The default is ',' for CSV, 't' for TSV.
 * @param header Optional column titles. Default: empty list.
 *
 *   If non-empty, the data will be read with [header] as the column titles
 *   (use [skipLines] if there's a header in the data).
 *   If empty (default), the header will be read from the data.
 * @param compression The compression of the data.
 *   Default: [Compression.None][org.jetbrains.kotlinx.dataframe.io.Compression.None], unless detected otherwise from the input file or url.
 * @param colTypes The expected [ColType] per column name. Default: empty map, a.k.a. infer every column type.
 *
 *   If supplied for a certain column name (inferred from data or given by [header]),
 *   the parser will parse the column with the specified name as the specified type, else it will infer the type.
 *
 *   e.g. `colTypes = `[mapOf][mapOf]`("colName" `[to][to]` `[ColType][ColType]`.`[Int][ColType.Int]`)`.
 *   You can also set [ColType][ColType]`.`[DEFAULT][ColType.DEFAULT]` `[to][to]` `[ColType][ColType]`.X`
 *   to set a _default_ column type, like [ColType.String].
 * @param skipLines The number of lines to skip before reading the header and data. Default: `0`.
 *
 *   Useful for files with metadata, or comments at the beginning, or to give a custom [header].
 * @param readLines The maximum number of lines to read from the data. Default: `null`.
 *
 *   If `null`, all lines will be read.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, [DEFAULT_PARSER_OPTIONS][org.jetbrains.kotlinx.dataframe.io.DEFAULT_PARSER_OPTIONS]:
 *
 *   [ParserOptions][ParserOptions]`(`
 *
 *   &nbsp;&nbsp;&nbsp;&nbsp;[nullStrings][ParserOptions.nullStrings]`  =  `[["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_NULL_STRINGS]`,`
 *
 *   &nbsp;&nbsp;&nbsp;&nbsp;[useFastDoubleParser][ParserOptions.useFastDoubleParser]` = true,`
 *
 *   `)`
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   **NOTE:** Make sure to use [DEFAULT_PARSER_OPTIONS][org.jetbrains.kotlinx.dataframe.io.DEFAULT_PARSER_OPTIONS]`.copy()` to override the desired options.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Wether to allow rows with fewer columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too short will be interpreted as _empty_ values.
 * @param ignoreExcessColumns Whether to ignore rows with more columns than the header. Default: `true`.
 *
 *   If `true`, rows that are too long will have those columns dropped.
 * @param quote The quote character. Default: `"`.
 *
 *   Used when field- or line delimiters should be interpreted as literal text.
 *
 *   For example: `123,"hello, there",456,` would correspond to: `123`; `hello, there`; `456`.
 * @param ignoreSurroundingSpaces Whether to ignore leading and trailing blanks around non-quoted fields.
 *   Default: `true`.
 * @param trimInsideQuoted Whether to ignore leading and trailing blanks inside quoted fields.
 *   Default: `false`.
 * @param parseParallel Whether to parse the data in parallel. Default: `true`.
 *
 *   If `true`, the data will be parsed in parallel.
 *   This is usually faster, but can be turned off for debugging.
 * @param additionalCsvSpecs Optional [CsvSpecs]. Default: `null`.
 *
 *   A [CsvSpecs] instance can be supplied to configure additional
 *   parsing options not covered by the other parameters.
 *   The (default) values of other parameters will override the values in [additionalCsvSpecs].
 */
internal fun readDelimImpl(
    inputStream: InputStream,
    delimiter: Char,
    header: List<String>,
    compression: Compression<*>,
    colTypes: Map<String, ColType>,
    skipLines: Long,
    readLines: Long?,
    parserOptions: ParserOptions,
    ignoreEmptyLines: Boolean,
    allowMissingColumns: Boolean,
    ignoreExcessColumns: Boolean,
    quote: Char,
    ignoreSurroundingSpaces: Boolean,
    trimInsideQuoted: Boolean,
    parseParallel: Boolean,
    additionalCsvSpecs: CsvSpecs?,
): DataFrame<*> {
    // set up the csv specs
    val csvSpecs = with(CsvSpecs.builder()) {
        if (additionalCsvSpecs != null) from(additionalCsvSpecs)
        customDoubleParser(DataFrameCustomDoubleParser(parserOptions))
        nullValueLiterals(parserOptions.nullStrings ?: DEFAULT_NULL_STRINGS)
        headerLegalizer(::legalizeHeader)
        numRows(readLines ?: Long.MAX_VALUE)
        ignoreEmptyLines(ignoreEmptyLines)
        allowMissingColumns(allowMissingColumns)
        ignoreExcessColumns(ignoreExcessColumns)
        delimiter(delimiter)
        quote(quote)
        ignoreSurroundingSpaces(ignoreSurroundingSpaces)
        trim(trimInsideQuoted)
        concurrent(parseParallel)
        header(header)
        skipLines(takeHeaderFromCsv = header.isEmpty(), skipLines = skipLines)

        // Deephaven's LocalDateTime parser is unconfigurable, so if the user provides a locale, pattern, or formatter,
        // we must use our own parser for LocalDateTime and let Deephaven read them as Strings.
        val useDeepHavenLocalDateTime = with(parserOptions) {
            locale == null && dateTimePattern == null && dateTimeFormatter == null
        }
        parsersWithOptions(parserOptions, useDeepHavenLocalDateTime)

        // this function must be last, so the return value is used
        return@with this.withColTypes(colTypes, useDeepHavenLocalDateTime)
    }.build()

    val csvReaderResult = inputStream.useDecompressed(compression) { decompressedInputStream ->
        // read the csv
        try {
            @Suppress("ktlint:standard:comment-wrapping")
            CsvReader.read(
                /* specs = */ csvSpecs,
                /* stream = */ decompressedInputStream.skippingBomCharacters(),
                /* sinkFactory = */ ListSink.SINK_FACTORY,
            )
        } catch (e: CsvReaderException) {
            // catch case when the file is empty and header needs to be inferred from it.
            if (e.message == "Can't proceed because hasHeaders is set but input file is empty") {
                return@readDelimImpl DataFrame.empty()
            }
            throw IllegalStateException("Could not read delimiter-separated data. ${e.message}", e)
        }
    }

    val defaultColType = colTypes[ColType.DEFAULT]

    // convert each ResultColumn to a DataColumn
    val cols = csvReaderResult.map {
        it.toDataColumn(
            parserOptions = parserOptions,
            desiredColType = colTypes[it.name()] ?: defaultColType,
        )
    }

    return dataFrameOf(cols)
}

@Suppress("UNCHECKED_CAST")
private fun CsvReader.ResultColumn.toDataColumn(
    parserOptions: ParserOptions,
    desiredColType: ColType?,
): DataColumn<*> {
    val listSink = data()!! as ListSink
    val columnData = listSink.data
    val dataType = listSink.dataType
    val hasNulls = listSink.hasNulls
    val type = dataType().toKType().withNullability(hasNulls)

    val column = DataColumn.createValueColumn(
        name = name(),
        values = columnData,
        type = type,
    )
    if (dataType != STRING) return column

    // attempt to perform additional parsing if necessary, will remain String if it fails
    column as ValueColumn<String?>

    val skipTypes = when {
        desiredColType != null ->
            // skip all types except the desired type
            ParserOptions.allTypesExcept(desiredColType.toKType())

        else ->
            // no need to check for types that Deephaven already parses, skip those too
            parserOptions.skipTypes + typesDeephavenAlreadyParses
    }
    val adjustsedParserOptions = parserOptions.copy(skipTypes = skipTypes)

    return column.tryParse(adjustsedParserOptions)
}

private fun DataType?.toKType(): KType =
    when (this) {
        BOOLEAN_AS_BYTE -> typeOf<Boolean>()

        // unused in Parsers.DEFAULT
        BYTE -> typeOf<Byte>()

        // unused in Parsers.DEFAULT
        SHORT -> typeOf<Short>()

        INT -> typeOf<Int>()

        LONG -> typeOf<Long>()

        // unused in Parsers.COMPLETE and Parsers.DEFAULT
        FLOAT -> typeOf<Float>()

        DOUBLE -> typeOf<Double>()

        DATETIME_AS_LONG -> typeOf<LocalDateTime>()

        CHAR -> typeOf<Char>()

        STRING -> typeOf<String>()

        // unused in Parsers.COMPLETE and Parsers.DEFAULT
        TIMESTAMP_AS_LONG -> typeOf<LocalDateTime>()

        DataType.CUSTOM -> error("custom data type")

        null -> error("null data type")
    }

private fun legalizeHeader(header: Array<String>): Array<String> {
    val generator = ColumnNameGenerator()
    return header.map { generator.addUnique(it) }.toTypedArray()
}

/**
 * Sets correct parsers per name in [colTypes]. If [ColType.DEFAULT] is present, it sets the default parser.
 *
 * CAREFUL: Unlike the other functions on [CsvSpecs.Builder], this function can return a NEW builder instance.
 * Make sure to use the return value.
 */
private fun CsvSpecs.Builder.withColTypes(
    colTypes: Map<String, ColType>,
    useDeepHavenLocalDateTime: Boolean,
): CsvSpecs.Builder {
    if (colTypes.isEmpty()) return this

    colTypes.forEach { (colName, colType) ->
        this.putParserForName(colName, colType.toCsvParser(useDeepHavenLocalDateTime))
    }

    return if (ColType.DEFAULT in colTypes) {
        this.withDefaultParser(
            colTypes[ColType.DEFAULT]!!.toCsvParser(useDeepHavenLocalDateTime),
        )
    } else {
        this
    }
}

private fun CsvSpecs.Builder.skipLines(takeHeaderFromCsv: Boolean, skipLines: Long): CsvSpecs.Builder =
    if (takeHeaderFromCsv) {
        skipHeaderRows(skipLines)
    } else {
        skipRows(skipLines)
    }

/**
 * Sets the correct parsers for the csv, based on the [ParserOptions.skipTypes].
 */
private fun CsvSpecs.Builder.parsersWithOptions(
    parserOptions: ParserOptions,
    useDeepHavenLocalDateTime: Boolean,
): CsvSpecs.Builder =
    if (parserOptions.skipTypes.isEmpty()) {
        parsers(Parsers.DEFAULT) // BOOLEAN, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
    } else {
        val parsersToSkip = parserOptions.skipTypes
            .mapNotNull { it.toColType().toCsvParserOrNull(useDeepHavenLocalDateTime) }
        parsers(Parsers.DEFAULT.toSet() - parsersToSkip.toSet())
    }

private fun CsvSpecs.Builder.header(header: List<String>): CsvSpecs.Builder =
    if (header.isEmpty()) {
        // take header from csv
        hasHeaderRow(true)
    } else {
        hasHeaderRow(false)
            .headers(header)
    }

/**
 * Converts a [ColType] to a [Parser] from the Deephaven CSV library.
 * If no direct [Parser] exists, it returns `null`.
 */
internal fun ColType.toCsvParserOrNull(useDeepHavenLocalDateTime: Boolean): Parser<*>? =
    when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Char -> Parsers.CHAR
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.String -> Parsers.STRING
        ColType.LocalDateTime -> if (useDeepHavenLocalDateTime) Parsers.DATETIME else null
        else -> null
    }

/**
 * Converts a [ColType] to a [Parser] from the Deephaven CSV library.
 * If no direct [Parser] exists, it defaults to [Parsers.STRING] so that [DataFrame.parse] can handle it.
 */
internal fun ColType.toCsvParser(useDeepHavenLocalDateTime: Boolean): Parser<*> =
    toCsvParserOrNull(useDeepHavenLocalDateTime) ?: Parsers.STRING

internal fun KType.toColType(): ColType =
    when (this.withNullability(false)) {
        typeOf<Int>() -> ColType.Int
        typeOf<Long>() -> ColType.Long
        typeOf<Double>() -> ColType.Double
        typeOf<Boolean>() -> ColType.Boolean
        typeOf<BigDecimal>() -> ColType.BigDecimal
        typeOf<LocalDate>() -> ColType.LocalDate
        typeOf<LocalTime>() -> ColType.LocalTime
        typeOf<LocalDateTime>() -> ColType.LocalDateTime
        typeOf<String>() -> ColType.String
        typeOf<Instant>() -> ColType.Instant
        typeOf<Duration>() -> ColType.Duration
        typeOf<URL>() -> ColType.Url
        typeOf<DataFrame<*>>() -> ColType.JsonArray
        typeOf<DataRow<*>>() -> ColType.JsonObject
        typeOf<Char>() -> ColType.Char
        else -> ColType.String
    }

/**
 * Types that Deephaven already parses, so we can skip them when
 * defaulting to DataFrame's String parsers.
 *
 * [LocalDateTime] and [java.time.LocalDateTime] are not included because Deephaven cannot recognize all formats.
 */
internal val typesDeephavenAlreadyParses: Set<KType> =
    setOf(
        typeOf<Int>(),
        typeOf<Long>(),
        typeOf<Double>(),
        typeOf<Char>(),
        typeOf<Boolean>(),
    )
