package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.junit.Test

class CastTests {

    @DataSchema
    data class Data(val a: Int, val b: String)

    // region DataFrame

    @Test
    fun `unchecked dataframe casts only change the schema type argument`() {
        val df = dataFrameOf(
            "a" to columnOf("not an integer"),
            "b" to columnOf(42),
        )

        val castedWithFlag: DataFrame<Data> = df.cast(verify = false)
        val castedUnchecked: DataFrame<Data> = df.castUnchecked()

        castedWithFlag shouldBeSameInstanceAs df
        castedUnchecked shouldBeSameInstanceAs df
    }

    @Test
    fun `verified dataframe cast returns the same dataframe and keeps excessive columns`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf("one", "two"),
            "extra" to columnOf(true, false),
        )
        val casted: DataFrame<Data> = df.cast(verify = true)

        casted shouldBeSameInstanceAs df
    }

    @Test
    fun `verified cast rejects incompatible column without converting values`() {
        val df = dataFrameOf(
            "a" to columnOf("1"),
            "b" to columnOf("one"),
        )

        df.convertTo<Data>()["a"][0] shouldBe 1
        shouldThrow<IllegalArgumentException> {
            df.cast<Data>(verify = true)
        }
    }

    @Test
    fun `verified cast rejects missing required column`() {
        val df = dataFrameOf(
            "a" to columnOf(1),
        )

        shouldThrow<IllegalArgumentException> {
            df.cast<Data>(verify = true)
        }
    }

    @Test
    fun `castTo infers schema from dataframe`() {
        val schemaSource = dataFrameOf(
            "a" to columnOf(0),
            "b" to columnOf("schema"),
        ).cast<Data>(verify = true)
        val df = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf("one", "two"),
        )

        val casted: DataFrame<Data> = df.castTo(schemaFrom = schemaSource, verify = true)

        casted shouldBeSameInstanceAs df
    }

    @Test
    fun `unchecked castTo only changes the schema type argument`() {
        val schemaSource = dataFrameOf(
            "a" to columnOf(0),
            "b" to columnOf("schema"),
        ).cast<Data>(verify = true)
        val df = dataFrameOf(
            "a" to columnOf("not an integer"),
            "b" to columnOf(42),
        )

        val casted: DataFrame<Data> = df.castTo(schemaFrom = schemaSource, verify = false)

        casted shouldBeSameInstanceAs df
    }

    private fun dataFactory(
        @Suppress("UNUSED_PARAMETER") seed: Int,
    ): DataFrame<Data> = error("castTo must use only the function's return type")

    @Test
    fun `castTo infers schema from function return type without invoking function`() {
        val df = dataFrameOf(
            "a" to columnOf(1),
            "b" to columnOf("one"),
        )

        val casted: DataFrame<Data> = df.castTo(schemaFrom = ::dataFactory, verify = true)

        casted shouldBeSameInstanceAs df
    }

    @Test
    fun `verified castTo rejects incompatible schema`() {
        val schemaSource = dataFrameOf(
            "a" to columnOf(0),
            "b" to columnOf("schema"),
        ).cast<Data>(verify = true)
        val df = dataFrameOf(
            "a" to columnOf("not an integer"),
            "b" to columnOf("value"),
        )

        shouldThrow<IllegalArgumentException> {
            df.castTo(schemaFrom = schemaSource, verify = true)
        }
    }

    @Test
    fun `cast without arguments verifies schema`() {
        val df = dataFrameOf(
            "a" to columnOf(1),
            "b" to columnOf("s"),
            "c" to columnOf(2),
        )
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
        val df = dataFrameOf(
            "a" to columnOf(1),
            "b" to columnOf("s"),
        )
        df.cast<NullableData>() shouldBe df

        shouldThrow<IllegalArgumentException> {
            dataFrameOf(
                "a" to columnOf("s"),
                "b" to columnOf(1),
            ).cast<NullableData>()
        }
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
    fun `verified cast rejects incompatible nested column group`() {
        val df = dataFrameOf(
            "test" to columnOf(
                "rawData" to columnOf("string"),
            ),
        )

        shouldThrow<IllegalArgumentException> {
            df.cast<Container>(verify = true)
        }
    }

    @Test
    fun `cast should disregard column order`() {
        // https://github.com/Kotlin/dataframe/issues/1644
        // it ensures that variable in notebooks can be `castTo` to its own marker type
        val df = dataFrameOf(
            "test" to columnOf(
                "rawData" to columnOf("string"),
                "score" to columnOf(1f),
            ),
        )

        df.cast<Container>(verify = true) shouldBeSameInstanceAs df
    }

    // endregion

    // region DataRow

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

    // endregion

    // region DataColumn

    @Test
    fun `column casts preserve column instances`() {
        val dataColumn: DataColumn<*> = columnOf(1, 2)
        val valueColumn: ValueColumn<*> = dataColumn.asValueColumn()
        val frameColumn: FrameColumn<*> = columnOf(
            dataFrameOf(
                "a" to columnOf(1),
                "b" to columnOf("one"),
            ),
        )

        val castedDataColumn: DataColumn<Number> = dataColumn.cast()
        val castedValueColumn: ValueColumn<Number> = valueColumn.cast()
        val castedFrameColumn: FrameColumn<Data> = frameColumn.castFrameColumn()

        castedDataColumn shouldBeSameInstanceAs dataColumn
        castedValueColumn shouldBeSameInstanceAs valueColumn
        castedFrameColumn shouldBeSameInstanceAs frameColumn
    }

    // endregion

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
