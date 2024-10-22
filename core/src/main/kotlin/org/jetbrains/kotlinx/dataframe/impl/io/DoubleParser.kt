package org.jetbrains.kotlinx.dataframe.impl.io

import ch.randelshofer.fastdoubleparser.ConfigurableDoubleParser
import ch.randelshofer.fastdoubleparser.JavaDoubleParser
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale
import kotlin.reflect.KFunction1

private val logger = KotlinLogging.logger {}

/**
 * Parses a [String]/[CharSequence], [CharArray], or [ByteArray] into a [Double].
 *
 * If [ParserOptions.useFastDoubleParser] is enabled, it will try to parse the input with a fast double parser,
 * [ConfigurableDoubleParser].
 * We use [ConfigurableDoubleParser] instead of the, even faster, [JavaDoubleParser] to
 * support different locales and fallback symbols.
 *
 * Public, so it can be used in other modules.
 * Open, so it may be modified.
 *
 * @param parserOptions can be supplied to configure the parser.
 *   We'll only use [ParserOptions.locale] and [ParserOptions.useFastDoubleParser].
 */
@Suppress("ktlint:standard:comment-wrapping")
public open class DoubleParser(private val parserOptions: ParserOptions) {

    protected val locale: Locale = parserOptions.locale ?: Locale.getDefault()
    protected val supportedFastCharsets: Set<Charset> = setOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)

    protected val localDecimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(locale)
    protected val fallbackDecimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ROOT)

    protected open val parser: ConfigurableDoubleParser =
        ConfigurableDoubleParser(
            /* symbols = */ setupNumberFormatSymbols(),
            /* ignoreCase = */ true,
        )

    /**
     * Sets up the [NumberFormatSymbols] for the [ConfigurableDoubleParser] based on
     * [localDecimalFormatSymbols] with fallbacks from [fallbackDecimalFormatSymbols].
     *
     * Fallback characters/strings are only added if they're not clashing with local characters/strings.
     */
    protected fun setupNumberFormatSymbols(): NumberFormatSymbols {
        // collect all chars and strings that are locale-specific such that we can check whether
        // fallback chars and strings are safe to add
        val localChars = buildSet {
            with(localDecimalFormatSymbols) {
                add(decimalSeparator.lowercaseChar())
                add(groupingSeparator.lowercaseChar())
                add(minusSign.lowercaseChar())
                add('+')
                add(zeroDigit.lowercaseChar())
            }
        }
        val localStrings = buildSet {
            with(localDecimalFormatSymbols) {
                add(exponentSeparator.lowercase())
                add(infinity.lowercase())
                add(naN.lowercase())
            }
        }

        /**
         * Builds a set with the specified char from [localDecimalFormatSymbols] and
         * its fallback char from [fallbackDecimalFormatSymbols] if it's safe to do so.
         * [additionals] will be added to the set as well when they're safe to add.
         */
        fun KFunction1<DecimalFormatSymbols, Char>.fromLocalWithFallBack(vararg additionals: Char): Set<Char> =
            buildSet {
                val char = this@fromLocalWithFallBack(localDecimalFormatSymbols).lowercaseChar()
                add(char)

                // add fallback char if it's safe to do so
                val fallbackChar = this@fromLocalWithFallBack(fallbackDecimalFormatSymbols).lowercaseChar()
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
         * [additionals] will be added to the set as well when they're safe to add.
         */
        fun KFunction1<DecimalFormatSymbols, String>.fromLocalWithFallBack(vararg additionals: String): Set<String> =
            buildSet {
                val string = this@fromLocalWithFallBack(localDecimalFormatSymbols).lowercase()
                add(string)

                // add fallback string if it's safe to do so
                val fallbackString = this@fromLocalWithFallBack(fallbackDecimalFormatSymbols).lowercase()
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
            .withInfinity(DecimalFormatSymbols::getInfinity.fromLocalWithFallBack("∞", "inf", "infinity", "infty"))
            .withNaN(DecimalFormatSymbols::getNaN.fromLocalWithFallBack("nan", "na", "n/a"))
    }

    // fallback method for parsing doubles
    protected open fun String.parseToDoubleOrNullFallback(): Double? =
        when (lowercase()) {
            "inf", "+inf", "infinity", "+infinity", "infty", "+infty", "∞", "+∞" -> Double.POSITIVE_INFINITY

            "-inf", "-infinity", "-infty", "-∞" -> Double.NEGATIVE_INFINITY

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
        }.also {
            if (it == null) {
                logger.debug { "Could not parse '$this' as Double with NumberFormat with locale '$locale'." }
            }
        }

    public open fun parseOrNull(ba: ByteArray, charset: Charset = Charsets.UTF_8): Double? {
        if (parserOptions.useFastDoubleParser && charset in supportedFastCharsets) {
            try {
                return parser.parseDouble(ba)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ba.toString(charset)
                    }' from a ByteArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }
        return ba.toString(charset).parseToDoubleOrNullFallback()
    }

    public open fun parseOrNull(cs: CharSequence): Double? {
        if (parserOptions.useFastDoubleParser) {
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

    public open fun parseOrNull(ca: CharArray): Double? {
        if (parserOptions.useFastDoubleParser) {
            try {
                return parser.parseDouble(ca)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ca.joinToString("")
                    }' as from a CharArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }

        return ca.joinToString("").parseToDoubleOrNullFallback()
    }
}
