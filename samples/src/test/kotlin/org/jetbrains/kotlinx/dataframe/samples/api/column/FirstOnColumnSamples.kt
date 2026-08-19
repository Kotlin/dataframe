package org.jetbrains.kotlinx.dataframe.samples.api.column

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class FirstOnColumnSamples : DataFrameSampleHelper("first", "api") {

    @DataSchema
    interface SimplePerson {
        val name: String
        val age: Int
    }

    private val df: DataFrame<SimplePerson> = dataFrameOf(
        "name" to listOf("Alice", "Bob", "Charlie", "Diana"),
        "age" to listOf(15, 20, 25, 30),
    ).cast()

    @Test
    fun firstOnColumnDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun firstOnColumn() {
        // SampleStart
        df.name.first() // returns "Alice"
        // SampleEnd
    }

    @Test
    fun firstOnColumnPredicate() {
        // SampleStart
        df.age.first { it > 17 } // returns 20
        // SampleEnd
    }

    @Test
    fun firstOrNullOnColumn_properties() {
        // SampleStart
        df
            .filter { age > 50 } // df is empty after filtering
            .age
            .firstOrNull() // returns null
        // SampleEnd
    }

    @Test
    fun firstOrNullOnColumn_strings() {
        // SampleStart
        df
            .filter { "age"<Int>() > 50 } // df is empty after filtering
            .age
            .firstOrNull() // returns null
        // SampleEnd
    }

    @Test
    fun firstOrNullOnColumnPredicate() {
        // SampleStart
        df.age.firstOrNull { it > 50 } // returns null
        // SampleEnd
    }
}
