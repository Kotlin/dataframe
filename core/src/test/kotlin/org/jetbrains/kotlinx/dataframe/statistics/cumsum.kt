package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.cumSum
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.impl.nullableNothingType
import org.jetbrains.kotlinx.dataframe.math.cumSumTypeConversion
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class CumsumTests {

    val col by columnOf(1, 2, null, 3, 4)
    val expected = listOf(1, 3, null, 6, 10)
    val expectedNoSkip = listOf(1, 3, null, null, null)

    @Test
    fun `int column`() {
        col.cumSum().toList() shouldBe expected
        col.cumSum(skipNA = false).toList() shouldBe expectedNoSkip
    }

    @Test
    fun `short column`() {
        col.map { it?.toShort() }.cumSum().toList() shouldBe expected
        col.map { it?.toShort() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip
    }

    @Test
    fun `byte column`() {
        col.map { it?.toByte() }.cumSum().toList() shouldBe expected
        col.map { it?.toByte() }.cumSum(skipNA = false).toList() shouldBe expectedNoSkip
    }

    @Test
    fun `frame with multiple columns`() {
        val col2 by columnOf(1.toShort(), 2, 3, 4, 5)
        val col3 by columnOf(1.toByte(), 2, 3, 4, null)
        val df = dataFrameOf(col, col2, col3)
        val res = df.cumSum(skipNA = false)

        res[col].toList() shouldBe expectedNoSkip
        res[col2].toList() shouldBe listOf(1.toShort(), 3, 6, 10, 15)
        res[col3].toList() shouldBe listOf(1.toByte(), 3, 6, 10, null)
    }

    @Test
    fun frame() {
        val str by columnOf("a", "b", "c", "d", "e")
        val df = dataFrameOf(col, str)

        df.cumSum()[col].toList() shouldBe expected
        df.cumSum(skipNA = false)[col].toList() shouldBe expectedNoSkip

        df.cumSum { col }[col].toList() shouldBe expected
        df.cumSum(skipNA = false) { col }[col].toList() shouldBe expectedNoSkip

        df.cumSum(col)[col].toList() shouldBe expected
        df.cumSum(col, skipNA = false)[col].toList() shouldBe expectedNoSkip
    }

    @Test
    fun `double column`() {
        val doubles by columnOf(1.0, 2.0, null, Double.NaN, 4.0)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }

    @Test
    fun `number column`() {
        val doubles: DataColumn<Number?> by columnOf<Number?>(1, 2, null, Double.NaN, 4)
        doubles.cumSum().toList() shouldBe listOf(1.0, 3.0, Double.NaN, Double.NaN, 7.0)
    }

    @Test
    fun `groupBy`() {
        val df = dataFrameOf("str", "col")(
            "a", 1,
            "b", 2,
            "c", null,
            "a", 3,
            "c", 4,
        )
        df.groupBy("str").cumSum().concat() shouldBe
            dataFrameOf("str", "col")(
                "a", 1,
                "a", 4,
                "b", 2,
                "c", null,
                "c", 4,
            )
    }

    @Test
    fun `df cumSum default`() {
        val df = dataFrameOf(
            "doubles" to columnOf(1.0, 2.0, null),
            "shorts" to columnOf(1.toShort(), 2.toShort(), null),
            "bigInts" to columnOf(1.toBigInteger(), 2.toBigInteger(), null),
            "mixed" to columnOf<Number?>(1.0, 2, null),
            "group" to columnOf(
                "ints" to columnOf(1, 2, 3),
            ),
        )

        val res = df.cumSum()

        // works for Doubles, turns nulls into NaNs
        res["doubles"].values() shouldBe columnOf(1.0, 3.0, Double.NaN).values()
        // works for Shorts, turns into Ints, skips nulls
        res["shorts"].values() shouldBe columnOf(1, 3, null).values()
        // does not work for big numbers, keeps them as is
        res["bigInts"].values() shouldBe columnOf(1.toBigInteger(), 2.toBigInteger(), null).values()
        // works for mixed columns of primitives, number-unifies them; in this case to Doubles
        res["mixed"].values() shouldBe columnOf(1.0, 3.0, Double.NaN).values()
        // runs at any depth
        res["group"]["ints"].values() shouldBe columnOf(1, 3, 6).values()
    }

    @Test
    fun `cumSumTypeConversion tests`() {
        cumSumTypeConversion(typeOf<Int>(), false) shouldBe typeOf<Int>()
        cumSumTypeConversion(typeOf<Long?>(), false) shouldBe typeOf<Long?>()
        cumSumTypeConversion(typeOf<Short?>(), false) shouldBe typeOf<Int?>()
        cumSumTypeConversion(typeOf<Byte>(), false) shouldBe typeOf<Int>()
        cumSumTypeConversion(typeOf<Float?>(), false) shouldBe typeOf<Float>()
        cumSumTypeConversion(typeOf<Double?>(), false) shouldBe typeOf<Double>()
        cumSumTypeConversion(typeOf<Double>(), false) shouldBe typeOf<Double>()
        cumSumTypeConversion(nullableNothingType, false) shouldBe nullableNothingType

        shouldThrow<IllegalStateException> { cumSumTypeConversion(typeOf<String>(), false) }
    }
}
