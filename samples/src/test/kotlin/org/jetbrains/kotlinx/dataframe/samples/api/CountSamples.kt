package org.jetbrains.kotlinx.dataframe.samples.api

import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class CountSamples : DataFrameSampleHelper("count", "api") {
    val df = peopleDf

    @Test
    fun countDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun count() {
        // SampleStart
        df.count() // the result is 10
        // SampleEnd
    }

    @Test
    fun countCondition_properties() {
        // SampleStart
        df.count { age > 15 } // the result is 8
        // SampleEnd
    }

    @Test
    fun countCondition_strings() {
        // SampleStart
        df.count { "age"<Int>() > 15 } // the result is 8
        // SampleEnd
    }

    @Test
    fun countGroupBy_properties() {
        // SampleStart
        df.groupBy { city }.count()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun countGroupBy_strings() {
        // SampleStart
        df.groupBy("city").count()
        // SampleEnd
    }

    @Test
    fun countPivot_properties() {
        // SampleStart
        df.pivot { city }.count { age > 18 }
            // SampleEnd
            .toDataFrame()
            .saveDfHtmlSample()
    }

    @Test
    fun countPivot_strings() {
        // SampleStart
        df.pivot("city").count { "age"<Int>() > 18 }
        // SampleEnd
    }

    @Test
    fun countPivotGroupBy_properties() {
        // SampleStart
        df.pivot { name.firstName }.groupBy { name.lastName }.count()
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun countPivotGroupBy_strings() {
        // SampleStart
        df.pivot { "name"["firstName"] }
            .groupBy { "name"["lastName"] }
            .count()
        // SampleEnd
    }

    @Test
    fun countDataRow() {
        // SampleStart
        df[0].count() // the result is 5
        // SampleEnd
    }

    @Test
    fun countDataRowCondition() {
        // SampleStart
        df[2].count { it == null } // the result is 1
        // SampleEnd
    }

    @Test
    fun countDataColumn() {
        // SampleStart
        df.age.count() // the result is 10
        // SampleEnd
    }

    @Test
    fun countDataColumnCondition() {
        // SampleStart
        df.age.count { it > 17 } // the result is 8
        // SampleEnd
    }
}
