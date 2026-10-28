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
import kotlin.reflect.typeOf

class CastTests {

    @DataSchema
    data class Data(val a: Int, val b: String)

    @DataSchema
    data class NullableData(val a: Int, val b: String?)

    @DataSchema
    data class NullableValue(val value: String?)

    @DataSchema
    data class NestedData(val a: Int, val group: NullableValue, val frames: DataFrame<Data>)

    // region DataFrame

    @Test
    fun `unchecked dataframe casts only change the schema type argument`() {
        val df = dataFrameOf(
            "a" to columnOf("not an integer"),
            "b" to columnOf(42),
        )

        val castedWithFlag: DataFrame<Data> = df.cast(verify = false)
        val castedWithoutVerificationOverload: DataFrame<Data> = df.cast()

        castedWithFlag shouldBeSameInstanceAs df
        castedWithoutVerificationOverload shouldBeSameInstanceAs df
        castedWithFlag["a"].type() shouldBe typeOf<String>()
        castedWithFlag["b"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `verified dataframe cast accepts matching schema and preserves excessive columns`() {
        val df = dataFrameOf(
            "a" to columnOf(1, 2),
            "b" to columnOf("one", "two"),
            "extra" to columnOf(true, false),
        )
        val casted: DataFrame<Data> = df.cast(verify = true)

        casted shouldBe df
    }

    @Test
    fun `verified cast fills missing nullable column with nulls`() {
        val df = dataFrameOf("a" to columnOf(1, 2))

        val casted = df.cast<NullableData>(verify = true)

        casted["a"] shouldBe df["a"]
        casted["b"].toList() shouldBe listOf(null, null)
        casted["b"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `verified cast creates missing groups and frame columns`() {
        val df = dataFrameOf("a" to columnOf(1, 2))
        val emptyFrame = dataFrameOf(
            "a" to columnOf(values = emptyArray<Int>()),
            "b" to columnOf(values = emptyArray<String>()),
        )

        val casted = df.cast<NestedData>(verify = true)

        casted["a"] shouldBe df["a"]
        val group = casted["group"].asColumnGroup()
        group["value"].toList() shouldBe listOf(null, null)
        group["value"].type() shouldBe typeOf<String?>()
        val frames = casted["frames"].asFrameColumn()
        frames.toList() shouldBe listOf(emptyFrame, emptyFrame)
        frames.schema.value shouldBe emptyFrame.schema()
    }

    @Test
    fun `verified cast of empty dataframe creates target schema`() {
        val df = dataFrameOf("extra" to columnOf(values = emptyArray<Boolean>()))

        val casted = df.cast<Data>(verify = true)

        casted shouldBe dataFrameOf(
            "a" to columnOf(values = emptyArray<Int>()),
            "b" to columnOf(values = emptyArray<String>()),
        )
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

        casted shouldBe df
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
        casted["a"].type() shouldBe typeOf<String>()
        casted["b"].type() shouldBe typeOf<Int>()
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

        casted shouldBe df
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
        dataFrameOf(
            "test" to columnOf(
                "rawData" to columnOf("string"),
                "score" to columnOf(1f),
            ),
        ).cast<Container>(verify = true)
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

        val casted: DataRow<Data> = row.cast()
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
        val group: ColumnGroup<*> = columnOf(
            "a" to columnOf(1),
            "b" to columnOf("one"),
        )
        val frameColumn: FrameColumn<*> = columnOf(
            dataFrameOf(
                "a" to columnOf(1),
                "b" to columnOf("one"),
            ),
        )

        val castedDataColumn: DataColumn<Number> = dataColumn.cast()
        val castedValueColumn: ValueColumn<Number> = valueColumn.cast()
        val castedGroup: ColumnGroup<Data> = group.cast()
        val castedFrameColumn: FrameColumn<Data> = frameColumn.castFrameColumn()

        castedDataColumn shouldBeSameInstanceAs dataColumn
        castedValueColumn shouldBeSameInstanceAs valueColumn
        castedGroup shouldBeSameInstanceAs group
        castedFrameColumn shouldBeSameInstanceAs frameColumn
    }

    // endregion
}
