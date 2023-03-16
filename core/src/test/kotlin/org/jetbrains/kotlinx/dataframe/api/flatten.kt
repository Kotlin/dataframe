package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.junit.Test

class FlattenTests {

    @Test
    fun `flatten names`() {
        val df = dataFrameOf("a", "b", "c")(1, 2, 3)
        val grouped = df.group("a", "b").into("d")
        grouped.flatten() shouldBe df
        grouped.add("a") { 0 }.flatten().columnNames() shouldBe listOf("a1", "b", "c", "a")
    }

    @DataSchema
    interface TestRow {
        val a: String
        val b: String
        val c: String
    }

    @DataSchema
    interface Grouped {
        val d: DataRow<TestRow>
    }

    @Test
    fun `flatten access APIs`() {
        val df = dataFrameOf("a", "b", "c")(1, 2, 3)
        val grouped = df.group("a", "b").into("d")

        // String API
        grouped.flatten("d") shouldBe df
        val castedGroupedDF = grouped.cast<Grouped>()

        // KProperties API
        castedGroupedDF.flatten (Grouped::d) shouldBe df

        // Extension properties API
        castedGroupedDF.flatten { d } shouldBe df

        // Column accessors API
        val d by columnGroup()
        val a by d.column<String>()
        val b by d.column<String>()
        val c by d.column<String>()
        grouped.flatten(d) shouldBe df
    }

    @Test
    fun `flatten nested`() {
        val df = dataFrameOf("a", "b", "c", "d")(1, 2, 3, 4)
        val grouped = df.group("a", "b").into("e")
            .group("e", "c").into("f")

        grouped.flatten() shouldBe df
        val flattened = grouped.flatten { "f"["e"] }
        flattened.columnNames() shouldBe listOf("f", "d")
        flattened.ungroup("f") shouldBe df

        grouped.flatten { "f"["e"] and "f" } shouldBe df
    }

    @Test
    fun `flatten with parent name conflict`() {
        val df = dataFrameOf("a", "b", "c", "d")(1, 2, 3, 4)
        val grouped = df.group("a", "b").into("e")
            .group("e", "c").into("f")
            .rename { "f"["e"] }.into("a")
        val flattened = grouped.flatten { "f"["a"] }
        flattened.getColumnGroup("f").columnNames() shouldBe listOf("a", "b", "c")
        flattened.ungroup("f") shouldBe df
    }
}
