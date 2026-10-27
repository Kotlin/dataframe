package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.rowsReversed
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class RowsSamples : DataFrameSampleHelper("rows", "api") {
    val df = peopleDf

    @Test
    fun rows() {
        // SampleStart
        df.rows().map { it.age } // [15, 45, 20, ..., 18]: from the first row to the last one
        df.rowsReversed().map { it.age } // [18, 15, 30, ..., 15]: from the last row to the first one
        // SampleEnd
        df.rows().map { it.age } shouldBe listOf(15, 45, 20, 40, 30, 20, 30, 30, 15, 18)
        df.rowsReversed().map { it.age } shouldBe listOf(18, 15, 30, 30, 20, 30, 40, 20, 45, 15)
    }
}
