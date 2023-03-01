package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.size
import org.junit.Test

class UpdateTests {

    @Test
    fun `update empty dataframe with missing column`() {
        val df = DataFrame.Empty
        val col by column<Int>()
        df.update { col }.with { 2 } shouldBe df
    }

    @DataSchema
    interface DataPart {
        val a: Int
        val b: String
    }

    @DataSchema
    data class Data(
        override val a: Int,
        override val b: String,
        val c: Boolean,
    ) : DataPart

    @Test
    fun `update asFrame`() {
        val df = listOf(
            Data(1, "a", true),
            Data(2, "b", false),
        ).toDataFrame()

        val group by columnGroup<DataPart>() named "Some Group"
        val groupedDf = df.group { a and b }.into { group }

        val res = groupedDf
            .update { group }
            .where { !c }
            .asFrame {
                // size should still be full df size
                size.nrow shouldBe 2

                // this will only apply to rows where `.where { !c }` holds
                update { a }.with { 0 }
            }

        val (first, second) = res[{ group }].map { it.a }.toList()
        first shouldBe 1
        second shouldBe 0

        res[{ group }].name() shouldBe "Some Group"
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
