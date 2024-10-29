package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.collections.shouldContainInOrder
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.FastDoubleParser
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale

private const val LOG_LEVEL = "org.slf4j.simpleLogger.defaultLogLevel"

class FastDoubleParserTests {

    private var loggerBefore: String? = null

    @Before
    fun setLogger() {
        loggerBefore = System.getProperty(LOG_LEVEL)
        System.setProperty(LOG_LEVEL, "debug")
    }

    @After
    fun restoreLogger() {
        if (loggerBefore != null) {
            System.setProperty(LOG_LEVEL, loggerBefore)
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
        val parser =
            FastDoubleParser(ParserOptions(locale = Locale.forLanguageTag("et-EE"), useFastDoubleParser = true))

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
}
