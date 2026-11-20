package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.junit.Test

class CastTests {

    @DataSchema
    data class Data(val a: Int, val b: String)

    @Test
    fun safeUnsafeCast() {
        val df = dataFrameOf("a", "b", "c")(1, "s", 2)
        df.cast<Data>(verify = true) shouldBe df

        shouldThrow<IllegalArgumentException> {
            df.convert("a").toDouble().cast<Data>(verify = true)
        }
        val converted = df.convert("a").toDouble()
        converted.cast<Data>(verify = false) shouldBe converted
        converted.castUnchecked<Data>() shouldBe converted
    }

    @Test
    fun `cast without arguments verifies schema`() {
        val df = dataFrameOf("a", "b", "c")(1, "s", 2)
        val casted: DataFrame<Data> = df.cast()
        casted shouldBe df
        df.cast<Data>() shouldBe df

        shouldThrow<IllegalArgumentException> {
            df.convert("a").toDouble().cast<Data>()
        }
        shouldThrow<IllegalArgumentException> {
            df.remove("b").cast<Data>()
        }
    }

    @DataSchema
    data class NullableData(val a: Int?, val b: Any?)

    @Test
    fun `cast accepts columns whose type is a subtype of the schema type`() {
        val df = dataFrameOf("a", "b")(1, "s")
        df.cast<NullableData>() shouldBe df

        shouldThrow<IllegalArgumentException> {
            dataFrameOf("a", "b")("s", 1).cast<NullableData>()
        }
    }

    @Test
    fun `castUnchecked does not verify schema`() {
        val df = dataFrameOf("a", "b")("s", 1)
        val casted: DataFrame<Data> = df.castUnchecked()
        casted shouldBeSameInstanceAs df
        df.castUnchecked<Data>() shouldBeSameInstanceAs df
        df[0].castUnchecked<Data>() shouldBe df[0]
    }

    @Test
    fun `row cast without arguments verifies schema`() {
        val row = dataFrameOf(
            "a" to columnOf("one", "two"),
            "b" to columnOf(1, 2),
        )[1]

        shouldThrow<IllegalArgumentException> {
            row.cast<Data>()
        }
    }

    @DataSchema
    data class Container(val test: Schema)

    @DataSchema
    data class Schema(val score: Float, val rawData: String)

    @Test
    fun `cast should disregard column order`() {
        // https://github.com/Kotlin/dataframe/issues/1644
        // it ensures that variable in notebooks can be `castTo` to its own marker type
        dataFrameOf(
            "test" to columnOf(
                "rawData" to columnOf("string"),
                "score" to columnOf(1f),
            ),
        ).cast<Container>(verify = true)
    }

    @Test
    fun `verified row cast with dataframe matching target schema`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf("one", "two"),
        )

        val casted: DataRow<Data> = df[1].cast(verify = true)

        casted shouldBe df[1]
        casted.index() shouldBe 1
    }

    @Test
    fun `unchecked row casts do not check schema`() {
        val row = dataFrameOf(
            "a" to columnOf("one", "two"),
            "b" to columnOf(1, 2),
        )[1]

        val casted: DataRow<Data> = row.castUnchecked()
        val castedWithFlag: DataRow<Data> = row.cast(verify = false)

        casted shouldBeSameInstanceAs row
        castedWithFlag shouldBe row
        castedWithFlag.index() shouldBe 1
    }

    @DataSchema
    data class Key(val a: Int)

    private val grouped = dataFrameOf(
        "a" to columnOf(1, 1, 2),
        "b" to columnOf("x", "y", "z"),
    ).groupBy("a")

    @Test
    fun `groupBy cast verifies keys and groups`() {
        val casted: GroupBy<Key, Data> = grouped.cast()
        casted shouldBeSameInstanceAs grouped
    }

    @DataSchema
    data class NullableB(val b: String?)

    @Test
    fun `groupBy cast does not add missing nullable columns`() {
        val casted = grouped.cast<Key, NullableB>()
        casted shouldBeSameInstanceAs grouped
        casted.groups.values().forEach { it.containsColumn("b") shouldBe true }

        val withoutB = grouped.updateGroups { it.remove("b") }
        val castedWithoutB = withoutB.cast<Key, NullableB>()
        castedWithoutB shouldBeSameInstanceAs withoutB
        castedWithoutB.groups.values().forEach { it.containsColumn("b") shouldBe false }
    }

    @Test
    fun `groupBy cast with full schema as keys fails`() {
        // T of GroupBy describes key columns only
        shouldThrow<IllegalArgumentException> {
            grouped.cast<Data, Data>()
        }
    }

    @Test
    fun `groupBy cast fails on wrong group schema`() {
        shouldThrow<IllegalArgumentException> {
            grouped.cast<Key, Container>()
        }
        shouldThrow<IllegalArgumentException> {
            grouped.updateGroups { it.remove("b") }.cast<Key, Data>()
        }
    }

    @Test
    fun `groupBy castUnchecked does not verify`() {
        val casted: GroupBy<Data, Container> = grouped.castUnchecked()
        casted shouldBeSameInstanceAs grouped
        grouped.cast<Data, Container>(verify = false) shouldBeSameInstanceAs grouped
    }

    private val group = dataFrameOf(
        "a" to columnOf(1, 2),
        "b" to columnOf("x", "y"),
    ).asColumnGroup("group")

    @Test
    fun `columnGroup cast verifies schema`() {
        val casted: ColumnGroup<Data> = group.cast()
        casted shouldBeSameInstanceAs group

        shouldThrow<IllegalArgumentException> {
            group.cast<Container>()
        }
        shouldThrow<IllegalArgumentException> {
            group.remove("b").asColumnGroup("group").cast<Data>()
        }
    }

    @Test
    fun `columnGroup cast does not add missing nullable columns`() {
        val withoutB = group.remove("b").asColumnGroup("group")
        val casted = withoutB.cast<NullableB>()
        casted shouldBeSameInstanceAs withoutB
        casted.containsColumn("b") shouldBe false
    }

    @Test
    fun `columnGroup castUnchecked does not verify`() {
        val casted: ColumnGroup<Container> = group.castUnchecked()
        casted shouldBeSameInstanceAs group
        group.cast<Container>(verify = false) shouldBeSameInstanceAs group
    }
}
