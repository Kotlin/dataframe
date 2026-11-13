package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.getOrNull
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class GetRowSamples : DataFrameSampleHelper("getRow", "api") {
    val df = peopleDf

    @Test
    fun getRows() {
        // SampleStart
        df.getRows(0..2) // a DataFrame with the first three rows
        df.getRows(listOf(4, 1)) // a DataFrame with the rows 4 and 1, in this order
        // SampleEnd
        df.getRows(0..2).nrow shouldBe 3
        df.getRows(0..2).age.toList() shouldBe listOf(15, 45, 20)
        df.getRows(listOf(4, 1)).age.toList() shouldBe listOf(30, 45)

        shouldThrow<IndexOutOfBoundsException> { df.getRows(8..10) }
        shouldThrow<IndexOutOfBoundsException> { df.getRows(listOf(1, 10)) }
    }

    @Test
    fun getOrNull() {
        // SampleStart
        df.getOrNull(1) // the row with index 1
        df.getOrNull(10) // null: df has only 10 rows
        df.getOrNull(-1) // null
        // SampleEnd
        df.getOrNull(1)?.age shouldBe 45
        df.getOrNull(10) shouldBe null
        df.getOrNull(-1) shouldBe null
    }
}
