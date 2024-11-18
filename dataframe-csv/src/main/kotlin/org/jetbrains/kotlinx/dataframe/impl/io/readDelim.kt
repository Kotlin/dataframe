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
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADJUST_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMPRESSION
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.FIXED_COLUMN_WIDTHS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HAS_FIXED_WIDTH_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.HEADER
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EMPTY_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_EXCESS_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.IGNORE_SURROUNDING_SPACES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.INPUT_STREAM_READ
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSER_OPTIONS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.PARSE_PARALLEL
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.QUOTE
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.READ_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.SKIP_LINES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.TRIM_INSIDE_QUOTED
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.AdjustCsvSpecs
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
 * @include [INPUT_STREAM_READ]
 * @param delimiter The field delimiter character. The default is ',' for CSV, '\t' for TSV.
 * @include [HEADER]
 * @include [COMPRESSION]
 * @include [COL_TYPES]
 * @include [SKIP_LINES]
 * @include [READ_LINES]
 * @include [HAS_FIXED_WIDTH_COLUMNS]
 * @include [FIXED_COLUMN_WIDTHS]
 * @include [PARSER_OPTIONS]
 * @include [IGNORE_EMPTY_LINES]
 * @include [ALLOW_MISSING_COLUMNS]
 * @include [IGNORE_EXCESS_COLUMNS]
 * @include [QUOTE]
 * @include [IGNORE_SURROUNDING_SPACES]
 * @include [TRIM_INSIDE_QUOTED]
 * @include [PARSE_PARALLEL]
 * @include [ADJUST_CSV_SPECS]
 */
internal fun readDelimImpl(
    inputStream: InputStream,
    delimiter: Char,
    header: List<String>,
    hasFixedWidthColumns: Boolean,
    fixedColumnWidths: List<Int>,
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
    adjustCsvSpecs: AdjustCsvSpecs,
): DataFrame<*> {
    // set up the csv specs
    val csvSpecs = with(CsvSpecs.builder()) {
        customDoubleParser(DataFrameCustomDoubleParser(parserOptions))
        nullValueLiterals(parserOptions.nullStrings ?: DEFAULT_NULL_STRINGS)
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

        // Deephaven's LocalDateTime parser is unconfigurable, so if the user provides a locale, pattern, or formatter,
        // we must use our own parser for LocalDateTime and let Deephaven read them as Strings.
        val useDeepHavenLocalDateTime = with(parserOptions) {
            locale == null && dateTimePattern == null && dateTimeFormatter == null
        }
        parsers(parserOptions, colTypes, useDeepHavenLocalDateTime)

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
 *     - let deephaven use all its [default parsers][Parsers.DEFAULT]
 *     - subtract parsers of [skipTypes][ParserOptions.skipTypes] if those are supplied
 * - if [colTypes] are supplied
 *     - if [ColType.DEFAULT] is among the values
 *       - set the parser for each supplied column+colType
 *       - let deephaven use _only_ the parser given as [ColType.DEFAULT] type
 *     - if [ColType.DEFAULT] is not among the values
 *       - set the parser for each supplied column+coltype
 *       - let deephaven use all its [default parsers][Parsers.DEFAULT]
 *       - subtract parsers of [skipTypes][ParserOptions.skipTypes] if those are supplied
 *
 * Note that `skipTypes` will never skip a type explicitly set by `colTypes`.
 * This is intended.
 */
private fun CsvSpecs.Builder.parsers(
    parserOptions: ParserOptions,
    colTypes: Map<String, ColType>,
    useDeepHavenLocalDateTime: Boolean,
): CsvSpecs.Builder {
    for ((colName, colType) in colTypes) {
        if (colName == ColType.DEFAULT) continue
        putParserForName(colName, colType.toCsvParser(useDeepHavenLocalDateTime))
    }
    val parsersToUse = when {
        ColType.DEFAULT in colTypes ->
            listOf(colTypes[ColType.DEFAULT]!!.toCsvParser(useDeepHavenLocalDateTime))

        parserOptions.skipTypes.isNotEmpty() -> {
            val parsersToSkip = parserOptions.skipTypes
                .mapNotNull { it.toColType().toCsvParserOrNull(useDeepHavenLocalDateTime) }
            Parsers.DEFAULT.toSet() - parsersToSkip.toSet()
        }

        else -> Parsers.DEFAULT // BOOLEAN, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
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
