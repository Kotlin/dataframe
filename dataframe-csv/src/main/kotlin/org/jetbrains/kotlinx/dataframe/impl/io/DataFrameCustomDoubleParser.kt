package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.containers.ByteSlice
import io.deephaven.csv.tokenization.Tokenizer.CustomDoubleParser
import org.jetbrains.kotlinx.dataframe.api.ParserOptions

/**
 * Wrapper around [DoubleParser] so we can use it from Deephaven.
 */
internal class DataFrameCustomDoubleParser(parserOptions: ParserOptions) : CustomDoubleParser {

    private val doubleParser = DoubleParser(parserOptions)

    override fun parse(bs: ByteSlice): Double =
        try {
            doubleParser.parseOrNull(bs.data(), bs.begin(), bs.size())
        } catch (e: Exception) {
            null
        } ?: throw NumberFormatException("Failed to parse double")

    override fun parse(cs: CharSequence): Double =
        doubleParser.parseOrNull(cs.toString())
            ?: throw NumberFormatException("Failed to parse double")
}
