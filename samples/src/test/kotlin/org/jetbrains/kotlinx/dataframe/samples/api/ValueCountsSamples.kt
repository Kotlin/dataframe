package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class ValueCountsSamples : DataFrameSampleHelper("valueCounts", "api") {
    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int?
    }

    private val df: DataFrame<SimplePerson> = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Alice", "Alice"),
        "age" to listOf(15, 20, 25, 15, null),
    ).cast()

    private val alice15Color = RgbColor(189, 206, 233)
    private val aliceNullColor = RgbColor(233, 199, 220)

    private fun aliceColor(name: String, age: Int?): RgbColor? =
        when (name to age) {
            "Alice" to 15 -> alice15Color
            "Alice" to null -> aliceNullColor
            else -> null
        }

    @Test
    fun valueCountsDf() {
        // SampleStart
        df
            // SampleEnd
            .format().perRowCol { row, _ ->
                aliceColor(row.name, row.age)?.let {
                    background(it) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun valueCounts() {
        // SampleStart
        df.valueCounts()
            // SampleEnd
            .format().perRowCol { row, _ ->
                aliceColor(row.name, row.age)?.let {
                    background(it) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsWithNA() {
        // SampleStart
        df.valueCounts(dropNA = false)
            // SampleEnd
            .format().perRowCol { row, _ ->
                aliceColor(row.name, row.age)?.let {
                    background(it) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsSelector_properties() {
        // SampleStart
        df.valueCounts(dropNA = false) { name }
            // SampleEnd
            .defaultHeaderFormatting { name }
            .saveDfHtmlSample()
    }

    @Test
    fun valueCountsSelector_strings() {
        // SampleStart
        df.valueCounts("name", dropNA = false)
        // SampleEnd
    }

    @Test
    fun valueCountsColumn() {
        // SampleStart
        df.age.valueCounts()
            // SampleEnd
            .defaultHeaderFormatting { "age"<Int>() }
            .saveDfHtmlSample()
    }
}
