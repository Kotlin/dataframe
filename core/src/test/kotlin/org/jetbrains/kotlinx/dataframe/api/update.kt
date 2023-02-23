package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.junit.Test

class UpdateTests {

    @Test
    fun `update empty dataframe with missing column`() {
        val df = DataFrame.Empty
        val col by column<Int>()
        df.update { col }.with { 2 } shouldBe df
    }

    @DataSchema
    interface SchemaA {
        val i: Int?
    }

    @DataSchema
    interface SchemaB {
        val i: Int
    }

    @Test
    fun `fillNulls update`() {
        val df = dataFrameOf("i")(1, null)

        df.fillNulls(SchemaA::i).with { 42 }

        df.fillNulls(SchemaB::i).with { 42 }
    }

    @Test
    fun `fillNA update`() {
        val df = dataFrameOf("i")(1, null)

        df.fillNA(SchemaA::i).with { 42 }

        df.fillNA(SchemaB::i).with { 42 }
    }
}
