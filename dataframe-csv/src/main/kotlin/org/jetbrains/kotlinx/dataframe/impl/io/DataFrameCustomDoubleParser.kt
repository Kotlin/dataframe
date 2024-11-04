package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.containers.ByteSlice
import io.deephaven.csv.tokenization.Tokenizer.CustomDoubleParser
import org.jetbrains.kotlinx.dataframe.api.ParserOptions

/**
 * Wrapper around [FastDoubleParser] so we can use it from Deephaven.
 */
internal class DataFrameCustomDoubleParser(parserOptions: ParserOptions) : CustomDoubleParser {

    private val fastDoubleParser = FastDoubleParser(parserOptions)

    override fun parse(bs: ByteSlice): Double =
        try {
            fastDoubleParser.parseOrNull(bs.data(), bs.begin(), bs.size())
        } catch (e: Exception) {
            null
        } ?: throw NumberFormatException("Failed to parse double")

    override fun parse(cs: CharSequence): Double =
        fastDoubleParser.parseOrNull(cs.toString())
            ?: throw NumberFormatException("Failed to parse double")
}
