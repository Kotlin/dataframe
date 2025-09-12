package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.blue
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.red
import org.jetbrains.kotlinx.dataframe.api.FormattingDsl.rgb
import org.jetbrains.kotlinx.dataframe.io.DisplayConfiguration
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.isHappy
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test

class FormatTests : TestBase() {

    @Test
    fun `basic format with background color`() {
        val formatted = df.format { age }.with { background(red) }
        val html = formatted.toHtml().toString()

        // Should contain CSS background-color styling
        (html.split("background-color:#ff0000").size - 1) shouldBe 7
        // Format operation should produce a FormattedFrame
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format with text color`() {
        val formatted = df.format { age }.with { textColor(blue) }
        val html = formatted.toHtml().toString()

        (html.split("color:#0000ff").size - 1) shouldBe 14
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format with multiple attributes using and`() {
        val formatted = df.format { age }.with { background(white) and textColor(black) and bold }
        val html = formatted.toHtml().toString()

        (html.split("background-color:#ffffff").size - 1) shouldBe 7
        (html.split("color:#000000").size - 1) shouldBe 14
        (html.split("font-weight:bold").size - 1) shouldBe 7
        (html.split("font-weight:bold").size - 1) shouldBe 7
    }

    @Test
    fun `format with italic and underline`() {
        val formatted = df.format { age }.with { italic and underline }
        val html = formatted.toHtml().toString()

        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
    }

    @Test
    fun `format with italic and underline nested in group`() {
        val formatted = df.format { name.firstName }.with { italic and underline }
        val html = formatted.toHtml().toString()

        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
    }

    @Test
    fun `format with italic and underline for entire group`() {
        val formatted = df
            .format().with { background(white) }
            .format { age }.with { background(blue) and textColor(white) }
            .format { name }.with { background(green) }
            .format { name.firstName }.with { italic and underline }
            .format { name.colsOf<String>() }.where { it.startsWith("C") }.with { background(red) }

        val html = formatted.toHtml().toString()

        (html.split("background-color:#ffffff").size - 1) shouldBe 21
        (html.split("background-color:#00ff00").size - 1) shouldBe 16
        (html.split("background-color:#0000ff").size - 1) shouldBe 7
        (html.split("background-color:#ff0000").size - 1) shouldBe 5
        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("font-style:italic").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
        (html.split("text-decoration:underline").size - 1) shouldBe 7
    }

    @Test
    fun `format with custom rgb color`() {
        val customColor = rgb(128, 64, 192)
        val formatted = df.format { age }.with { background(customColor) }
        val html = formatted.toHtml().toString()

        // Custom color should be applied
        (html.split("background-color:#8040c0").size - 1) shouldBe 7
    }

    @Test
    fun `format with custom attribute`() {
        val formatted = df.format { age }.with { attr("text-align", "center") }
        val html = formatted.toHtml().toString()

        val occurrences = html.split("text-align:center").size - 1
        occurrences shouldBe 7
    }

    @Test
    fun `format with where clause`() {
        val formatted = df.format { age }.where { it > 30 }.with { background(red) }
        val html = formatted.toHtml().toString()

        // Should contain styling but only for cells where age > 30
        val occurrences = html.split("background-color:#ff0000").size - 1
        occurrences shouldBe 2 // Two cells where age > 30
    }

    @Test
    fun `format with at specific rows`() {
        val formatted = df.format { age }.at(0, 2, 4, 9999).with { background(green) }
        val html = formatted.toHtml().toString()

        val occurrences = html.split("background-color:#00ff00").size - 1
        occurrences shouldBe 3
    }

    @Test
    fun `format with at range`() {
        val formatted = df.format { age }.at(1..3).with { background(blue) }
        val html = formatted.toHtml().toString()

        val occurrences = html.split("background-color:#0000ff").size - 1
        occurrences shouldBe 3
    }

    @Test
    fun `format with notNull filter`() {
        val formatted = df.format { weight }.notNull().with { background(green) }
        val html = formatted.toHtml().toString()

        // Should only format non-null weight values
        val occurrences = html.split("background-color:#00ff00").size - 1
        occurrences shouldBe 5
    }

    @Test
    fun `format with notNull shorthand`() {
        val formatted = df.format { weight }.notNull { background(red) and bold }
        val html = formatted.toHtml().toString()

        html.split("background-color:#ff0000").size - 1 shouldBe 5
        html.split("font-weight:bold").size - 1 shouldBe 5
    }

    @Test
    fun `format with perRowCol`() {
        val formatted = df.format { age }.perRowCol { row, col ->
            if (col[row] > 25) background(red) else background(green)
        }
        val html = formatted.toHtml().toString()

        // Should contain formatting based on age values
        val occurrences = html.split("background-color:#00ff00").size - 1
        occurrences shouldBe 3

        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format with linearBg`() {
        val formatted = df.format { age }.linearBg(15 to blue, 45 to red)
        val html = formatted.toHtml().toString()

        (html.split("background-color:#0000ff").size - 1) shouldBe 1
        (html.split("background-color:#2a00d4").size - 1) shouldBe 2
        (html.split("background-color:#d4002a").size - 1) shouldBe 1
        (html.split("background-color:#7f007f").size - 1) shouldBe 2
        (html.split("background-color:#2a00d4").size - 1) shouldBe 2
        (html.split("background-color:#7f007f").size - 1) shouldBe 2
        (html.split("background-color:#ff0000").size - 1) shouldBe 1
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format with linear color interpolation`() {
        val formatted = df.format { age }.with { value ->
            textColor(linear(value, 15 to blue, 45 to red))
        }
        val html = formatted.toHtml().toString()

        (html.split("color:#0000ff").size - 1) shouldBe 2
        (html.split("color:#2a00d4").size - 1) shouldBe 4
        (html.split("color:#d4002a").size - 1) shouldBe 2
        (html.split("color:#7f007f").size - 1) shouldBe 4
        (html.split("color:#2a00d4").size - 1) shouldBe 4
        (html.split("color:#7f007f").size - 1) shouldBe 4
        (html.split("color:#ff0000").size - 1) shouldBe 2
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `chained format operations`() {
        val formatted = df
            .format().with { background(white) and textColor(black) }
            .format { age }.with { background(red) }
            .format { isHappy }.with { background(if (it) green else red) }

        val html = formatted.toHtml().toString()

        // Should contain all applied styles
        html.split("background-color:#ffffff").size - 1 shouldBe 35
        html.split("background-color:#ff0000").size - 1 shouldBe 9
        html.split("background-color:#00ff00").size - 1 shouldBe 5
        html.split("color:#000000").size - 1 shouldBe 98 // includes attributes outside cells
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format all columns`() {
        val formatted = df.format().with { bold and textColor(black) }
        val html = formatted.toHtml().toString()

        html.split("font-weight:bold").size - 1 shouldBe 49 // All cells formatted
        html.split("color:#000000").size - 1 shouldBe 98 // includes attributes outside cells
    }

    @Test
    fun `format by column names`() {
        val formatted = df.format("age", "weight").with { background(blue) }
        val html = formatted.toHtml().toString()

        html.split("background-color:#0000ff").size - 1 shouldBe 14 // 7 rows * 2 columns (age, weight)
    }

    @Test
    fun `format with complex perRowCol logic`() {
        val formatted = df.format { age }.perRowCol { row, col ->
            val value = col[row]
            when {
                value < 20 -> textColor(blue)
                value < 30 -> textColor(green)
                else -> textColor(red)
            }
        }
        val html = formatted.toHtml().toString()

        // Ages: 15(blue), 45(red), 20(green), 40(red), 30(green), 20(green), 30(green)
        html.split("color:#0000ff").size - 1 shouldBe 2 // blue: age < 20
        html.split("color:#00ff00").size - 1 shouldBe 4 // green: 20 <= age < 30
        html.split("color:#ff0000").size - 1 shouldBe 8 // red: age >= 30
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `toStandaloneHtml includes CSS definitions`() {
        val formatted = df.format { age }.with { background(red) }
        val standaloneHtml = formatted.toStandaloneHtml().toString()
        val regularHtml = formatted.toHtml().toString()

        // Standalone should be longer and contain more CSS/script definitions
        standaloneHtml.length shouldNotBe regularHtml.length
        standaloneHtml.split("<!DOCTYPE html>").size - 1 shouldBe 0
        standaloneHtml.split("<html>").size - 1 shouldBe 1
        standaloneHtml.split("<head>").size - 1 shouldBe 1
        standaloneHtml.split("<style>").size - 1 shouldBe 0
    }

    @Test
    fun `format with custom display configuration`() {
        val config = DisplayConfiguration.DEFAULT.copy(rowsLimit = 3)
        val formatted = df.format { age }.with { background(red) }
        val html = formatted.toHtml(config).toString()

        html.split("background-color:#ff0000").size - 1 shouldBe 3 // Limited to 3 rows by config
    }

    @Test
    fun `documentation example - simple formatting`() {
        // Simple formatting example
        val formatted = df
            .format { age }.with { background(red) }
            .format { weight }.notNull().with { textColor(blue) }

        val html = formatted.toHtml().toString()

        // Should contain both background and text color formatting
        html.split("background-color:#ff0000").size - 1 shouldBe 7 // age column, 7 rows
        html.split("color:#0000ff").size - 1 shouldBe 10 // weight column, actual count from test
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format returns FormattedFrame`() {
        val formatted = df.format { age }.with { background(red) }

        // Should be a FormattedFrame, not a regular DataFrame
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Test
    fun `format with null values handled correctly`() {
        val formatted = df.format { weight }.with { value ->
            if (value != null) background(green) else null
        }
        val html = formatted.toHtml().toString()

        // Should handle null values gracefully
        html.split("background-color:#00ff00").size - 1 shouldBe 5 // Only non-null weight values get formatted
        formatted::class.simpleName shouldBe "FormattedFrame"
    }

    @Suppress("ktlint:standard:argument-list-wrapping")
    @Test
    fun `formatting a column shouldn't affect nested columns with the same name`() {
        val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
            "Alice", "Cooper", 15, "London", 54, true,
            "Bob", "Dylan", 45, "Dubai", 87, true,
            "Charlie", "Daniels", 20, "Moscow", null, false,
            "Charlie", "Chaplin", 40, "Milan", null, true,
            "Bob", "Marley", 30, "Tokyo", 68, true,
            "Alice", "Wolf", 20, null, 55, false,
            "Charlie", "Byrd", 30, "Moscow", 90, true,
        ).group("firstName", "lastName").into("name")
            .groupBy("city").toDataFrame()
            .add("cityCopy") { "city"<String>() }
            .group("city").into("cityGroup")
            .rename("cityCopy").into("city")

        df.alsoDebug()

        df.format().with { background(black) }.toStandaloneHtml().openInBrowser()

        // affects city, cityGroup.city, and group[*].city
        df.format("city").with { bold and italic and textColor(green) }.toStandaloneHtml().openInBrowser()
    }
}
