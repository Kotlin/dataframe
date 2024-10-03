package org.jetbrains.kotlinx.dataframe.impl.io

import io.deephaven.csv.containers.ByteSlice
import io.deephaven.csv.tokenization.Tokenizer.CustomDoubleParser

internal object DoubleParser : CustomDoubleParser {

    private fun String.toDouble(): Double =
        when (this.lowercase()) {
            "inf", "+inf" -> Double.POSITIVE_INFINITY
            "-inf" -> Double.NEGATIVE_INFINITY
            "nan", "na", "n/a" -> Double.NaN
            else -> java.lang.Double.parseDouble(this)
        }

    override fun parse(bs: ByteSlice?): Double = bs.toString().toDouble()

    override fun parse(cs: CharSequence?): Double = cs.toString().toDouble()
}
