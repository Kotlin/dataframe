package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.collections.shouldContainInOrder
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

// can be enabled for showing logs for these tests
private const val SHOW_LOGS = false

class FastDoubleParserTests {

    private val logLevel = "org.slf4j.simpleLogger.log.${FastDoubleParser::class.qualifiedName}"
    private var loggerBefore: String? = null

    @Before
    fun setLogger() {
        if (!SHOW_LOGS) return
        loggerBefore = System.getProperty(logLevel)
        System.setProperty(logLevel, "trace")
    }

    @After
    fun restoreLogger() {
        if (!SHOW_LOGS) return
        if (loggerBefore != null) {
            System.setProperty(logLevel, loggerBefore)
        }
    }

    @Test
    fun `can fast parse doubles`() {
        val parser = FastDoubleParser(ParserOptions(locale = Locale.ROOT, useFastDoubleParser = true))

        val numbers = listOf(
            "+12.45",
            "-13.35",
            "100123.35",
            "-204,235.23",
            "1.234e3",
            "3e-04", // failed with old double parser
            "nAn",
            "-N/a",
            "inf",
            "-InfinIty",
        )

        val expectedDoubles = listOf(
            12.45,
            -13.35,
            100_123.35,
            -204_235.23,
            1.234e3,
            3e-04,
            Double.NaN,
            -Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
        )

        // CharSequence
        numbers.map { parser.parseOrNull(it) }.shouldContainInOrder(expectedDoubles)

        // CharArray
        numbers.map { parser.parseOrNull(it.toCharArray()) }.shouldContainInOrder(expectedDoubles)

        // ByteArray
        numbers.map { parser.parseOrNull(it.toByteArray()) }.shouldContainInOrder(expectedDoubles)
    }

    @Test
    fun `can fast parse german locale`() {
        val parser = FastDoubleParser(ParserOptions(locale = Locale.GERMANY, useFastDoubleParser = true))

        val numbers = listOf(
            "12,45",
            "-13,35",
            "100.123,35",
            "-204.235,23",
            "1,234e3",
        )

        val expectedDoubles = listOf(
            12.45,
            -13.35,
            100_123.35,
            -204_235.23,
            1.234e3,
        )

        // CharSequence
        numbers.map { parser.parseOrNull(it) }.shouldContainInOrder(expectedDoubles)

        // CharArray
        numbers.map { parser.parseOrNull(it.toCharArray()) }.shouldContainInOrder(expectedDoubles)

        // ByteArray
        numbers.map { parser.parseOrNull(it.toByteArray()) }.shouldContainInOrder(expectedDoubles)
    }

    @Test
    fun `can fast parse french locale`() {
        val parser = FastDoubleParser(ParserOptions(locale = Locale.FRANCE, useFastDoubleParser = true))

        val numbers = listOf(
            "12,45",
            "-13,35",
            "100 123,35",
            "-204 235,23",
            "1,234e3",
        )

        val expectedDoubles = listOf(
            12.45,
            -13.35,
            100_123.35,
            -204_235.23,
            1.234e3,
        )

        // CharSequence
        numbers.map { parser.parseOrNull(it) }.shouldContainInOrder(expectedDoubles)

        // CharArray
        numbers.map { parser.parseOrNull(it.toCharArray()) }.shouldContainInOrder(expectedDoubles)

        // ByteArray
        numbers.map { parser.parseOrNull(it.toByteArray()) }.shouldContainInOrder(expectedDoubles)
    }

    @Test
    fun `can fast parse estonian locale`() {
        val parser = FastDoubleParser(
            ParserOptions(locale = Locale.forLanguageTag("et-EE"), useFastDoubleParser = true),
        )

        val numbers = listOf(
            "12,45",
            "−13,35", // note the different minus sign '−' vs '-'
            "100 123,35",
            "−204 235,23", // note the different minus sign '−' vs '-'
            "1,234e3",
            "-345,122", // check forgiving behavior with 'ordinary' minus sign
        )

        val expectedDoubles = listOf(
            12.45,
            -13.35,
            100_123.35,
            -204_235.23,
            1.234e3,
            -345.122,
        )

        // CharSequence
        numbers.map { parser.parseOrNull(it) }.shouldContainInOrder(expectedDoubles)

        // CharArray
        numbers.map { parser.parseOrNull(it.toCharArray()) }.shouldContainInOrder(expectedDoubles)

        // ByteArray
        numbers.map { parser.parseOrNull(it.toByteArray()) }.shouldContainInOrder(expectedDoubles)
    }

    @Test
    fun `fast parse any locale`() {
        val locales = Locale.getAvailableLocales()
        val doubles = listOf(
            12.45,
            -12.45,
            100_123.35,
            -204_235.23,
            1.234e3,
            -345.122,
            0.0,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            Double.NaN,
        )

        for (locale in locales) {
            val parser = FastDoubleParser(ParserOptions(locale = locale, useFastDoubleParser = true))
            val formatter = NumberFormat.getInstance(locale)
            for (double in doubles) {
                val formatted = formatter.format(double)
                val parsedByNumberFormatter = formatter.parse(formatted)?.toDouble()

                val parsedString = parser.parseOrNull(formatted)
                assert(double == parsedString || (double.isNaN() && parsedString?.isNaN() == true)) {
                    "Failed to parse $formatted with locale $locale. Expected $double, got $parsedString. NumberFormat parsed it like: $parsedByNumberFormatter"
                }

                val parsedCharArray = parser.parseOrNull(formatted.toCharArray())
                assert(double == parsedCharArray || (double.isNaN() && parsedCharArray?.isNaN() == true)) {
                    "Failed to parse $formatted with locale $locale. Expected $double, got $parsedCharArray. NumberFormat parsed it like: $parsedByNumberFormatter"
                }

                val parsedByteArray = parser.parseOrNull(formatted.toByteArray())
                assert(double == parsedByteArray || (double.isNaN() && parsedByteArray?.isNaN() == true)) {
                    "Failed to parse $formatted with locale $locale. Expected $double, got $parsedByteArray. NumberFormat parsed it like: $parsedByNumberFormatter"
                }
            }
        }
    }
}
