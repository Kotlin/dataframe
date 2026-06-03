package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.countDistinct
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.take
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class CountDistinctSamples : DataFrameSampleHelper("countDistinct", "api") {
    val df = peopleDf.take(7).filter { index() in setOf(0, 1, 2, 6) }

    private fun firstNameToColor(name: String): RgbColor =
        when (name) {
            "Alice" -> RgbColor(189, 206, 233)
            "Bob" -> RgbColor(198, 224, 198)
            "Charlie" -> RgbColor(219, 198, 230)
            else -> RgbColor(255, 255, 255)
        }

    @Test
    fun countDistinctDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinct() {
        // SampleStart
        df.countDistinct() // the result is 4
        // SampleEnd
    }

    @Test
    fun countDistinctColumns_properties() {
        // SampleStart
        df.countDistinct { name.firstName and city } // the result is 3
        // SampleEnd
    }

    @Test
    fun countDistinctColumns_strings() {
        // SampleStart
        df.countDistinct { "name"["firstName"] and "city" } // the result is 3
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsDf() {
        df.format().perRowCol { row, _ ->
            val firstName = df[row.index()].name.firstName
            background(firstNameToColor(firstName)) and textColor(black)
        }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctGroupBy() {
        // SampleStart
        df.groupBy { city }
            // SampleEnd
            .toExpandedHtml()
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctOnGroupBySmallTable_properties() {
        // SampleStart
        df.groupBy { city }.countDistinct()
            // SampleEnd
            .defaultHeaderFormatting { "countDistinct"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctOnGroupBySmallTable_strings() {
        // SampleStart
        df.groupBy("city").countDistinct()
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.countDistinct { name.firstName }
            // SampleEnd
            .defaultHeaderFormatting { "countDistinct"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctColumnsOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").countDistinct { "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsCustomNameOnGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.countDistinct("uniqueFirstNames") { name.firstName }
            // SampleEnd
            .defaultHeaderFormatting { "uniqueFirstNames"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctColumnsCustomNameOnGroupBy_strings() {
        // SampleStart
        df.groupBy("city").countDistinct("uniqueFirstNames") { "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsGroupBy() {
        df.groupBy { city }
            .toDataFrame()
            .convert { group }.with { group ->
                val firstNameCol = group["name"]["firstName"]
                group.format().perRowCol { row, _ ->
                    val firstName = firstNameCol[row.index()] as String
                    background(firstNameToColor(firstName)) and textColor(black)
                }
            }
            .toExpandedHtml()
            .saveDfHtmlSample()
    }
}
