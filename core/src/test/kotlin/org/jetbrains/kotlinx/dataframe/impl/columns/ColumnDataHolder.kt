package org.jetbrains.kotlinx.dataframe.impl.columns

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.map
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.BOXED_ARRAY
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.BOXED_ARRAY_WITH_NULL
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.COLLECTOR
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.DOUBLE_ARRAY
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.LIST
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.LIST_WITH_NULL
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnDataHolderTests.ColumnType.NON_PRIMITIVE
import org.jetbrains.kotlinx.dataframe.impl.createDataCollector
import org.jetbrains.kotlinx.dataframe.math.mean
import org.junit.Ignore
import org.junit.Test
import org.openjdk.jol.info.GraphLayout
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

// @Ignore
class ColumnDataHolderTests {

    enum class ColumnType(override val value: String) : DataSchemaEnum {
        LIST("list"),
        LIST_WITH_NULL("list with null"),
        BOXED_ARRAY("boxed array"),
        BOXED_ARRAY_WITH_NULL("boxed array with null"),
        DOUBLE_ARRAY("double array"),
        COLLECTOR("collector"),
        NON_PRIMITIVE("non-primitive"),
    }

    @DataSchema
    data class Result(
        val type: ColumnType,
        val creationTime: Duration,
        val processingTime: Duration,
        val size: Long,
    )

    // 5M rows, mutable ColumnDataHolder, also in Column Data Collector
    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|     creation|    processing|        size|
    // |--|----------------------|-------------|--------------|------------|
    // | 0|             COLLECTOR| 5.811827943s|  2.642763677s| 379,996,177|
    // | 1|        LIST_WITH_NULL| 3.856582576s|  3.515039598s| 372,159,419|
    // | 2| BOXED_ARRAY_WITH_NULL| 4.368591388s|  4.029615844s| 372,119,758|
    // | 3|         NON_PRIMITIVE| 4.674218094s|  4.254802090s| 492,129,366|
    // | 4|                  LIST| 1.026079164s|  9.910889437s| 132,255,918|
    // | 5|          DOUBLE_ARRAY| 393.596507ms| 10.135781208s| 132,201,040|
    // | 6|           BOXED_ARRAY| 652.903191ms| 10.343283330s| 132,197,921|
    // ⌎-------------------------------------------------------------------⌏
    // same on master:
    // ⌌-----------------------------------------------------------⌍
    // |  |           type|     creation|   processing|        size|
    // |--|---------------|-------------|-------------|------------|
    // | 0|      COLLECTOR| 2.740211637s| 1.942454645s| 254,261,240|
    // | 1| LIST_WITH_NULL| 756.111045ms| 2.362196244s| 250,476,070|
    // | 2|  NON_PRIMITIVE| 1.459294225s| 4.924777527s| 250,017,144|
    // | 3|           LIST| 809.479392ms| 9.246052314s| 430,426,371|
    // ⌎-----------------------------------------------------------⌏
    @Test
    fun `measuring speed of ColumnDataHolder creation`() {
        val size = 5e6.toInt()
        val content = { i: Int -> Random.nextDouble() }
        val nullableContent = { i: Int ->
            if (Random.nextBoolean()) {
                null
            } else {
                Random.nextDouble()
            }
        }
        val tests = buildList {
            repeat(5) {
                add(LIST)
                add(LIST_WITH_NULL)
                add(BOXED_ARRAY)
                add(BOXED_ARRAY_WITH_NULL)
                add(DOUBLE_ARRAY)
                add(COLLECTOR)
                add(NON_PRIMITIVE)
            }
        }.shuffled()

        val results = mutableListOf<Result>()

        val a by column<Double>()
        val b by column<Double>()
        val c by column<Double>()
        val d by column<Double>()

        for ((i, test) in tests.withIndex()) {
            println("running test [$i/${tests.lastIndex}]")
            val (df, time1) = measureTimedValue {
                when (test) {
                    LIST -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), List(size, content)),
                        DataColumn.createValueColumn(b.name(), List(size, content)),
                        DataColumn.createValueColumn(c.name(), List(size, content)),
                    )

                    NON_PRIMITIVE -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), List(size, nullableContent) + (1 to 2)),
                        DataColumn.createValueColumn(b.name(), List(size, nullableContent) + (1 to 2)),
                        DataColumn.createValueColumn(c.name(), List(size, nullableContent) + (1 to 2)),
                    )

                    LIST_WITH_NULL -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), List(size, nullableContent)),
                        DataColumn.createValueColumn(b.name(), List(size, nullableContent)),
                        DataColumn.createValueColumn(c.name(), List(size, nullableContent)),
                    )

                    BOXED_ARRAY -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), Array(size, content)),
                        DataColumn.createValueColumn(b.name(), Array(size, content)),
                        DataColumn.createValueColumn(c.name(), Array(size, content)),
                    )

                    BOXED_ARRAY_WITH_NULL -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), Array(size, nullableContent)),
                        DataColumn.createValueColumn(b.name(), Array(size, nullableContent)),
                        DataColumn.createValueColumn(c.name(), Array(size, nullableContent)),
                    )

                    DOUBLE_ARRAY -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), DoubleArray(size, content)),
                        DataColumn.createValueColumn(b.name(), DoubleArray(size, content)),
                        DataColumn.createValueColumn(c.name(), DoubleArray(size, content)),
                    )

                    COLLECTOR -> dataFrameOf(
                        createDataCollector().also { cdc ->
                            repeat(size) { cdc.add(nullableContent(it)) }
                        }.toColumn(a.name()),
                        createDataCollector().also { cdc ->
                            repeat(size) { cdc.add(nullableContent(it)) }
                        }.toColumn(b.name()),
                        createDataCollector().also { cdc ->
                            repeat(size) { cdc.add(nullableContent(it)) }
                        }.toColumn(c.name()),
                    )
                }
            }

            df.columns().forEach {
                ((it as DataColumnImpl<*>).values as ColumnDataHolderImpl<*>)
                    .usesPrimitiveArrayList shouldBe (test != NON_PRIMITIVE)
            }

            val time2 = measureTime {
                df.drop { "a"<Any?>() !is Double || "b"<Any?>() !is Double || "c"<Any?>() !is Double }
                    .fillNulls { a and b and c }.with { 0.0 }
                    .filter { a() > b() }
                    .add(d) { a() + b() + c() }
            }

            val footprint = try {
                GraphLayout.parseInstance(df).toFootprint()
            } catch (e: Throwable) {
                throw Exception("failed test: $test", e)
            }
            val size = footprint.lines()
                .last { "total" in it }
                .split(" ")
                .mapNotNull { it.toLongOrNull() }
                .last()

            results += Result(test, time1, time2, size)
        }

        results.toDataFrame()
            .groupBy { type }
            .aggregate {
                creationTime.toList().mean() into "creation"
                processingTime.toList().mean() into "processing"
                this.size.toList().mean() into "size"
            }
            .sortBy { "processing"() }
            .replace { this.size.cast<Double>() }.with {
                it.map {
                    it.toLong().toString()
                        .reversed()
                        .chunked(3)
                        .reversed()
                        .joinToString(",") { it.reversed() }
                }
            }
            .print(borders = true, title = true)

        results
    }

    fun Collection<Duration>.mean(): Duration = reduce { acc, duration -> acc + duration } / size

    @Ignore
    @Test
    fun `create large columns`() {
        val size = 100_000_000
        val content = { i: Int -> Random.nextDouble() }
        val nullableContent = { i: Int ->
            if (Random.nextBoolean()) {
                null
            } else {
                Random.nextDouble()
            }
        }
        val df = dataFrameOf(
            DataColumn.createValueColumn("a", DoubleArray(size, content)),
            DataColumn.createValueColumn("b", DoubleArray(size, content)),
            DataColumn.createValueColumn("c", DoubleArray(size, content)),
        )

        df.print()
    }

    @Test
    fun `create all types of columns`() {
        ColumnDataHolder.of<Int>(intArrayOf(1, 2, 3), INT).let {
            it shouldContainInOrder listOf(1, 2, 3)
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        }
        ColumnDataHolder.of<Int>(arrayOf(1, 2, 3), INT).let {
            it shouldContainInOrder listOf(1, 2, 3)
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        }
        ColumnDataHolder.of<Int?>(arrayOf(1, 2, null), NULLABLE_INT).let {
            it shouldContainInOrder listOf(1, 2, null)
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        }
        ColumnDataHolder.of<Int?>(listOf(1, 2, null), NULLABLE_INT).let {
            it shouldContainInOrder listOf(1, 2, null)
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        }
        ColumnDataHolder.of<Int?>(listOf(1, 2, null), NULLABLE_INT).let {
            it shouldContainInOrder listOf(1, 2, null)
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        }
        ColumnDataHolder.of<Any?>(
            any = listOf(Pair(1, 2), null, emptyList<Any>()),
            type = NULLABLE_ANY,
        ).let {
            it shouldContainInOrder listOf(Pair(1, 2), null, emptyList<Any>())
            (it as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe false
        }
    }

    @Test
    fun `create typed ColumnDataHolder by collecting values`() {
        val holder = ColumnDataHolder.emptyForType<Int?>(NULLABLE_INT, strictTypes = true)
        shouldThrow<IllegalArgumentException> { (holder as ColumnDataHolder<Any>).add(3.0) }
        holder.add(1)
        holder.add(2)
        holder.add(null)
        shouldThrow<IllegalArgumentException> { (holder as ColumnDataHolder<Any>).add(3.0) }
        holder.add(3)
        holder shouldContainInOrder listOf(1, 2, null, 3)
    }

    @Test
    fun `create untyped ColumnDataHolder by collecting values`() {
        val holder = ColumnDataHolder.empty<Any?>(strictTypes = false)
        holder.add(1)
        holder.add(2)
        (holder as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        holder.add(null)
        (holder as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe true
        holder.add(3.0) // should switch to mutableList here
        (holder as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe false
        holder.add(3)
        (holder as ColumnDataHolderImpl<*>).usesPrimitiveArrayList shouldBe false
        holder shouldContainInOrder listOf(1, 2, null, 3)
    }

    @Test
    fun temp() {
        val holder = PrimitiveArrayList<Any>(12)
        holder.initCapacity shouldBe 12
    }
}
