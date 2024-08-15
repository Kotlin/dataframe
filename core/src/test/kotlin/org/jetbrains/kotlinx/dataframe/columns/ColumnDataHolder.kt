package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.DataSchemaEnum
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.aggregate
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.fillNulls
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.BOXED_ARRAY
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.BOXED_ARRAY_WITH_NULL
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.DOUBLE_ARRAY
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.LIST
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.LIST_WITH_NULL
import org.jetbrains.kotlinx.dataframe.math.mean
import org.junit.Test
import org.openjdk.jol.info.GraphLayout
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class ColumnDataHolder {

    enum class ColumnType(override val value: String) : DataSchemaEnum {
        LIST("list"),
        LIST_WITH_NULL("list with null"),
        BOXED_ARRAY("boxed array"),
        BOXED_ARRAY_WITH_NULL("boxed array with null"),
        DOUBLE_ARRAY("double array"),
    }

    @DataSchema
    data class Result(
        val type: ColumnType,
        val creationTime: Duration,
        val processingTime: Duration,
        val size: Long,
    )

    // 5M rows, with boolean arrays instead of int array, multiple nulls
    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|     creation|    processing|        size|
    // |--|----------------------|-------------|--------------|------------|
    // | 0|        LIST_WITH_NULL| 1.539455562s| 12.908897743s| 145305200.8|
    // | 1| BOXED_ARRAY_WITH_NULL| 1.631661691s| 12.949199901s| 145305161.6|
    // | 2|                  LIST| 1.237262323s| 14.270608275s| 145304406.4|
    // | 3|          DOUBLE_ARRAY| 577.880107ms| 14.464996203s| 145305240.0|
    // | 4|           BOXED_ARRAY| 1.438983943s| 14.627522900s| 145296296.8|
    // ⌎-------------------------------------------------------------------⌏

    // 10M with int array, single null
    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|     creation|    processing|        size|
    // |--|----------------------|-------------|--------------|------------|
    // | 0|          DOUBLE_ARRAY| 452.407441ms| 10.661419664s| 250303579.2|
    // | 1| BOXED_ARRAY_WITH_NULL| 1.246602198s| 10.876937912s| 250303867.2|
    // | 2|                  LIST| 1.075708642s| 10.987466189s| 250303651.2|
    // | 3|           BOXED_ARRAY| 1.109656324s| 11.206449292s| 250308171.2|
    // | 4|        LIST_WITH_NULL| 1.878721075s| 11.211828024s| 250294786.4|
    // ⌎-------------------------------------------------------------------⌏

    // 10M boolean array multiple nulls
    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|     creation|    processing|        size|
    // |--|----------------------|-------------|--------------|------------|
    // | 0|        LIST_WITH_NULL| 1.896135155s| 12.906753380s| 280,393,248.0|
    // | 1| BOXED_ARRAY_WITH_NULL| 1.622306469s| 13.093053168s| 280,320,763.2|
    // | 2|          DOUBLE_ARRAY| 535.327248ms| 13.494416201s| 280,330,497.6|
    // | 3|                  LIST| 1.395451763s| 13.647339781s| 280,372,962.4|
    // | 4|           BOXED_ARRAY| 1.240805238s| 14.096025326s| 280,339,035.2|
    // ⌎-------------------------------------------------------------------⌏

    // 10M int array multiple nulls... probably preferable
    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|     creation|    processing|        size|
    // |--|----------------------|-------------|--------------|------------|
    // | 0|          DOUBLE_ARRAY| 472.084569ms| 13.341951593s| 250,313,040.0|
    // | 1|                  LIST| 1.395223809s| 13.447386786s| 250,312,961.6|
    // | 2| BOXED_ARRAY_WITH_NULL| 1.672050297s| 13.528234068s| 310,318,894.4|
    // | 3|           BOXED_ARRAY| 1.379209011s| 13.646054496s| 250,312,883.2|
    // | 4|        LIST_WITH_NULL| 2.950703003s| 14.230182141s| 310,293,660.8|
    // ⌎-------------------------------------------------------------------⌏
    @Test
    fun `measuring speed of ColumnDataHolder creation`() {
        val size = 1e7.toInt()
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
                }
            }

            val time2 = measureTime {
                df.fillNulls { a and b and c }.with { 0.0 }
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
            .print(borders = true, title = true)

        results
    }

    fun Collection<Duration>.mean(): Duration = reduce { acc, duration -> acc + duration } / size

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
}
