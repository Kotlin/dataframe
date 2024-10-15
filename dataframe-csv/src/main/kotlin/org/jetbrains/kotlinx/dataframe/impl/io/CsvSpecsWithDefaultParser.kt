package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.CsvSpecs
import io.deephaven.csv.parsers.Parser

/**
 * Extends the generated [CsvSpecs] such that [defaultParser] is used
 * when no parser is defined in [CsvSpecs.parserForName] or [CsvSpecs.parserForIndex] for any given column.
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
}
