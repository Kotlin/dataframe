package org.jetbrains.kotlinx.dataframe.samples.api.column

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class SingleOnColumnSamples : DataFrameSampleHelper("single", "api") {

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
    fun singleOnColumnDf() {
        // SampleStart
        df
            // SampleEnd
            .saveDfHtmlSample()
    }

    @Test
    fun singleOnColumn() {
        // SampleStart
        df
            .filter { name == "Bob" } // one row is left after filtering
            .age
            .single() // returns 20
        // SampleEnd
        df.filter { name == "Bob" }.age.single() shouldBe 20
    }
}
