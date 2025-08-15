@file:JvmName("ReadDelimDeephavenKt")

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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.convertTo
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.AdjustCsvSpecs
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS
import org.jetbrains.kotlinx.dataframe.io.skippingBomCharacters
import org.jetbrains.kotlinx.dataframe.io.toKType
import org.jetbrains.kotlinx.dataframe.io.useDecompressed
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration
import kotlin.time.Instant as StdlibInstant
import kotlinx.datetime.Instant as DeprecatedInstant

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
 * @param hasFixedWidthColumns Whether the data has fixed-width columns instead of a single delimiter.
 *   Default: `false`.
 *
 *   Fixed-width columns can occur, for instance, in multi-space delimited data, where the columns are separated
 *   by multiple spaces instead of a single delimiter, so columns are visually aligned.
 *   Column widths are determined by the header in the data (if present), or manually by setting
 *   [fixedColumnWidths].
 * @param fixedColumnWidths The fixed column widths. Default: empty list.
 *
 *   Requires [hasFixedWidthColumns]. If empty, the column widths will be determined by the header in the data
 *   (if present), else, this manually sets the column widths.
 *   The number of widths should match the number of columns.
 * @param parserOptions Optional [parsing options][ParserOptions] for columns initially read as [String].
 *   Default, `null`.
 *
 *   Can configure locale, date format, double parsing, skipping types, etc.
 *
 *   If [parserOptions] or any of the arguments are `null`, the global parser configuration
 *   ([DataFrame.parser][DataFrame.Companion.parser]) will be queried.
 *
 *   The only exceptions are:
 *   - [nullStrings][ParserOptions.nullStrings], which, if `null`,
 *   will take the global setting + [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS].
 *   - [skipTypes][ParserOptions.skipTypes], which will always add [typesDeephavenAlreadyParses][org.jetbrains.kotlinx.dataframe.impl.io.typesDeephavenAlreadyParses] to
 *   the given types or the global setting.
 * @param ignoreEmptyLines Whether to skip intermediate empty lines. Default: `false`.
 *
 *   If `false`, empty lines will be interpreted as having _empty_ values if [allowMissingColumns].
 * @param allowMissingColumns Whether to allow rows with fewer columns than the header. Default: `true`.
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
 *   If `true`, the data will be read and parsed in parallel by the Deephaven parser.
 *   This is usually faster but can be turned off for debugging.
 * @param compression The compression of the data.
 *   Default: [Compression.None], unless detected otherwise from the input file or url.
 * @param adjustCsvSpecs Optional extra [CsvSpecs] configuration. Default: `{ it }`.
 *
 *   Before instantiating the [CsvSpecs], the [CsvSpecs.Builder] will be passed to this lambda.
 *   This will allow you to configure/overwrite any CSV / TSV parsing options.
 */
internal fun readDelimImpl(
    inputStream: InputStream,
    delimiter: Char,
    header: List<String>,
    hasFixedWidthColumns: Boolean,
    fixedColumnWidths: List<Int>,
    colTypes: Map<String, ColType>,
    skipLines: Long,
    readLines: Long?,
    parserOptions: ParserOptions?,
    ignoreEmptyLines: Boolean,
    allowMissingColumns: Boolean,
    ignoreExcessColumns: Boolean,
    quote: Char,
    ignoreSurroundingSpaces: Boolean,
    trimInsideQuoted: Boolean,
    parseParallel: Boolean,
    compression: Compression<*>,
    adjustCsvSpecs: AdjustCsvSpecs,
): DataFrame<*> {
    // set up the csv specs
    val csvSpecs = with(CsvSpecs.builder()) {
        customDoubleParser(DataFrameCustomDoubleParser(parserOptions))

        // use the given nullStrings if provided, else take the global ones + some extras
        val nullStrings = parserOptions?.nullStrings ?: (DataFrame.parser.nulls + DEFAULT_DELIM_NULL_STRINGS)
        nullValueLiterals(nullStrings)
        headerLegalizer(::legalizeHeader)
        numRows(readLines ?: Long.MAX_VALUE)
        ignoreEmptyLines(ignoreEmptyLines)
        allowMissingColumns(allowMissingColumns)
        ignoreExcessColumns(ignoreExcessColumns)
        if (!hasFixedWidthColumns) delimiter(delimiter)
        quote(quote)
        ignoreSurroundingSpaces(ignoreSurroundingSpaces)
        trim(trimInsideQuoted)
        concurrent(parseParallel)
        header(header)
        hasFixedWidthColumns(hasFixedWidthColumns)
        if (hasFixedWidthColumns && fixedColumnWidths.isNotEmpty()) fixedColumnWidths(fixedColumnWidths)
        skipLines(takeHeaderFromCsv = header.isEmpty(), skipLines = skipLines)
        parsers(parserOptions, colTypes)

        adjustCsvSpecs(this, this)
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
            if (e.message ==
                "Can't proceed because hasHeaderRow is set but input file is empty or shorter than skipHeaderRows"
            ) {
                return@readDelimImpl DataFrame.empty()
            }
            throw IllegalStateException(
                "Could not read delimiter-separated data: CsvReaderException: ${e.message}: ${e.cause?.message ?: ""}",
                e,
            )
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
    parserOptions: ParserOptions?,
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

    return when {
        desiredColType != null ->
            column.convertTo(
                newType = desiredColType.toKType().withNullability(true),
                parserOptions = parserOptions,
            )

        else -> {
            val givenSkipTypes = parserOptions?.skipTypes ?: DataFrame.parser.skipTypes
            // no need to check for types that Deephaven already parses, skip those too
            val adjustedSkipTypes = givenSkipTypes + typesDeephavenAlreadyParses
            val adjustedParserOptions = (parserOptions ?: ParserOptions())
                .copy(skipTypes = adjustedSkipTypes)

            column.tryParse(adjustedParserOptions)
        }
    }
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

private fun CsvSpecs.Builder.skipLines(takeHeaderFromCsv: Boolean, skipLines: Long): CsvSpecs.Builder =
    if (takeHeaderFromCsv) {
        skipHeaderRows(skipLines)
    } else {
        skipRows(skipLines)
    }

/**
 * Sets the correct parsers for the csv, based on [colTypes] and [ParserOptions.skipTypes].
 * If [ColType.DEFAULT] is present, it sets the default parser.
 *
 * Logic overview:
 *
 * - if no [colTypes] are given
 *     - let deephaven use all its [default parsers][Parsers.DEFAULT] minus [Parsers.DATETIME]
 *     - subtract parsers of [skipTypes][ParserOptions.skipTypes] if those are supplied
 * - if [colTypes] are supplied
 *     - if [ColType.DEFAULT] is among the values
 *       - set the parser for each supplied column+colType
 *       - let deephaven use _only_ the parser given as [ColType.DEFAULT] type
 *     - if [ColType.DEFAULT] is not among the values
 *       - set the parser for each supplied column+coltype
 *       - let deephaven use all its [default parsers][Parsers.DEFAULT] minus [Parsers.DATETIME]
 *       - subtract parsers of [skipTypes][ParserOptions.skipTypes] if those are supplied
 *
 * We will not use [Deephaven's DateTime parser][Parsers.DATETIME].
 * This is done to avoid different behavior compared to [DataFrame.parse];
 * Deephaven parses [Instant] as [LocalDateTime]. [Issue #1047](https://github.com/Kotlin/dataframe/issues/1047)
 *
 * Note that `skipTypes` will never skip a type explicitly set by `colTypes`.
 * This is intended.
 */
private fun CsvSpecs.Builder.parsers(parserOptions: ParserOptions?, colTypes: Map<String, ColType>): CsvSpecs.Builder {
    for ((colName, colType) in colTypes) {
        if (colName == ColType.DEFAULT) continue
        putParserForName(colName, colType.toCsvParser())
    }
    // BOOLEAN, INT, LONG, DOUBLE, CHAR, STRING
    val defaultParsers = Parsers.DEFAULT - Parsers.DATETIME
    val skipTypes = parserOptions?.skipTypes ?: DataFrame.parser.skipTypes
    val parsersToUse = when {
        ColType.DEFAULT in colTypes ->
            listOf(colTypes[ColType.DEFAULT]!!.toCsvParser(), Parsers.STRING)

        skipTypes.isNotEmpty() -> {
            val parsersToSkip = skipTypes
                .mapNotNull { it.toColType().toCsvParserOrNull() }
            defaultParsers.toSet() - parsersToSkip.toSet()
        }

        else -> defaultParsers
    }
    parsers(parsersToUse)
    return this
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
internal fun ColType.toCsvParserOrNull(): Parser<*>? =
    when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Char -> Parsers.CHAR
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.String -> Parsers.STRING
        else -> null
    }

/**
 * Converts a [ColType] to a [Parser] from the Deephaven CSV library.
 * If no direct [Parser] exists, it defaults to [Parsers.STRING] so that [DataFrame.parse] can handle it.
 */
internal fun ColType.toCsvParser(): Parser<*> = toCsvParserOrNull() ?: Parsers.STRING

internal fun KType.toColType(): ColType =
    when (this.withNullability(false)) {
        typeOf<Int>() -> ColType.Int
        typeOf<Long>() -> ColType.Long
        typeOf<Double>() -> ColType.Double
        typeOf<Boolean>() -> ColType.Boolean
        typeOf<BigDecimal>() -> ColType.BigDecimal
        typeOf<BigInteger>() -> ColType.BigInteger
        typeOf<LocalDate>() -> ColType.LocalDate
        typeOf<LocalTime>() -> ColType.LocalTime
        typeOf<LocalDateTime>() -> ColType.LocalDateTime
        typeOf<String>() -> ColType.String
        typeOf<DeprecatedInstant>() -> ColType.DeprecatedInstant
        typeOf<StdlibInstant>() -> ColType.StdlibInstant
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
