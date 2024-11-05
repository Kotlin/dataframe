package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources

/** [\["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"\]][DEFAULT_NULL_STRINGS] */
@ExcludeFromSources
internal interface DefaultNullStringsContentLink

/**
 * Default strings that are considered null:
 *
 * @include [DefaultNullStringsContentLink]
 */
public val DEFAULT_NULL_STRINGS: Set<String> =
    setOf("", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil")

/**
 * Default parsing options for reading delimited data like CSV and TSV.
 *
 * By default, we use [nullStrings][ParserOptions.nullStrings]`  =  `
 * {@include [DefaultNullStringsContentLink]} and
 * [useFastDoubleParser][ParserOptions.useFastDoubleParser]` = true`.
 */
public val DEFAULT_PARSER_OPTIONS: ParserOptions =
    ParserOptions(nullStrings = DEFAULT_NULL_STRINGS, useFastDoubleParser = true)

/**
 * Typealias for `CsvSpecs.Builder.(CsvSpecs.Builder) -> CsvSpecs.Builder`.
 * A lamdba where you can overwrite or adjust any of the CSV specs.
 */
public typealias AdjustCsvSpecs = CsvSpecs.Builder.(CsvSpecs.Builder) -> CsvSpecs.Builder

/**
 * Typealias for `CSVFormat.Builder.(CSVFormat.Builder) -> CSVFormat.Builder`.
 * A lamdba where you can overwrite or adjust any of the CSV format options.
 */
public typealias AdjustCSVFormat = CSVFormat.Builder.(CSVFormat.Builder) -> CSVFormat.Builder
