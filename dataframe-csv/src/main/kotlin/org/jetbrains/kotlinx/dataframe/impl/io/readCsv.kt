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
 * @include [CsvParams.INPUT_STREAM]
 * @include [CsvParams.DELIMITER]
 * @include [CsvParams.HEADER]
 * @include [CsvParams.IS_COMPRESSED]
 * @include [CsvParams.COL_TYPES]
 * @include [CsvParams.SKIP_LINES]
 * @include [CsvParams.READ_LINES]
 * @include [CsvParams.PARSER_OPTIONS]
 * @include [CsvParams.IGNORE_EMPTY_LINES]
 * @include [CsvParams.ALLOW_MISSING_COLUMNS]
 * @include [CsvParams.IGNORE_EXCESS_COLUMNS]
 * @include [CsvParams.QUOTE]
 * @include [CsvParams.IGNORE_SURROUNDING_SPACES]
 * @include [CsvParams.TRIM_INSIDE_QUOTED]
 * @include [CsvParams.PARSE_PARALLEL]
 * @include [CsvParams.ADDITIONAL_CSV_SPECS]
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
