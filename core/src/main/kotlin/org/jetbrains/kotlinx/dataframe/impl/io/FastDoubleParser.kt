package org.jetbrains.kotlinx.dataframe.impl.io

import ch.randelshofer.fastdoubleparser.ConfigurableDoubleParser
import ch.randelshofer.fastdoubleparser.NumberFormatSymbols
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.api.Parsers
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

private val logger = KotlinLogging.logger {}

/**
 * Parses a [String]/[CharSequence], [CharArray], or [ByteArray] into a [Double].
 *
 * If [ParserOptions.useFastDoubleParser] is enabled, it will try to parse the input with the
 * fast double parser library, [FastDoubleParser](https://github.com/wrandelshofer/FastDoubleParser).
 * If not, or if it fails, it will use [NumberFormat] to parse the input.
 *
 * The [locale][locale] used by the double parser is defined like:
 *
 *   [parserOptions][parserOptions]`?.`[locale][ParserOptions.locale]`  ?:  `[Parsers.locale][Parsers.locale]`  :?  `[Locale.getDefault()][Locale.getDefault]
 *
 * [FastDoubleParser] has a fallback mechanism; In practice, this means it can recognize symbols and notations
 * of any locale recognized by Java as long as that symbol does not conflict with the given locale.
 *
 * For example, if your locale uses ',' as decimal separator, it will NOT recognize ',' as thousands separator,
 * but it will recognize ' ', '٬', '_', ' ', etc. as such.
 * The same holds for characters like "e", "inf", "×10^", "NaN", etc.
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

    private val parser = ConfigurableDoubleParser(/* symbols = */ setupNumberFormatSymbols(), /* ignoreCase = */ true)

    /**
     * Sets up the [NumberFormatSymbols] for the [ConfigurableDoubleParser] based on
     * the [locale] with fallbacks from all other locales.
     *
     * Fallback characters/strings are only added if they're not clashing with local characters/strings.
     */
    private fun setupNumberFormatSymbols(): NumberFormatSymbols =
        numberFormatSymbolsCache.getOrPut(locale) {
            val localDecimalFormatSymbols = DecimalFormatSymbols.getInstance(locale)

            // collect all chars and strings that are locale-specific such that we can check whether
            // fallback chars and strings are safe to add
            val localChars = with(localDecimalFormatSymbols) {
                buildSet {
                    add(decimalSeparator.lowercaseChar())
                    add(groupingSeparator.lowercaseChar())
                    add(minusSign.lowercaseChar())
                    add('+')
                    // we don't include zeroDigit here, for notations like ×10^
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
             * Builds a set with the specified char from [this] and
             * [fallbackChars] will be added to the set too, when they're safe to add.
             */
            fun Char.withFallback(fallbackChars: CharArray): Set<Char> =
                buildSet {
                    val char = this@withFallback.lowercaseChar()
                    add(char)

                    // Treat NBSP and other whitespace characters the same.
                    if (char.isWhitespace()) addAll(WHITE_SPACES.asIterable())

                    // add fallback chars if needed
                    for (char in fallbackChars) {
                        val lowercase = char.lowercaseChar()
                        if (lowercase !in localChars && !localStrings.any { lowercase in it }) {
                            add(lowercase)
                        }

                        // Treat NBSP and other whitespace characters the same.
                        if (char.isWhitespace()) addAll(WHITE_SPACES.asIterable())
                    }
                }

            /**
             * Builds a set with the specified string from [this] and
             * [fallbackStrings] will be added to the set too, when they're safe to add.
             */
            fun String.withFallback(fallbackStrings: Array<String>): Set<String> =
                buildSet {
                    val string = this@withFallback.lowercase()
                    add(string)

                    // Treat NBSP and other whitespace characters the same.
                    if (string.isBlank()) addAll(WHITE_SPACES.map { it.toString() })

                    // add fallback strings if needed
                    for (string in fallbackStrings) {
                        val lowercase = string.lowercase()
                        if (!lowercase.any { it in localChars } && lowercase !in localStrings) {
                            add(lowercase)
                        }

                        // Treat NBSP and other whitespace characters the same.
                        if (string.isBlank()) addAll(WHITE_SPACES.map { it.toString() })
                    }
                }

            NumberFormatSymbols.fromDecimalFormatSymbols(localDecimalFormatSymbols)
                .withPlusSign(
                    setOf('+'),
                ).withDecimalSeparator(
                    localDecimalFormatSymbols.decimalSeparator.withFallback(DECIMAL_SEPARATORS),
                ).withGroupingSeparator(
                    localDecimalFormatSymbols.groupingSeparator.withFallback(GROUPING_SEPARATORS),
                ).withExponentSeparator(
                    localDecimalFormatSymbols.exponentSeparator.withFallback(EXPONENTS),
                ).withMinusSign(
                    localDecimalFormatSymbols.minusSign.withFallback(MINUS_SIGNS),
                ).withInfinity(
                    localDecimalFormatSymbols.infinity.withFallback(INFINITIES),
                ).withNaN(
                    localDecimalFormatSymbols.naN.withFallback(NANS),
                )
        }

    /** Fallback method for parsing doubles. */
    private fun String.parseToDoubleOrNullFallback(): Double? =
        when (lowercase()) {
            in INFINITIES, in PLUS_INFINITIES -> Double.POSITIVE_INFINITY

            in MINUS_INFINITIES -> Double.NEGATIVE_INFINITY

            in NANS -> Double.NaN

            else -> {
                // NumberFormat is not thread safe; must be created in the function body
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
                logger.trace { "Could not parse '$this' as Double with NumberFormat with locale '$locale'." }
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
                logger.trace(e) {
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
                logger.trace(e) {
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
                logger.trace(e) {
                    "Failed to parse '${
                        ca.joinToString("")
                    }' as from a CharArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }
        return String(chars = ca, offset = offset, length = length).parseToDoubleOrNullFallback()
    }

    /**
     * Here we store all possible decimal format symbols of all locales on the system.
     * These will be used as fallbacks for the selected locale.
     * They are only added by [withFallback] if they don't interfere with symbols already in the provided [locale]
     * (so ',' is not added as grouping separator if '.' is already the locale's decimal separator).
     */
    internal companion object {
        private val allDecimalFormatSymbols by lazy {
            Locale.getAvailableLocales().map { DecimalFormatSymbols.getInstance(it) }
        }
        val MINUS_SIGNS by lazy {
            allDecimalFormatSymbols.mapNotNullTo(mutableSetOf()) { it.minusSign }.toCharArray()
        }
        val INFINITIES by lazy {
            allDecimalFormatSymbols.mapNotNullTo(mutableSetOf()) { it.infinity }
                .plus(arrayOf("∞", "inf", "infinity", "infty"))
                .toTypedArray()
        }
        val PLUS_INFINITIES by lazy { INFINITIES.map { "+$it" }.toTypedArray() }
        val MINUS_INFINITIES by lazy {
            INFINITIES.flatMap { inf -> MINUS_SIGNS.map { min -> min + inf } }.toTypedArray()
        }
        val NANS by lazy {
            allDecimalFormatSymbols.mapNotNullTo(mutableSetOf()) { it.naN }
                .plus(arrayOf("nan", "na", "n/a"))
                .toTypedArray()
        }
        val WHITE_SPACES = charArrayOf(' ', '\u00A0', '\u2009', '\u202F', '\t')
        val GROUPING_SEPARATORS by lazy {
            allDecimalFormatSymbols.mapNotNullTo(mutableSetOf()) { it.groupingSeparator }
                .plus(arrayOf('\'', '˙', *WHITE_SPACES.toTypedArray()))
                .toCharArray()
        }
        val DECIMAL_SEPARATORS by lazy {
            allDecimalFormatSymbols.flatMapTo(mutableSetOf()) {
                listOfNotNull(it.decimalSeparator, it.monetaryDecimalSeparator)
            }.plus(arrayOf('·', '⎖'))
                .toCharArray()
        }
        val EXPONENTS by lazy {
            allDecimalFormatSymbols.mapNotNullTo(mutableSetOf()) { it.exponentSeparator }.toTypedArray()
        }
        val numberFormatSymbolsCache = mutableMapOf<Locale, NumberFormatSymbols>()
    }
}
