package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class SortWithSamples : DataFrameSampleHelper("sortWith", "api") {
    private val df = peopleDf

    @Test
    fun sortWithDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortWithComparator() {
        // SampleStart
        // Sort rows by "age" ascending, then by ("name"/"lastName") descending
        df.sortWith(
            compareBy<DataRow<Person>> { it.age }
                .thenByDescending { it.name.lastName },
        )
            // SampleEnd
            .defaultHeaderFormatting { age and name.lastName }
            .saveDfHtmlSample()
    }

    @Test
    fun sortWithLambda() {
        // SampleStart
        // Sort rows by "age" ascending, then by ("name"/"firstName") ascending
        df.sortWith { row1, row2 ->
            when {
                row1.age != row2.age -> row1.age.compareTo(row2.age)
                else -> row1.name.firstName.compareTo(row2.name.firstName)
            }
        }
            // SampleEnd
            .defaultHeaderFormatting { age and name.firstName }
            .saveDfHtmlSample()
    }

    @Test
    fun sortWithColumn() {
        // SampleStart
        // Sort "name"/"lastName" values by their length
        df.name.lastName.sortWith { name1, name2 -> name1.length - name2.length }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
