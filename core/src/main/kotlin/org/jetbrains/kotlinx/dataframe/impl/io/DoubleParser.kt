package org.jetbrains.kotlinx.dataframe.impl.io

import ch.randelshofer.fastdoubleparser.JavaDoubleParser
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

/**
 * Parses a [String]/[CharSequence], [CharArray], or [ByteArray] into a [Double].
 *
 * If [ParserOptions.useFastDoubleParser] is enabled, it will try to parse the input with a fast double parser,
 * [JavaDoubleParser]. This parser relies on [DecimalFormatBridge] to convert the input to a format that can be
 * parsed by [JavaDoubleParser] and thus may be unreliable.
 *
 * Public so it can be used in other modules.
 */
public class DoubleParser(private val parserOptions: ParserOptions) {

    private val locale = parserOptions.locale ?: Locale.getDefault()

    private val bridge
        get() = DecimalFormatBridge(from = locale, to = Locale.ROOT)

    // fallback method for parsing doubles
    private fun String.parseToDoubleOrNull(): Double? =
        when (lowercase()) {
            "inf", "+inf", "infinity", "+infinity" -> Double.POSITIVE_INFINITY

            "-inf", "-infinity" -> Double.NEGATIVE_INFINITY

            "nan", "na", "n/a" -> Double.NaN

            else -> {
                // not thread safe; must be created here
                val numberFormat = NumberFormat.getInstance(locale)

                val parsePosition = ParsePosition(0)
                val result = numberFormat.parse(this, parsePosition)?.toDouble()

                if (parsePosition.index != this.length || parsePosition.errorIndex != -1) {
                    null
                } else {
                    result
                }
            }
        }

    private val supportedFastCharsets = setOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)

    // parse a byte array of encoded strings into a double
    public fun parseOrNull(ba: ByteArray, charset: Charset = Charsets.UTF_8): Double? {
        if (parserOptions.useFastDoubleParser && charset in supportedFastCharsets) {
            try {
                // first try to parse with JavaDoubleParser by converting the locale to ROOT
                val array = bridge.convert(ba)

                return JavaDoubleParser.parseDouble(array)
            } catch (_: Exception) {
            }
        }
        return ba.toString(charset).parseToDoubleOrNull()
    }

    public fun parseOrNull(cs: CharSequence): Double? {
        if (parserOptions.useFastDoubleParser) {
            try {
                // first try to parse with JavaDoubleParser by converting the locale to ROOT
                val converted = bridge.convert(cs.toString())

                return JavaDoubleParser.parseDouble(converted)
            } catch (_: Exception) {
            }
        }

        return cs.toString().parseToDoubleOrNull()
    }

    public fun parseOrNull(ca: CharArray): Double? {
        if (parserOptions.useFastDoubleParser) {
            try {
                // first try to parse with JavaDoubleParser by converting the locale to ROOT
                val converted = bridge.convert(ca)

                return JavaDoubleParser.parseDouble(converted)
            } catch (_: Exception) {
            }
        }

        return ca.joinToString("").parseToDoubleOrNull()
    }
}

/**
 * Creates a bridge between two [DecimalFormatSymbols] instances, as to convert one
 * decimal string or byteArray to another decimal format without parsing it.
 *
 * This may be unreliable! It is recommended to use a proper parser instead, but for fast double parsing
 * it may be useful.
 *
 * For example:
 * ```kt
 * DecimalFormatBridge(from = Locale.GERMANY, to = Locale.ROOT)
 *   .convert("1.234,56") // results in "1,234.56"
 * ```
 *
 * May be removed if [Issue #82](https://github.com/wrandelshofer/FastDoubleParser/issues/82) is resolved.
 */
internal class DecimalFormatBridge private constructor(from: DecimalFormatSymbols, to: DecimalFormatSymbols) {

    companion object {

        private val memoizedBridges: MutableMap<Pair<Locale, Locale>, DecimalFormatBridge> = mutableMapOf()

        operator fun invoke(from: Locale, to: Locale): DecimalFormatBridge =
            memoizedBridges.getOrPut(from to to) {
                DecimalFormatBridge(
                    DecimalFormatSymbols.getInstance(from),
                    DecimalFormatSymbols.getInstance(to),
                )
            }
    }

    // whether the conversion is a no-op
    private val noop = from == to

    private val conversions: Map<Char, Char> = listOf(
        from.zeroDigit to to.zeroDigit,
        from.groupingSeparator to to.groupingSeparator,
        from.decimalSeparator to to.decimalSeparator,
        from.perMill to to.perMill,
        from.percent to to.percent,
        from.digit to to.digit,
        from.patternSeparator to to.patternSeparator,
        from.minusSign to to.minusSign,
        from.monetaryDecimalSeparator to to.monetaryDecimalSeparator,
    ).filter { it.first != it.second }
        .toMap()

    private val byteConversions = conversions
        .map { (from, to) ->
            from.code.toByte() to to.code.toByte()
        }.toMap()

    fun convert(input: String): String {
        if (noop || conversions.isEmpty()) return input
        return input.map { conversions[it] ?: it }.joinToString("")
    }

    fun convert(bytes: ByteArray): ByteArray {
        val copy = bytes.copyOf()
        convertInPlace(copy)
        return copy
    }

    fun convertInPlace(bytes: ByteArray) {
        if (noop || conversions.isEmpty()) return
        for (i in bytes.indices) {
            bytes[i] = byteConversions[bytes[i]] ?: bytes[i]
        }
    }

    fun convert(chars: CharArray): CharArray {
        val copy = chars.copyOf()
        convertInPlace(copy)
        return copy
    }

    fun convertInPlace(chars: CharArray) {
        if (noop || conversions.isEmpty()) return
        for (i in chars.indices) {
            chars[i] = conversions[chars[i]] ?: chars[i]
        }
    }
}
