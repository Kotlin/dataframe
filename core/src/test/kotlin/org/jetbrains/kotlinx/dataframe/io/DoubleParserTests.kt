package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.collections.shouldContainInOrder
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.impl.io.DecimalFormatBridgeImpl
import org.jetbrains.kotlinx.dataframe.impl.io.DoubleParser
import org.junit.Ignore
import org.junit.Test
import java.util.Locale

class DoubleParserTests {

    init {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug")
    }

    @Ignore
    @Test
    fun `can bridge from German locale`() {
        val numbers = listOf(
            "12,45",
            "-13,35",
            "100.123,35",
            "-204.235,23",
            "1,234e3",
        )

        val expectedStrings = listOf(
            "12.45",
            "-13.35",
            "100,123.35",
            "-204,235.23",
            "1.234e3",
        )
        // test bridge
        val bridge = DecimalFormatBridgeImpl(Locale.GERMANY, Locale.ROOT)

        // CharSequence
        numbers.map { bridge.convert(it) }.shouldContainInOrder(expectedStrings)

        // CharArray
        numbers.map { bridge.convert(it.toCharArray()).joinToString("") }.shouldContainInOrder(expectedStrings)

        // ByteArray
        for (charset in listOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)) {
            numbers.map { bridge.convert(it.toByteArray(charset), charset).toString(charset) }
                .shouldContainInOrder(expectedStrings)
        }
    }

    @Ignore
    @Test
    fun `can bridge from French locale`() {
        val numbers = listOf(
            "12,45",
            "-13,35",
            "100 123,35",
            "-204 235,23",
            "1,234e3",
        )

        val expectedStrings = listOf(
            "12.45",
            "-13.35",
            "100,123.35",
            "-204,235.23",
            "1.234e3",
        )
        // test bridge
        val bridge = DecimalFormatBridgeImpl(Locale.FRANCE, Locale.ROOT)

        // CharSequence
        numbers.map { bridge.convert(it) }.shouldContainInOrder(expectedStrings)

        // CharArray
        numbers.map { bridge.convert(it.toCharArray()).joinToString("") }.shouldContainInOrder(expectedStrings)

        // ByteArray
        for (charset in listOf(Charsets.UTF_8, Charsets.ISO_8859_1, Charsets.US_ASCII)) {
            numbers.map { bridge.convert(it.toByteArray(charset), charset).toString(charset) }
                .shouldContainInOrder(expectedStrings)
        }
    }

    @Ignore
    @Test
    fun `can bridge from Estonian locale`() {
        val numbers = listOf(
            "12,45",
            "−13,35", // note the different minus sign '−' vs '-'
            "100 123,35",
            "−204 235,23", // note the different minus sign '−' vs '-'
            "1,234e3",
            "-345,122", // check forgiving behavior with 'ordinary' minus sign
        )

        val expectedStrings = listOf(
            "12.45",
            "-13.35",
            "100,123.35",
            "-204,235.23",
            "1.234e3",
            "-345.122",
        )
        // test bridge
        val bridge = DecimalFormatBridgeImpl(Locale.forLanguageTag("et-EE"), Locale.ROOT)

        // CharSequence
        numbers.map { bridge.convert(it) }.shouldContainInOrder(expectedStrings)

        // CharArray
        numbers.map { bridge.convert(it.toCharArray()).joinToString("") }.shouldContainInOrder(expectedStrings)

        // ByteArray (must skip ASCII as NBSP and estonian minus sign are not ASCII)
        for (charset in listOf(Charsets.UTF_8, Charsets.ISO_8859_1)) {
            numbers.map { bridge.convert(it.toByteArray(charset), charset).toString(charset) }
                .shouldContainInOrder(expectedStrings)
        }
    }

    @Test
    fun `can fast parse doubles`() {
        val parser = DoubleParser(ParserOptions(locale = Locale.ROOT, useFastDoubleParser = true))

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
        val parser = DoubleParser(ParserOptions(locale = Locale.GERMANY, useFastDoubleParser = true))

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
        val parser = DoubleParser(ParserOptions(locale = Locale.FRANCE, useFastDoubleParser = true))

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
        val parser = DoubleParser(ParserOptions(locale = Locale.forLanguageTag("et-EE"), useFastDoubleParser = true))

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
