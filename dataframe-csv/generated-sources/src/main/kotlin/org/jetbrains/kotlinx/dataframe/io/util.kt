package org.jetbrains.kotlinx.dataframe.io

import io.deephaven.csv.CsvSpecs
import org.apache.commons.csv.CSVFormat
import org.jetbrains.kotlinx.dataframe.documentationCsv.ExcludeFromSources

/**
 * Default strings that are considered null when reading CSV / TSV / delim files:
 *
 * [["", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil"]][org.jetbrains.kotlinx.dataframe.io.DEFAULT_DELIM_NULL_STRINGS]
 */
public val DEFAULT_DELIM_NULL_STRINGS: Set<String> =
    setOf("", "NA", "N/A", "null", "NULL", "None", "none", "NIL", "nil")

/**
 * Typealias for `CsvSpecs.Builder.(CsvSpecs.Builder) -> CsvSpecs.Builder`.
 * A lambda where you can overwrite or adjust any of the CSV specs.
 */
public typealias AdjustCsvSpecs = CsvSpecs.Builder.(CsvSpecs.Builder) -> CsvSpecs.Builder

/**
 * Typealias for `CSVFormat.Builder.(CSVFormat.Builder) -> CSVFormat.Builder`.
 * A lambda where you can overwrite or adjust any of the CSV format options.
 */
public typealias AdjustCSVFormat = CSVFormat.Builder.(CSVFormat.Builder) -> CSVFormat.Builder
