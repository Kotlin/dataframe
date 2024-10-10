package org.jetbrains.kotlinx.dataframe.impl.io

import ch.randelshofer.fastdoubleparser.JavaDoubleParser
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import java.nio.charset.Charset
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParsePosition
import java.util.Locale

private val logger = KotlinLogging.logger {}

/**
 * Parses a [String]/[CharSequence], [CharArray], or [ByteArray] into a [Double].
 *
 * If [ParserOptions.useFastDoubleParser] is enabled, it will try to parse the input with a fast double parser,
 * [JavaDoubleParser]. This parser relies on [DecimalFormatBridge] to convert the input to a format that can be
 * parsed by [JavaDoubleParser] and thus may be unreliable.
 *
 * Public, so it can be used in other modules.
 * Open, so it may be modified with your own (better) [DecimalFormatBridge].
 */
public open class DoubleParser(private val parserOptions: ParserOptions) {

    protected val locale: Locale = parserOptions.locale ?: Locale.getDefault()

    protected val supportedFastCharsets: Set<Charset> = setOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)

    private val targetDecimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.ROOT)
        .also {
            it.groupingSeparator = Char.MIN_VALUE // remove groupingSeparator for FastJavaDoubleParser
        }
    protected open val bridge: DecimalFormatBridge
        get() = DecimalFormatBridgeImpl(
            from = DecimalFormatSymbols.getInstance(locale),
            to = targetDecimalFormatSymbols,
        )

    // fallback method for parsing doubles
    protected fun String.parseToDoubleOrNull(): Double? =
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
        }.also {
            if (it == null) {
                logger.debug { "Could not parse '$this' as Double with NumberFormat with locale '$locale'." }
            }
        }

    // parse a byte array of encoded strings into a double
    public open fun parseOrNull(ba: ByteArray, charset: Charset = Charsets.UTF_8): Double? {
        if (parserOptions.useFastDoubleParser && charset in supportedFastCharsets) {
            // first try to parse with JavaDoubleParser by converting the locale to ROOT
            val array = bridge.convert(ba, charset)
            try {
                return JavaDoubleParser.parseDouble(array)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ba.toString(charset)
                    }' as '${
                        array.toString(charset)
                    }' from a ByteArray to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }
        return ba.toString(charset).parseToDoubleOrNull()
    }

    public open fun parseOrNull(cs: CharSequence): Double? {
        if (parserOptions.useFastDoubleParser) {
            // first try to parse with JavaDoubleParser by converting the locale to ROOT
            val converted = bridge.convert(cs.toString())
            try {
                return JavaDoubleParser.parseDouble(converted)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '$cs' as '$converted' from a CharSequence to Double with FastDoubleParser with locale '$locale'."
                }
            }
        }

        return cs.toString().parseToDoubleOrNull()
    }

    public open fun parseOrNull(ca: CharArray): Double? {
        if (parserOptions.useFastDoubleParser) {
            // first try to parse with JavaDoubleParser by converting the locale to ROOT
            val converted = bridge.convert(ca)
            try {
                return JavaDoubleParser.parseDouble(converted)
            } catch (e: Exception) {
                logger.debug(e) {
                    "Failed to parse '${
                        ca.joinToString("")
                    }' as '${
                        converted.joinToString("")
                    }' from a CharArray to Double with FastDoubleParser with locale '$locale'."
                }
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
public interface DecimalFormatBridge {

    public fun convert(string: String): String

    public fun convert(bytes: ByteArray, charset: Charset): ByteArray

    public fun convert(chars: CharArray): CharArray
}

/**
 * Implementation of [DecimalFormatBridge] which replaces individual characters in [DecimalFormatSymbols]
 * of the source into those of the destination [DecimalFormatSymbols].
 *
 * This catches most cases, but definitely not all.
 *
 * NOTE: Strings are not replaced.
 */
internal class DecimalFormatBridgeImpl private constructor(from: DecimalFormatSymbols, to: DecimalFormatSymbols) :
    DecimalFormatBridge {

        companion object {

            private val memoizedBridges:
                MutableMap<Pair<DecimalFormatSymbols, DecimalFormatSymbols>, DecimalFormatBridge> =
                mutableMapOf()

            operator fun invoke(from: Locale, to: Locale): DecimalFormatBridge =
                invoke(DecimalFormatSymbols.getInstance(from), DecimalFormatSymbols.getInstance(to))

            operator fun invoke(from: DecimalFormatSymbols, to: DecimalFormatSymbols): DecimalFormatBridge =
                memoizedBridges.getOrPut(Pair(from, to)) {
                    DecimalFormatBridgeImpl(
                        from = from,
                        to = to,
                    )
                }
        }

        // whether the conversion is a no-op
        private val noop = from == to

        private val charConversions: Map<Char, Char> = listOf(
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

        private fun getBytesConversions(charset: Charset): List<Pair<ByteArray, ByteArray>> =
            charConversions.map { (from, to) ->
                Pair(
                    "$from".toByteArray(charset),
                    if (to == Char.MIN_VALUE) byteArrayOf() else "$to".toByteArray(charset),
                )
            }
                .sortedByDescending { it.first.size }
                .distinctBy { it.first.toString(charset) }

        /**
         * Lazy map (to be filled with [getBytesConversions]) per charset of [charConversions]
         * that contains the char-as-byteArray version of [charConversions].
         * Useful if you want to convert a [String] as [ByteArray] to another [Locale].
         */
        private val bytesConversions: MutableMap<Charset, List<Pair<ByteArray, ByteArray>>> = mutableMapOf()

        override fun convert(string: String): String {
            if (noop || charConversions.isEmpty()) return string
            return string.map { charConversions[it] ?: it }.joinToString("")
        }

        override fun convert(bytes: ByteArray, charset: Charset): ByteArray {
            val conversionMap = bytesConversions
                .getOrPut(charset) { getBytesConversions(charset) }

            // gets all replacements as (index, original byte count, replacement) triples
            val replacements = conversionMap.flatMap { (from, to) ->
                bytes.findAllSubsequenceIndices(from).map { Triple(it, from.size, to) }
            }

            // change bytes into a list of byte arrays, where each byte array is a single byte
            val adjustableBytes = bytes.map { byteArrayOf(it) }.toMutableList()

            // perform each replacement in adjustableBytes
            // if the new replacement is shorter than the original, remove bytes after the replacement
            // if the new replacement is longer than the original, well that's why we have a byte array per original byte
            for ((i, originalByteCount, replacement) in replacements) {
                adjustableBytes[i] = replacement
                for (j in (i + 1)..<(i + originalByteCount)) {
                    adjustableBytes[j] = byteArrayOf()
                }
            }

            // flatten the list of byte arrays into a single byte array
            val result = ByteArray(adjustableBytes.sumOf { it.size })
            var i = 0
            for (it in adjustableBytes) {
                if (it.isEmpty()) continue
                System.arraycopy(it, 0, result, i, it.size)
                i += it.size
            }

            return result
        }

        override fun convert(chars: CharArray): CharArray {
            val copy = chars.copyOf()
            convertInPlace(copy)
            return copy
        }

        fun convertInPlace(chars: CharArray) {
            if (noop || charConversions.isEmpty()) return
            for (i in chars.indices) {
                chars[i] = charConversions[chars[i]] ?: chars[i]
            }
        }
    }

// Helper function to find all subsequences in a byte array
internal fun ByteArray.findAllSubsequenceIndices(subBytes: ByteArray): List<Int> {
    val result = mutableListOf<Int>()
    if (subBytes.isEmpty()) {
        return result
    }

    val lenMain = this.size
    val lenSub = subBytes.size

    var i = 0
    while (i <= lenMain - lenSub) {
        var match = true
        for (j in subBytes.indices) {
            if (this[i + j] != subBytes[j]) {
                match = false
                break
            }
        }

        if (match) {
            result.add(i)
            i += lenSub // Move i forward by the length of the subsequence to avoid overlapping matches
        } else {
            i++
        }
    }

    return result
}
