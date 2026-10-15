package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.RgbColor
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.format
import org.jetbrains.kotlinx.dataframe.api.getValue
import org.jetbrains.kotlinx.dataframe.api.perRowCol
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class FilterSamples : DataFrameSampleHelper("filter", "api") {
    val df = peopleDf

    private val keptRowColor = RgbColor(189, 206, 233)

    private fun DataRow<TestBase.Person>.isKept(): Boolean = age > 18 && name.firstName.startsWith("A")

    @Test
    fun filterDf() {
        // SampleStart
        df
            // SampleEnd
            .format().perRowCol { row, _ ->
                if (row.isKept()) background(keptRowColor) and textColor(black) else null
            }
            .saveDfHtmlSample()
    }

    @Test
    fun filter_properties() {
        // SampleStart
        df.filter { age > 18 && name.firstName.startsWith("A") }
            // SampleEnd
            .format().perRowCol { _, _ -> background(keptRowColor) and textColor(black) }
            .saveDfHtmlSample()
    }

    @Test
    fun filter_strings() {
        // SampleStart
        df.filter { getValue<Int>("age") > 18 && getColumnGroup("name").getValue<String>("firstName").startsWith("A") }
        // SampleEnd
    }

    @Test
    fun filterColumn() {
        // SampleStart
        df.age.filter { it > 17 }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
