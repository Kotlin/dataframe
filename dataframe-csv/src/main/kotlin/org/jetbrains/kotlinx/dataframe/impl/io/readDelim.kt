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
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ADDITIONAL_CSV_SPECS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.ALLOW_MISSING_COLUMNS
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COL_TYPES
import org.jetbrains.kotlinx.dataframe.documentation.DelimParams.COMPRESSION
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
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.Compression
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_NULL_STRINGS
import java.io.InputStream
import java.math.BigDecimal
import java.net.URL
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf
import kotlin.time.Duration

/**
 *
 * @include [INPUT_STREAM_READ]
 * @param delimiter The field delimiter character. The default is ',' for CSV, '\t' for TSV.
 * @include [HEADER]
 * @include [COMPRESSION]
 * @include [COL_TYPES]
 * @include [SKIP_LINES]
 * @include [READ_LINES]
 * @include [PARSER_OPTIONS]
 * @include [IGNORE_EMPTY_LINES]
 * @include [ALLOW_MISSING_COLUMNS]
 * @include [IGNORE_EXCESS_COLUMNS]
 * @include [QUOTE]
 * @include [IGNORE_SURROUNDING_SPACES]
 * @include [TRIM_INSIDE_QUOTED]
 * @include [PARSE_PARALLEL]
 * @include [ADDITIONAL_CSV_SPECS]
 */
internal fun readDelimImpl(
    inputStream: InputStream,
    delimiter: Char,
    header: List<String> = HEADER,
    compression: Compression<*> = COMPRESSION,
    colTypes: Map<String, ColType> = COL_TYPES,
    skipLines: Long = SKIP_LINES,
    readLines: Long? = READ_LINES,
    parserOptions: ParserOptions = PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = IGNORE_EXCESS_COLUMNS,
    quote: Char = QUOTE,
    ignoreSurroundingSpaces: Boolean = IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs? = ADDITIONAL_CSV_SPECS,
): DataFrame<*> {
    // set up the csv specs
    val csvSpecs = with(CsvSpecs.builder()) {
        if (additionalCsvSpecs != null) from(additionalCsvSpecs)
        customDoubleParser(FastDoubleParser(parserOptions))
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

        val useDeepHavenLocalDateTime = with(parserOptions) {
            locale == null && dateTimePattern == null && dateTimeFormatter == null
        }
        parsersWithOptions(parserOptions, useDeepHavenLocalDateTime)

        colTypes(colTypes, useDeepHavenLocalDateTime) // this function must be last, so the return value is used
    }.build()

    val csvReaderResult = inputStream.useSafely(compression) { safeInputStream ->
        if (safeInputStream.available() <= 0) {
            return if (header.isEmpty()) {
                DataFrame.empty()
            } else {
                dataFrameOf(
                    header.map {
                        DataColumn.createValueColumn(
                            name = it,
                            values = emptyList<String>(),
                            type = typeOf<String>(),
                        )
                    },
                )
            }
        }

        // read the csv
        try {
            CsvReader.read(
                csvSpecs,
                safeInputStream,
                ListSink.SINK_FACTORY,
            )
        } catch (e: CsvReaderException) {
            throw IllegalStateException("Could not read delimiter-separated data", e)
        }
    }

    val defaultColType = colTypes[DEFAULT_COL_TYPE]

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
    val columnData: List<Any?> = listSink.data
    val dataType = listSink.dataType
    val hasNulls = listSink.hasNulls
    val type = dataType().toKType().withNullability(hasNulls)

    val column = DataColumn.createValueColumn(
        name = name(),
        values = columnData,
        type = type,
    )
    if (dataType != STRING) return column

    // perform additional parsing if necessary
    column as ValueColumn<String?>

    return if (desiredColType == null) {
        // no need to check for types that Deephaven already parses
        val adjustedParserOptions = parserOptions.copy(
            skipTypes = parserOptions.skipTypes + typesDeephavenAlreadyParses,
        )
        column.tryParse(adjustedParserOptions)
    } else {
        column.tryParse(
            parserOptions.copy(
                skipTypes = ParserOptions.allTypesExcept(desiredColType.toKType()),
            ),
        )
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

/**
 * Sets correct parsers per name in [colTypes]. If [DEFAULT_COL_TYPE] is present, it sets the default parser.
 *
 * CAREFUL: Unlike the other functions on [CsvSpecs.Builder], this function can return a NEW builder instance.
 * Make sure to use the return value.
 */
private fun CsvSpecs.Builder.colTypes(
    colTypes: Map<String, ColType>,
    useDeepHavenLocalDateTime: Boolean,
): CsvSpecs.Builder {
    if (colTypes.isEmpty()) return this

    colTypes.entries.fold(this) { it, (colName, colType) ->
        it.putParserForName(colName, colType.toCsvParser(useDeepHavenLocalDateTime))
    }

    return if (DEFAULT_COL_TYPE in colTypes) {
        this.withDefaultParser(
            colTypes[DEFAULT_COL_TYPE]!!.toCsvParser(useDeepHavenLocalDateTime),
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
        parsers(
            Parsers.DEFAULT.toSet() -
                parserOptions.skipTypes
                    .mapNotNull { it.toColType().toCsvParserOrNull(useDeepHavenLocalDateTime) }
                    .toSet(),
        )
    }

private fun CsvSpecs.Builder.header(header: List<String>): CsvSpecs.Builder =
    if (header.isEmpty()) {
        // take header from csv
        hasHeaderRow(true)
    } else {
        hasHeaderRow(false)
            .headers(header)
    }

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

// has issues when calling to :core
internal fun ColType.toKType(): KType =
    when (this) {
        ColType.Int -> typeOf<Int>()
        ColType.Long -> typeOf<Long>()
        ColType.Double -> typeOf<Double>()
        ColType.Boolean -> typeOf<Boolean>()
        ColType.BigDecimal -> typeOf<BigDecimal>()
        ColType.LocalDate -> typeOf<LocalDate>()
        ColType.LocalTime -> typeOf<LocalTime>()
        ColType.LocalDateTime -> typeOf<LocalDateTime>()
        ColType.String -> typeOf<String>()
        ColType.Instant -> typeOf<Instant>()
        ColType.Duration -> typeOf<Duration>()
        ColType.Url -> typeOf<URL>()
        ColType.JsonArray -> typeOf<DataFrame<*>>()
        ColType.JsonObject -> typeOf<DataRow<*>>()
        ColType.Char -> typeOf<Char>()
    }

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
 */
internal val typesDeephavenAlreadyParses: Set<KType> =
    setOf(
        typeOf<Int>(),
        typeOf<Long>(),
        typeOf<Double>(),
        typeOf<Char>(),
        typeOf<Boolean>(),
//        typeOf<LocalDateTime>(), it cannot recognize all formats
//        typeOf<java.time.LocalDateTime>(), it cannot recognize all formats
    )
