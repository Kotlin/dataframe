package org.jetbrains.kotlinx.dataframe.impl.io

import ch.randelshofer.fastdoubleparser.ConfigurableDoubleParser
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.parser
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

private val logger = KotlinLogging.logger {}

// (lowercase) strings that are recognized to represent infinity and NaN in doubles in all locales
private val INFINITIES = arrayOf("∞", "inf", "infinity", "infty")
private val PLUS_INFINITIES = INFINITIES.map { "+$it" }
private val MINUS_INFINITIES = INFINITIES.map { "-$it" }
private val NANS = arrayOf("nan", "na", "n/a")

/**
 * Parses a [String]/[CharSequence], [CharArray], or [ByteArray] into a [Double].
 *
 * If [ParserOptions.useFastDoubleParser] is enabled, it will try to parse the input with an _EXPERIMENTAL_
 * fast double parser, [FastDoubleParser](https://github.com/wrandelshofer/FastDoubleParser).
 * If not, or if it fails, it will use [NumberFormat] to parse the input.
 *
 * Public, so it can be used in other modules.
 *
 * @param parserOptions can be supplied to configure the parser.
 *   If `null`, the global parser options ([DataFrame.parser][DataFrame.Companion.parser]) will be used.
 *   We'll only use [ParserOptions.locale] and [ParserOptions.useFastDoubleParser].
 */
@Suppress("ktlint:standard:comment-wrapping")
public class FastDoubleParser(private val parserOptions: ParserOptions? = null) {

    private val supportedFastCharsets = setOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)

    private val useFastDoubleParser = parserOptions?.useFastDoubleParser ?: Parsers.useFastDoubleParser
    private val locale = parserOptions?.locale ?: Parsers.locale
    private val fallbackLocale = Locale.ROOT

    private val localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(locale)
    private val fallbackDecimalFormatSymbols = DecimalFormatSymbols.getInstance(fallbackLocale)

    private val parser = ConfigurableDoubleParser(/* symbols = */ setupNumberFormatSymbols(), /* ignoreCase = */ true)

    /**
     * Sets up the [NumberFormatSymbols] for the [ConfigurableDoubleParser] based on
     * [localDecimalFormatSymbols] with fallbacks from [fallbackDecimalFormatSymbols].
     *
     * Fallback characters/strings are only added if they're not clashing with local characters/strings.
     */
    private fun setupNumberFormatSymbols(): NumberFormatSymbols {
        // collect all chars and strings that are locale-specific such that we can check whether
        // fallback chars and strings are safe to add
        val localChars = with(localDecimalFormatSymbols) {
            buildSet {
                add(decimalSeparator.lowercaseChar())
                add(groupingSeparator.lowercaseChar())
                add(minusSign.lowercaseChar())
                add('+')
                add(zeroDigit.lowercaseChar())
            }
        }
        val localStrings = with(localDecimalFormatSymbols) {
            buildSet {
                add(exponentSeparator.lowercase())
                add(infinity.lowercase())
                add(naN.lowercase())
            }
        }

        /**
         * Builds a set with the specified char from [localDecimalFormatSymbols] and
         * its fallback char from [fallbackDecimalFormatSymbols] if it's safe to do so.
         * [additionals] will be added to the set too, when they're safe to add.
         */
        fun ((DecimalFormatSymbols) -> Char).fromLocalWithFallBack(vararg additionals: Char): Set<Char> =
            buildSet {
                val getChar = this@fromLocalWithFallBack
                val char = getChar(localDecimalFormatSymbols).lowercaseChar()
                add(char)

                // add fallback char if it's safe to do so
                val fallbackChar = getChar(fallbackDecimalFormatSymbols).lowercaseChar()
                if (fallbackChar !in localChars && !localStrings.any { fallbackChar in it }) {
                    add(fallbackChar)
                }

                // Fixes NBSP and other whitespace characters not being recognized if the user writes space instead.
                if (char.isWhitespace()) add(' ')

                // add additional chars if needed
                for (additional in additionals) {
                    val lowercase = additional.lowercaseChar()
                    if (lowercase !in localChars && !localStrings.any { lowercase in it }) {
                        add(lowercase)
                    }
                }
            }

        /**
         * Builds a set with the specified string from [localDecimalFormatSymbols] and
         * its fallback string from [fallbackDecimalFormatSymbols] if it's safe to do so.
         * [additionals] will be added to the set too, when they're safe to add.
         */
        fun ((DecimalFormatSymbols) -> String).fromLocalWithFallBack(vararg additionals: String): Set<String> =
            buildSet {
                val getString = this@fromLocalWithFallBack
                val string = getString(localDecimalFormatSymbols).lowercase()
                add(string)

                // add fallback string if it's safe to do so
                val fallbackString = getString(fallbackDecimalFormatSymbols).lowercase()
                if (!fallbackString.any { it in localChars } && fallbackString !in localStrings) {
                    add(fallbackString)
                }

                // Fixes NBSP and other whitespace characters not being recognized if the user writes space instead.
                if (string.isBlank()) add(" ")

                // add additional strings if needed
                for (additional in additionals) {
                    val lowercase = additional.lowercase()
                    if (!lowercase.any { it in localChars } && lowercase !in localStrings) {
                        add(lowercase)
                    }
                }
            }

        return NumberFormatSymbols.fromDecimalFormatSymbols(localDecimalFormatSymbols)
            .withPlusSign(setOf('+'))
            .withDecimalSeparator(DecimalFormatSymbols::getDecimalSeparator.fromLocalWithFallBack())
            .withGroupingSeparator(DecimalFormatSymbols::getGroupingSeparator.fromLocalWithFallBack())
            .withExponentSeparator(DecimalFormatSymbols::getExponentSeparator.fromLocalWithFallBack())
            .withMinusSign(DecimalFormatSymbols::getMinusSign.fromLocalWithFallBack())
            .withInfinity(DecimalFormatSymbols::getInfinity.fromLocalWithFallBack(*INFINITIES))
            .withNaN(DecimalFormatSymbols::getNaN.fromLocalWithFallBack(*NANS))
    }

    /** Fallback method for parsing doubles. */
    private fun String.parseToDoubleOrNullFallback(): Double? =
        when (lowercase()) {
            in INFINITIES, in PLUS_INFINITIES -> Double.POSITIVE_INFINITY

            in MINUS_INFINITIES -> Double.NEGATIVE_INFINITY

            in NANS -> Double.NaN

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
        }.also {
            if (it == null) {
                logger.debug { "Could not parse '$this' as Double with NumberFormat with locale '$locale'." }
            }
        }

    /**
     * Parses a double value from a substring of the specified byte array.
     *
     * It uses the [fast double parser][ConfigurableDoubleParser] if [ParserOptions.useFastDoubleParser] is enabled,
     * else, or if that fails, it uses [parseToDoubleOrNullFallback].
     */
    public fun parseOrNull(
        ba: ByteArray,
        offset: Int = 0,
        length: Int = ba.size,
        charset: Charset = Charsets.UTF_8,
    ): Double? {
        if (useFastDoubleParser && charset in supportedFastCharsets) {
            try {
                return parser.parseDouble(ba, offset, length)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ba.toString(charset)
                    }' from a ByteArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }
        return String(bytes = ba, offset = offset, length = length, charset = charset)
            .parseToDoubleOrNullFallback()
    }

    /**
     * Parses a double value from the specified [CharSequence].
     *
     * It uses the [fast double parser][ConfigurableDoubleParser] if [ParserOptions.useFastDoubleParser] is enabled,
     * else, or if that fails, it uses [parseToDoubleOrNullFallback].
     */
    public fun parseOrNull(cs: CharSequence): Double? {
        if (useFastDoubleParser) {
            try {
                return parser.parseDouble(cs)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '$cs' from a CharSequence to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }

        return cs.toString().parseToDoubleOrNullFallback()
    }

    /**
     * Parses a double value from the specified [CharArray].
     *
     * It uses the [fast double parser][ConfigurableDoubleParser] if [ParserOptions.useFastDoubleParser] is enabled,
     * else, or if that fails, it uses [parseToDoubleOrNullFallback].
     */
    public fun parseOrNull(ca: CharArray, offset: Int = 0, length: Int = ca.size): Double? {
        if (useFastDoubleParser) {
            try {
                return parser.parseDouble(ca, offset, length)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ca.joinToString("")
                    }' as from a CharArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }
        return String(chars = ca, offset = offset, length = length).parseToDoubleOrNullFallback()
    }
}
