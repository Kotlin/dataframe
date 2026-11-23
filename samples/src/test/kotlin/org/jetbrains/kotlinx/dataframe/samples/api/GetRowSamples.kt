package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.first
import org.jetbrains.kotlinx.dataframe.api.getOrNull
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.maxBy
import org.jetbrains.kotlinx.dataframe.api.maxByOrNull
import org.jetbrains.kotlinx.dataframe.api.minBy
import org.jetbrains.kotlinx.dataframe.api.single
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

class GetRowSamples : DataFrameSampleHelper("getRow", "api") {
    val df = peopleDf

    @Test
    fun getRowDf() {
        // SampleStart
        val input = df
        // SampleEnd
        input.saveDfHtmlSample()
    }

    @Test
    fun getRowByIndex() {
        // SampleStart
        val row = df[2] // the row at index 2
        // SampleEnd
        row.toDataFrame().saveDfHtmlSample()

        row.name.lastName shouldBe "Daniels"

        shouldThrow<IndexOutOfBoundsException> { df[10] }
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

        df.getOrNull(1)?.toDataFrame()?.saveDfHtmlSample()
    }

    @Test
    fun getRowsByRange() {
        // SampleStart
        df.getRows(0..2) // a DataFrame with the first three rows
        // SampleEnd
        df.getRows(0..2).rowsCount() shouldBe 3
        df.getRows(0..2).age.toList() shouldBe listOf(15, 45, 20)
        df.getRows(IntRange.EMPTY).rowsCount() shouldBe 0

        shouldThrow<IndexOutOfBoundsException> { df.getRows(8..10) }

        df.getRows(0..2).saveDfHtmlSample()
    }

    @Test
    fun getRowsByIndices() {
        // SampleStart
        df.getRows(listOf(4, 1)) // a DataFrame with the rows 4 and 1, in this order
        // SampleEnd
        df.getRows(listOf(4, 1)).age.toList() shouldBe listOf(30, 45)

        shouldThrow<IndexOutOfBoundsException> { df.getRows(listOf(1, 10)) }

        df.getRows(listOf(4, 1)).saveDfHtmlSample()
    }

    @Test
    fun getRowByCondition_properties() {
        // SampleStart
        df.single { age == 45 } // the only person aged 45
        df.first { weight != null } // the first person with a known weight
        df.minBy { age } // the youngest person
        df.maxBy { age } // the oldest person
        df.maxByOrNull { weight } // the heaviest person
        // SampleEnd
        df.single { age == 45 }.toDataFrame().saveDfHtmlSample()

        df.single { age == 45 }.name.lastName shouldBe "Dylan"
        df.first { weight != null }.name.firstName shouldBe "Alice"
        df.minBy { age }.age shouldBe 15
        df.maxBy { age }.age shouldBe 45
        df.maxByOrNull { weight }?.name?.firstName shouldBe "Charlie"
    }

    @Test
    fun getRowByCondition_strings() {
        // SampleStart
        df.single { it["age"] == 45 } // the only person aged 45
        df.first { it["weight"] != null } // the first person with a known weight
        df.minBy("age") // the youngest person
        df.maxBy("age") // the oldest person
        df.maxByOrNull("weight") // the heaviest person
        // SampleEnd
        df.single { it["age"] == 45 }.name.lastName shouldBe "Dylan"
        df.first { it["weight"] != null }.name.firstName shouldBe "Alice"
        df.minBy("age").age shouldBe 15
        df.maxBy("age").age shouldBe 45
        df.maxByOrNull("weight")?.name?.firstName shouldBe "Charlie"
    }
}
