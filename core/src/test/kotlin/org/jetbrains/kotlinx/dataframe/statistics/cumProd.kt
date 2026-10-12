package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.asColumn
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.cumProd
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.name
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import java.math.BigInteger
import kotlin.random.Random
import kotlin.reflect.typeOf

class CumProdTests {

    @Test
    fun `integer column preserves name size type and input`() {
        val factors by columnOf(2, 3, 4)
        val result: DataColumn<Int> = factors.cumProd()

        result.toList() shouldBe listOf(2, 6, 24)
        result.name() shouldBe factors.name()
        result.size() shouldBe factors.size()
        result.type() shouldBe typeOf<Int>()
        factors.toList() shouldBe listOf(2, 3, 4)
    }

    @Test
    fun `nullable integer skips leading interior and trailing nulls without filling`() {
        val result: DataColumn<Int?> = columnOf(null, 2, null, null, 3, 4, null).cumProd()

        result.toList() shouldBe listOf(null, 2, null, null, 6, 24, null)
        result.type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `integer null propagates without skipping`() {
        columnOf(2, null, 3, 4).cumProd(skipNA = false).toList() shouldBe listOf(2, null, null, null)
        columnOf<Int?>(null, 3, 4).cumProd(skipNA = false).toList() shouldBe listOf(null, null, null)
    }

    @Test
    fun `byte products promote to Int before multiplication`() {
        val result: DataColumn<Int> = columnOf<Byte>(100, 2, 3).cumProd()

        result.toList() shouldBe listOf(100, 200, 600)
        result.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `nullable byte products promote while preserving nullability and propagation`() {
        val column = columnOf<Byte?>(100, null, 2)
        val result: DataColumn<Int?> = column.cumProd()

        result.toList() shouldBe listOf(100, null, 200)
        result.type() shouldBe typeOf<Int?>()
        column.cumProd(skipNA = false).toList() shouldBe listOf(100, null, null)
    }

    @Test
    fun `short products promote to Int before multiplication`() {
        val result: DataColumn<Int> = columnOf<Short>(20000, 2, 3).cumProd()

        result.toList() shouldBe listOf(20000, 40000, 120000)
        result.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `nullable short products promote while preserving nullability and propagation`() {
        val column = columnOf<Short?>(20000, null, 2)
        val result: DataColumn<Int?> = column.cumProd()

        result.toList() shouldBe listOf(20000, null, 40000)
        result.type() shouldBe typeOf<Int?>()
        column.cumProd(skipNA = false).toList() shouldBe listOf(20000, null, null)
    }

    @Test
    fun `long multiplication stays exact above double precision`() {
        val value = 9007199254740993L
        val result: DataColumn<Long> = columnOf(value, 2L, 1L).cumProd()

        result.toList() shouldBe listOf(value, 18014398509481986L, 18014398509481986L)
        result.type() shouldBe typeOf<Long>()
    }

    @Test
    fun `nullable long products skip or propagate null`() {
        val column = columnOf(2L, null, 3L)
        val result: DataColumn<Long?> = column.cumProd()

        result.toList() shouldBe listOf(2L, null, 6L)
        result.type() shouldBe typeOf<Long?>()
        column.cumProd(skipNA = false).toList() shouldBe listOf(2L, null, null)
    }

    @Test
    fun `float products preserve precision type`() {
        val result: DataColumn<Float> = columnOf(2.0f, 0.5f, -3.0f).cumProd()

        result.toList() shouldBe listOf(2.0f, 1.0f, -3.0f)
        result.type() shouldBe typeOf<Float>()
    }

    @Test
    fun `double products preserve precision type`() {
        val result: DataColumn<Double> = columnOf(2.0, 0.5, -3.0).cumProd()

        result.toList() shouldBe listOf(2.0, 1.0, -3.0)
        result.type() shouldBe typeOf<Double>()
    }

    @Test
    fun `float NaN is skipped or propagated`() {
        val column = columnOf(2.0f, Float.NaN, 3.0f)
        val skipping = column.cumProd().toList()

        skipping[0] shouldBe 2.0f
        skipping[1].isNaN() shouldBe true
        skipping[2] shouldBe 6.0f
        column.cumProd(skipNA = false).toList().drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `double NaN is skipped or propagated`() {
        val column = columnOf(Double.NaN, 2.0, Double.NaN, 3.0)
        val skipping = column.cumProd().toList()

        skipping[0].isNaN() shouldBe true
        skipping[1] shouldBe 2.0
        skipping[2].isNaN() shouldBe true
        skipping[3] shouldBe 6.0
        column.cumProd(skipNA = false).toList().all { it.isNaN() } shouldBe true
    }

    @Test
    fun `nullable float retains null and NaN separately`() {
        val result: DataColumn<Float?> = columnOf(2.0f, null, Float.NaN, 3.0f).cumProd()
        val values = result.toList()

        values[0] shouldBe 2.0f
        values[1] shouldBe null
        values[2]!!.isNaN() shouldBe true
        values[3] shouldBe 6.0f
        result.type() shouldBe typeOf<Float?>()
    }

    @Test
    fun `nullable double retains null and NaN separately`() {
        val result: DataColumn<Double?> = columnOf(2.0, null, Double.NaN, 3.0).cumProd()
        val values = result.toList()

        values[0] shouldBe 2.0
        values[1] shouldBe null
        values[2]!!.isNaN() shouldBe true
        values[3] shouldBe 6.0
        result.type() shouldBe typeOf<Double?>()
    }

    @Test
    fun `first null wins over later NaN when propagation is enabled`() {
        columnOf(2.0, null, Double.NaN, 3.0).cumProd(skipNA = false).toList() shouldBe
            listOf(2.0, null, null, null)
        columnOf(2.0f, null, Float.NaN, 3.0f).cumProd(skipNA = false).toList() shouldBe
            listOf(2.0f, null, null, null)
    }

    @Test
    fun `first NaN wins over later null when propagation is enabled`() {
        columnOf(2.0, Double.NaN, null, 3.0).cumProd(skipNA = false).toList().drop(1)
            .all { it?.isNaN() == true } shouldBe true
        columnOf(2.0f, Float.NaN, null, 3.0f).cumProd(skipNA = false).toList().drop(1)
            .all { it?.isNaN() == true } shouldBe true
    }

    @Test
    fun `empty columns preserve or promote declared types without identity rows`() {
        val byte = DataColumn.createValueColumn<Byte>("empty", emptyList(), typeOf<Byte>())
        val nullableByte = DataColumn.createValueColumn<Byte?>("empty", emptyList(), typeOf<Byte?>())
        val short = DataColumn.createValueColumn<Short>("empty", emptyList(), typeOf<Short>())
        val nullableShort = DataColumn.createValueColumn<Short?>("empty", emptyList(), typeOf<Short?>())
        val int = DataColumn.createValueColumn<Int>("empty", emptyList(), typeOf<Int>())
        val nullableInt = DataColumn.createValueColumn<Int?>("empty", emptyList(), typeOf<Int?>())
        val long = DataColumn.createValueColumn<Long>("empty", emptyList(), typeOf<Long>())
        val nullableLong = DataColumn.createValueColumn<Long?>("empty", emptyList(), typeOf<Long?>())
        val float = DataColumn.createValueColumn<Float>("empty", emptyList(), typeOf<Float>())
        val nullableFloat = DataColumn.createValueColumn<Float?>("empty", emptyList(), typeOf<Float?>())
        val double = DataColumn.createValueColumn<Double>("empty", emptyList(), typeOf<Double>())
        val nullableDouble = DataColumn.createValueColumn<Double?>("empty", emptyList(), typeOf<Double?>())

        for (skipNA in listOf(true, false)) {
            val results = listOf(
                byte.cumProd(skipNA),
                nullableByte.cumProd(skipNA),
                short.cumProd(skipNA),
                nullableShort.cumProd(skipNA),
                int.cumProd(skipNA),
                nullableInt.cumProd(skipNA),
                long.cumProd(skipNA),
                nullableLong.cumProd(skipNA),
                float.cumProd(skipNA),
                nullableFloat.cumProd(skipNA),
                double.cumProd(skipNA),
                nullableDouble.cumProd(skipNA),
            )
            results.all { it.size() == 0 && it.name() == "empty" } shouldBe true
            results.map { it.type() } shouldBe listOf(
                typeOf<Int>(),
                typeOf<Int?>(),
                typeOf<Int>(),
                typeOf<Int?>(),
                typeOf<Int>(),
                typeOf<Int?>(),
                typeOf<Long>(),
                typeOf<Long?>(),
                typeOf<Float>(),
                typeOf<Float?>(),
                typeOf<Double>(),
                typeOf<Double?>(),
            )
        }
    }

    @Test
    fun `all-null columns remain all-null for every supported numeric type`() {
        val results = listOf(
            columnOf<Byte?>(null, null).cumProd(),
            columnOf<Short?>(null, null).cumProd(),
            columnOf<Int?>(null, null).cumProd(),
            columnOf<Long?>(null, null).cumProd(),
            columnOf<Float?>(null, null).cumProd(),
            columnOf<Double?>(null, null).cumProd(),
        )

        results.forEach { it.toList() shouldBe listOf(null, null) }
    }

    @Test
    fun `all-NaN columns remain NaN in either mode`() {
        for (skipNA in listOf(true, false)) {
            columnOf(Double.NaN, Double.NaN).cumProd(skipNA).toList().all { it.isNaN() } shouldBe true
            columnOf(Float.NaN, Float.NaN).cumProd(skipNA).toList().all { it.isNaN() } shouldBe true
        }
    }

    @Test
    fun `nullable declarations with no nulls remain nullable`() {
        val column = DataColumn.createValueColumn("factors", listOf(2.0, 3.0), typeOf<Double?>())

        column.cumProd().type() shouldBe typeOf<Double?>()
    }

    @Test
    fun `negative factors and zero use ordinary multiplication`() {
        columnOf(-2, -3, -4, 0, 5).cumProd().toList() shouldBe listOf(-2, 6, -24, 0, 0)
    }

    @Test
    fun `integer overflow wraps with independent arbitrary precision reference`() {
        val value = BigInteger.valueOf(Int.MAX_VALUE.toLong())

        columnOf(Int.MAX_VALUE, 2, 3).cumProd().toList() shouldBe
            listOf(value.toInt(), (value * BigInteger.valueOf(2)).toInt(), (value * BigInteger.valueOf(6)).toInt())
    }

    @Test
    fun `long overflow wraps with independent arbitrary precision reference`() {
        val value = BigInteger.valueOf(Long.MAX_VALUE)

        columnOf(Long.MAX_VALUE, 2L, -1L).cumProd().toList() shouldBe
            listOf(value.toLong(), (value * BigInteger.valueOf(2)).toLong(), (value * BigInteger.valueOf(-2)).toLong())
    }

    @Test
    fun `floating overflow and underflow follow IEEE arithmetic`() {
        columnOf(Double.MAX_VALUE, 2.0).cumProd().toList() shouldBe listOf(Double.MAX_VALUE, Double.POSITIVE_INFINITY)
        columnOf(Float.MAX_VALUE, 2.0f).cumProd().toList() shouldBe listOf(Float.MAX_VALUE, Float.POSITIVE_INFINITY)
        columnOf(Double.MIN_VALUE, 0.5).cumProd().toList() shouldBe listOf(Double.MIN_VALUE, 0.0)
        columnOf(Float.MIN_VALUE, 0.5f).cumProd().toList() shouldBe listOf(Float.MIN_VALUE, 0.0f)
    }

    @Test
    fun `signed zeros follow multiplication sign rules`() {
        columnOf(-0.0, -2.0, -3.0).cumProd().toList().map { it.toBits() } shouldBe
            listOf((-0.0).toBits(), 0.0.toBits(), (-0.0).toBits())
        columnOf(-0.0f, -2.0f, -3.0f).cumProd().toList().map { it.toBits() } shouldBe
            listOf((-0.0f).toBits(), 0.0f.toBits(), (-0.0f).toBits())
    }

    @Test
    fun `arithmetic NaN is not skipped from accumulated product`() {
        val result = columnOf(Double.POSITIVE_INFINITY, 0.0, Double.NaN, 2.0).cumProd().toList()

        result[0] shouldBe Double.POSITIVE_INFINITY
        result.drop(1).all { it.isNaN() } shouldBe true
        columnOf(Float.POSITIVE_INFINITY, 0.0f, 2.0f).cumProd().toList().drop(1).all { it.isNaN() } shouldBe true
    }

    @Test
    fun `null input after arithmetic NaN is retained or propagated according to mode`() {
        val column = columnOf(Double.POSITIVE_INFINITY, 0.0, null, 2.0)
        val skipping = column.cumProd().toList()
        val propagating = column.cumProd(skipNA = false).toList()

        skipping[1]!!.isNaN() shouldBe true
        skipping[2] shouldBe null
        skipping[3]!!.isNaN() shouldBe true
        propagating[1]!!.isNaN() shouldBe true
        propagating.drop(2) shouldBe listOf(null, null)
    }

    @Test
    fun `default metadata is retained or promoted together with element type`() {
        val int = DataColumn.createValueColumn("values", listOf(2, 3), typeOf<Int>(), defaultValue = 42)
        val double = DataColumn.createValueColumn("values", listOf(2.0, 3.0), typeOf<Double>(), defaultValue = 0.5)
        val byte = DataColumn.createValueColumn<Byte>("values", listOf(2, 3), typeOf<Byte>(), defaultValue = 42)
        val short = DataColumn.createValueColumn<Short>("values", listOf(2, 3), typeOf<Short>(), defaultValue = 300)

        int.cumProd().defaultValue() shouldBe 42
        double.cumProd().defaultValue() shouldBe 0.5
        byte.cumProd().defaultValue() shouldBe 42
        short.cumProd().defaultValue() shouldBe 300
    }

    @Test
    fun `dataframe conversion preserves unselected columns and input`() {
        val factors by column<Int?>()
        val df = dataFrameOf("label", "factors")("a", 2, "b", null, "c", 3, "d", 4)
        val result = df.convert { factors }.asColumn { it.cumProd() }

        result["factors"].toList() shouldBe listOf(2, null, 6, 24)
        result["label"] shouldBe df["label"]
        df["factors"].toList() shouldBe listOf(2, null, 3, 4)
    }

    @Test
    fun `group transformations reset products and missing propagation`() {
        val factors by column<Int?>()
        val df = dataFrameOf("group", "factors")("a", 2, "b", 7, "a", null, "b", 4, "a", 3)
        val groups = df.groupBy("group")

        val skipping = groups.updateGroups { convert { factors }.asColumn { it.cumProd() } }.concat()
        val propagating = groups.updateGroups { convert { factors }.asColumn { it.cumProd(skipNA = false) } }.concat()

        skipping["factors"].toList() shouldBe listOf(2, null, 6, 7, 28)
        propagating["factors"].toList() shouldBe listOf(2, null, null, 7, 28)
    }

    @Test
    fun `seeded nullable Int products match arbitrary precision prefix reference`() {
        val random = Random(2147)
        repeat(100) {
            val input = List(random.nextInt(0, 100)) { if (random.nextInt(5) == 0) null else random.nextInt() }
            val column = DataColumn.createValueColumn("values", input, typeOf<Int?>())
            val expected = input.indices.map { i ->
                if (input[i] == null) {
                    null
                } else {
                    input.take(i + 1).filterNotNull().map { BigInteger.valueOf(it.toLong()) }
                        .fold(BigInteger.ONE, BigInteger::multiply).toInt()
                }
            }
            val firstNull = input.indexOf(null).let { if (it < 0) input.size else it }

            column.cumProd().toList() shouldBe expected
            column.cumProd(skipNA = false).toList() shouldBe expected.mapIndexed { i, v ->
                if (i >= firstNull) null else v
            }
        }
    }
}
