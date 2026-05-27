package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.countDistinct
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class CountDistinctSamples : DataFrameSampleHelper("countDistinct", "api") {
    val df = peopleDf

    private fun lastNameToColor(name: String): RgbColor =
        when (name) {
            "Byrd", "Daniels" -> RgbColor(210, 229, 199)
            else -> RgbColor(255, 255, 255)
        }

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
        df.countDistinct() // the result is 10
        // SampleEnd
    }

    @Test
    fun countDistinctColumns_properties() {
        // SampleStart
        df.countDistinct { name.firstName and city } // the result is 9
        // SampleEnd
    }

    @Test
    fun countDistinctColumns_strings() {
        // SampleStart
        df.countDistinct { "name"["firstName"] and "city" } // the result is 9
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsDf() {
        df.format().perRowCol { row, _ ->
            val lastName = df[row.index()].name.lastName
            background(lastNameToColor(lastName)) and textColor(black)
        }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctGroupBy() {
        // SampleStart
        df.groupBy { isHappy }
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.countDistinct()
            // SampleEnd
            .defaultHeaderFormatting { "countDistinct"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").countDistinct()
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.countDistinct { name.firstName }
            // SampleEnd
            .defaultHeaderFormatting { "countDistinct"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctColumnsOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").countDistinct { "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsCustomNameOnGroupBy_properties() {
        // SampleStart
        df.groupBy { isHappy }.countDistinct("uniqueFirstNames") { name.firstName }
            // SampleEnd
            .defaultHeaderFormatting { "uniqueFirstNames"() }
            .saveDfHtmlSample()
    }

    @Test
    fun countDistinctColumnsCustomNameOnGroupBy_strings() {
        // SampleStart
        df.groupBy("isHappy").countDistinct("uniqueFirstNames") { "name"["firstName"] }
        // SampleEnd
    }

    @Test
    fun countDistinctColumnsGroupBy() {
        df.groupBy { isHappy }
            .toDataFrame()
            .convert { group }.with { group ->
                val firstNameCol = group["name"]["firstName"]
                group.format().perRowCol { row, _ ->
                    val firstName = firstNameCol[row.index()] as String
                    background(firstNameToColor(firstName)) and textColor(black)
                }
            }
            .saveDfHtmlSample()
    }
}
