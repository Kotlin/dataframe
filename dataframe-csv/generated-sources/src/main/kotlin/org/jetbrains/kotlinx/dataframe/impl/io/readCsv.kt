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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.parse
import org.jetbrains.kotlinx.dataframe.api.tryParse
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE
import org.jetbrains.kotlinx.dataframe.io.toType
import java.io.InputStream
import java.util.zip.GZIPInputStream
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

/**
 *
 * @param inputStream Represents the CSV file to read.
 * @param delimiter The field delimiter character. ',' for CSV, 't' for TSV. The default is ','.
 * @param header if empty, the header will be read from the CSV file
 *   else, if not empty, the CSV will be read as header-less with [header][org.jetbrains.kotlinx.dataframe.impl.io.header] as the column titles.
 *   Combine with [skipLines][org.jetbrains.kotlinx.dataframe.impl.io.skipLines] if you want to overwrite a CSV header.
 *   The default is an empty list.
 * @param isCompressed If `true`, the input stream is compressed and will be decompressed before reading.
 *   The default is `false`.
 * @param colTypes A map of column names to their expected [ColType]s. Can be supplied to force
 *   the parser to interpret a column as a specific type, e.g. `colTypes = mapOf("colName" to ColType.Int)`.
 *   You can also supply a [ColType] for [DEFAULT_COL_TYPE][org.jetbrains.kotlinx.dataframe.io.DEFAULT_COL_TYPE] to set the default column type.
 *   The default is an empty map.
 * @param skipLines The number of lines to skip before reading the header and data.
 *   Useful for files with metadata or comments at the beginning.
 *   The default is 0.
 * @param readLines The number of lines to read from the input stream.
 *   If `null`, all lines will be read.
 *   The default is `null`.
 * @param parserOptions Optional parsing options for columns initially read as [String].
 *   Types that the CSV reader can already parse are automatically added to [ParserOptions.skipTypes].
 *   The default is are empty [ParserOptions].
 * @param ignoreEmptyLines If `true`, empty lines will be skipped.
 *   The default is `false`.
 * @param allowMissingColumns If this set to `true`, then rows that are too short
 *   (that have fewer columns than the header row) will be interpreted as if the missing columns contained
 *   the empty string.
 *   The default is `false`.
 * @param ignoreExcessColumns If this set to `true`, then rows that are too long
 *   (that have more columns than the header row) will have those excess columns dropped.
 *   The default is `false`.
 * @param quote The quote character (used when you want field or line delimiters to be interpreted as literal text.
 *
 * For example: `123,"hello, there",456,`
 *
 * Would be read as the three fields:
 * - `123`
 * - `hello, there`
 * - `456`
 *
 * The default is `'"'`.
 * @param ignoreSurroundingSpaces If `true`, leading and trailing blanks around non-quoted fields will be trimmed.
 *   The default is `true`.
 * @param trimInsideQuoted If `true`, leading and trailing blanks inside quoted fields will be trimmed.
 *   The default is `false`.
 * @param parseParallel If `true`, the CSV will be parsed in parallel using `runBlocking`.
 *   The default is `true`.
 * @param additionalCsvSpecs Optional [CsvSpecs] object to configure additional
 *   CSV parsing options not covered by the other parameters.
 *   The default is an empty [CsvSpecs].
 */
internal fun readCsvImpl(
    inputStream: InputStream,
    delimiter: Char = CsvParams.DELIMITER,
    header: List<String> = CsvParams.HEADER,
    isCompressed: Boolean = CsvParams.IS_COMPRESSED,
    colTypes: Map<String, ColType> = CsvParams.COL_TYPES,
    skipLines: Long = CsvParams.SKIP_LINES,
    readLines: Long? = CsvParams.READ_LINES,
    parserOptions: ParserOptions = CsvParams.PARSER_OPTIONS,
    ignoreEmptyLines: Boolean = CsvParams.IGNORE_EMPTY_LINES,
    allowMissingColumns: Boolean = CsvParams.ALLOW_MISSING_COLUMNS,
    ignoreExcessColumns: Boolean = CsvParams.IGNORE_EXCESS_COLUMNS,
    quote: Char = CsvParams.QUOTE,
    ignoreSurroundingSpaces: Boolean = CsvParams.IGNORE_SURROUNDING_SPACES,
    trimInsideQuoted: Boolean = CsvParams.TRIM_INSIDE_QUOTED,
    parseParallel: Boolean = CsvParams.PARSE_PARALLEL,
    additionalCsvSpecs: CsvSpecs = CsvParams.ADDITIONAL_CSV_SPECS,
): DataFrame<*> {
    // set up the csv specs
    val csvSpecs = with(CsvSpecs.builder()) {
        from(additionalCsvSpecs)
        parsers(Parsers.DEFAULT) // BOOLEAN, INT, LONG, DOUBLE, DATETIME, CHAR, STRING
        nullValueLiterals(parserOptions.nullStrings ?: defaultNullStrings)
        headerLegalizer(::legalizeHeader)
        numRows(readLines ?: Long.MAX_VALUE)
        ignoreEmptyLines(ignoreEmptyLines)
        allowMissingColumns(allowMissingColumns)
        ignoreExcessColumns(ignoreExcessColumns)
        delimiter(delimiter)
        quote(quote)
        ignoreSurroundingSpaces(ignoreSurroundingSpaces)
        trim(trimInsideQuoted)

        header(header)

        skipLines(takeHeaderFromCsv = header.isEmpty(), skipLines = skipLines)

        val useDeepHavenLocalDateTime = with(parserOptions) {
            locale == null && dateTimePattern == null && dateTimeFormatter == null
        }
        colTypes(colTypes, useDeepHavenLocalDateTime) // this function must be last, so the return value is used
    }.build()

    // edit the parser options to skip types that are already parsed by deephaven
    @Suppress("NAME_SHADOWING")
    val parserOptions = parserOptions.copy(
        skipTypes = parserOptions.skipTypes + typesDeephavenAlreadyParses,
    )

    // read the csv
    val csvReaderResult = CsvReader.read(
        csvSpecs,
        if (isCompressed) GZIPInputStream(inputStream) else inputStream,
        ListSink.SINK_FACTORY,
    )

    val defaultColType = colTypes[DEFAULT_COL_TYPE]

    // convert each ResultColumn to a DataColumn
    val cols =
        if (parseParallel) {
            runBlocking {
                csvReaderResult.map {
                    async {
                        it.toDataColumn(
                            parserOptions = parserOptions,
                            desiredColType = colTypes[it.name()] ?: defaultColType,
                        )
                    }
                }.awaitAll()
            }
        } else {
            csvReaderResult.map {
                it.toDataColumn(
                    parserOptions = parserOptions,
                    desiredColType = colTypes[it.name()] ?: defaultColType,
                )
            }
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
        column.tryParse(parserOptions)
    } else {
        parseColumnWithType(column, desiredColType.toType(), parserOptions)
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

private fun CsvSpecs.Builder.header(header: List<String>): CsvSpecs.Builder =
    if (header.isEmpty()) {
        // take header from csv
        hasHeaderRow(true)
    } else {
        hasHeaderRow(false)
            .headers(header)
    }

/**
 * TODO
 * Small hacky reflection-based solution to get the internal
 * [org.jetbrains.kotlinx.dataframe.impl.api.Parsers.nulls]
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
 * TODO
 * Hacky reflection-based solution to call internal functions:
 * ```kt
 * val parser = Parsers[type]!!
 * column.parse(parser, options)
 * ```
 */
internal fun parseColumnWithType(column: DataColumn<String?>, type: KType, options: ParserOptions?): DataColumn<*> {
    val clazz = Class.forName("org.jetbrains.kotlinx.dataframe.impl.api.Parsers")
    val objectInstanceField = clazz.getDeclaredField("INSTANCE")
    val parsersObjectInstance = objectInstanceField.get(null)
    val getFunction = clazz.getMethod("get", KType::class.java)
    val stringParser = getFunction.invoke(parsersObjectInstance, type)

    val parseClass = Class.forName("org.jetbrains.kotlinx.dataframe.impl.api.ParseKt")
    val parseMethod = parseClass.getMethod(
        "parse",
        DataColumn::class.java,
        Class.forName("org.jetbrains.kotlinx.dataframe.impl.api.StringParser"),
        ParserOptions::class.java,
    )

    val parsedCol = parseMethod.invoke(null, column, stringParser, options) as DataColumn<*>
    return parsedCol
}

/**
 * Converts a [ColType] to a [Parser] from the Deephaven CSV library.
 * If no direct [Parser] exists, it defaults to [Parsers.STRING] so that [DataFrame.parse] can handle it.
 */
internal fun ColType.toCsvParser(useDeepHavenLocalDateTime: Boolean): Parser<*> =
    when (this) {
        ColType.Int -> Parsers.INT
        ColType.Long -> Parsers.LONG
        ColType.Double -> Parsers.DOUBLE
        ColType.Char -> Parsers.CHAR
        ColType.Boolean -> Parsers.BOOLEAN
        ColType.String -> Parsers.STRING
        ColType.LocalDateTime -> if (useDeepHavenLocalDateTime) Parsers.DATETIME else Parsers.STRING
        else -> Parsers.STRING
    }

/**
 * Types that Deephaven already parses, so we can skip them.
 */
private val typesDeephavenAlreadyParses =
    setOf(
        typeOf<Int>(),
        typeOf<Long>(),
        typeOf<Double>(),
        typeOf<Char>(),
        typeOf<Boolean>(),
        typeOf<LocalDateTime>(),
        typeOf<java.time.LocalDateTime>(),
    )
