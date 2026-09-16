package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.and
import org.jetbrains.kotlinx.dataframe.api.expr
import org.jetbrains.kotlinx.dataframe.api.sort
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.sortByDesc
import org.jetbrains.kotlinx.dataframe.api.sortDesc
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase.Person
import org.jetbrains.kotlinx.dataframe.util.defaultHeaderFormatting
import org.junit.Test

class SortBySamples : DataFrameSampleHelper("sortBy", "api") {
    private val df = peopleDf

    @Test
    fun sortByDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortBy_properties() {
        // SampleStart
        df.sortBy { age }
            // SampleEnd
            .defaultHeaderFormatting { age }
            .saveDfHtmlSample()
    }

    @Test
    fun sortBy_strings() {
        // SampleStart
        df.sortBy("age")
        // SampleEnd
    }

    @Test
    fun sortBySeveralColumns_properties() {
        // SampleStart
        df.sortBy { age and name.lastName }
            // SampleEnd
            .defaultHeaderFormatting { age and name.lastName }
            .saveDfHtmlSample()
    }

    @Test
    fun sortBySeveralColumns_strings() {
        // SampleStart
        df.sortBy { "age" and "name"["lastName"] }
        // SampleEnd
    }

    @Test
    fun sortByReversed_properties() {
        // SampleStart
        df.sortBy { age.reversed() and name.lastName }
            // SampleEnd
            .defaultHeaderFormatting { age and name.lastName }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByReversed_strings() {
        // SampleStart
        df.sortBy { "age".reversed() and "name"["lastName"] }
        // SampleEnd
    }

    @Test
    fun sortByNulls_properties() {
        // SampleStart
        df.sortBy { weight }
            // SampleEnd
            .defaultHeaderFormatting { weight }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByNulls_strings() {
        // SampleStart
        df.sortBy("weight")
        // SampleEnd
    }

    @Test
    fun sortByNullsLast_properties() {
        // SampleStart
        df.sortBy { weight.nullsLast() }
            // SampleEnd
            .defaultHeaderFormatting { weight }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByNullsLast_strings() {
        // SampleStart
        df.sortBy { "weight".nullsLast() }
        // SampleEnd
    }

    @Test
    fun sortByExpr() {
        // SampleStart
        // Sort rows by the full name length
        df.sortBy { expr { name.firstName.length + name.lastName.length } }
            // SampleEnd
            .defaultHeaderFormatting { name }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByDesc_properties() {
        // SampleStart
        df.sortByDesc { age and weight }
            // SampleEnd
            .defaultHeaderFormatting { age and weight }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByDesc_strings() {
        // SampleStart
        df.sortByDesc("age", "weight")
        // SampleEnd
    }

    @Test
    fun sortByDescReversed_properties() {
        // SampleStart
        df.sortByDesc { age and name.lastName.reversed() }
            // SampleEnd
            .defaultHeaderFormatting { age and name.lastName }
            .saveDfHtmlSample()
    }

    @Test
    fun sortByDescReversed_strings() {
        // SampleStart
        df.sortByDesc { "age" and "name"["lastName"].reversed() }
        // SampleEnd
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
    fun sortColumn() {
        // SampleStart
        df.age.sort()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortColumnDesc() {
        // SampleStart
        df.age.sortDesc()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun sortColumnWith() {
        // SampleStart
        // Sort "name"/"lastName" values by their length
        df.name.lastName.sortWith { name1, name2 -> name1.length - name2.length }
            // SampleEnd
            .saveDfHtmlSample()
    }
}
