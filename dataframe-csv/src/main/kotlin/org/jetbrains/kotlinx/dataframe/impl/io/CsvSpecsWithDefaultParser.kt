package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.Parser
import io.deephaven.csv.tokenization.Tokenizer
import java.util.function.Function
import java.util.function.Predicate

/**
 * Extends the generated [CsvSpecs] such that [defaultParser] is used
 * when no parser is defined in [CsvSpecs.parserForName] or [CsvSpecs.parserForIndex] for any given column.
 *
 * Requested as feature: https://github.com/deephaven/deephaven-csv/issues/221
 */
internal fun CsvSpecs.Builder.withDefaultParser(defaultParser: Parser<*>): CsvSpecs.Builder =
    CsvSpecsBuilderWithDefaultParser(csvSpecsBuilder = this, defaultParser = defaultParser)

private class CsvSpecsBuilderWithDefaultParser(
    private val csvSpecsBuilder: CsvSpecs.Builder,
    private val defaultParser: Parser<*>,
) : CsvSpecs.Builder by csvSpecsBuilder {

    override fun build() =
        CsvSpecsWithDefaultParser(
            csvSpecs = csvSpecsBuilder.build(),
            defaultParser = defaultParser,
        )
}

private class CsvSpecsWithDefaultParser(val csvSpecs: CsvSpecs, val defaultParser: Parser<*>) : CsvSpecs() {

    inner class MapWithDefaultParser<T>(private val map: Map<T, Parser<*>>) : Map<T, Parser<*>> by map {
        override fun get(key: T): Parser<*> = map[key] ?: defaultParser
    }

    override fun parserForName(): Map<String, Parser<*>> = MapWithDefaultParser(csvSpecs.parserForName())

    override fun parserForIndex(): Map<Int, Parser<*>> = MapWithDefaultParser(csvSpecs.parserForIndex())

    override fun headers(): List<String> = csvSpecs.headers()

    override fun headerForIndex(): Map<Int, String> = csvSpecs.headerForIndex()

    override fun nullValueLiteralsForName(): Map<String, List<String>> = csvSpecs.nullValueLiteralsForName()

    override fun nullValueLiteralsForIndex(): Map<Int, List<String>> = csvSpecs.nullValueLiteralsForIndex()

    override fun parsers(): List<Parser<*>> = csvSpecs.parsers()

    override fun nullValueLiterals(): List<String> = csvSpecs.nullValueLiterals()

    override fun nullParser(): Parser<*>? = csvSpecs.nullParser()

    override fun customDoubleParser(): Tokenizer.CustomDoubleParser = csvSpecs.customDoubleParser()

    override fun customTimeZoneParser(): Tokenizer.CustomTimeZoneParser? = csvSpecs.customTimeZoneParser()

    override fun headerLegalizer(): Function<Array<String>, Array<String>> = csvSpecs.headerLegalizer()

    override fun headerValidator(): Predicate<String> = csvSpecs.headerValidator()

    override fun skipRows(): Long = csvSpecs.skipRows()

    override fun numRows(): Long = csvSpecs.numRows()

    override fun ignoreEmptyLines(): Boolean = csvSpecs.ignoreEmptyLines()

    override fun allowMissingColumns(): Boolean = csvSpecs.allowMissingColumns()

    override fun ignoreExcessColumns(): Boolean = csvSpecs.ignoreExcessColumns()

    override fun hasHeaderRow(): Boolean = csvSpecs.hasHeaderRow()

    override fun skipHeaderRows(): Long = csvSpecs.skipHeaderRows()

    override fun delimiter(): Char = csvSpecs.delimiter()

    override fun quote(): Char = csvSpecs.quote()

    override fun ignoreSurroundingSpaces(): Boolean = csvSpecs.ignoreSurroundingSpaces()

    override fun trim(): Boolean = csvSpecs.trim()

    override fun concurrent(): Boolean = csvSpecs.concurrent()
}
