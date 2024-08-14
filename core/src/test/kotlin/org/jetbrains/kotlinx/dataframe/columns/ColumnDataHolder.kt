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

    // ⌌-------------------------------------------------------------------⌍
    // |  |                  type|   creation|  processing|            size|
    // |--|----------------------|-----------|------------|----------------|
    // | 0| BOXED_ARRAY_WITH_NULL| 1.668690ms| 40.072489ms| 14500481.813333|
    // | 1|        LIST_WITH_NULL| 9.142612ms| 41.064332ms| 14509001.813333|
    // | 2|                  LIST| 2.710987ms| 42.268814ms| 11496455.760000|
    // | 3|           BOXED_ARRAY| 2.415740ms| 42.270087ms| 11502541.520000|
    // | 4|          DOUBLE_ARRAY| 1.840757ms| 42.354001ms| 11499172.666667|
    // ⌎-------------------------------------------------------------------⌏
    @Test
    fun `measuring speed of ColumnDataHolder creation`() {
        val size = 50_000
        val content = { i: Int -> Random.nextDouble() }
        val tests = buildList {
            repeat(300) {
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

        for (test in tests) {
            val (df, time1) = measureTimedValue {
                when (test) {
                    LIST -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), List(size, content)),
                        DataColumn.createValueColumn(b.name(), List(size, content)),
                        DataColumn.createValueColumn(c.name(), List(size, content)),
                    )

                    LIST_WITH_NULL -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), List<Double?>(size - 1, content) + null),
                        DataColumn.createValueColumn(b.name(), List<Double?>(size - 1, content) + null),
                        DataColumn.createValueColumn(c.name(), List<Double?>(size - 1, content) + null),
                    )

                    BOXED_ARRAY -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), Array(size, content)),
                        DataColumn.createValueColumn(b.name(), Array(size, content)),
                        DataColumn.createValueColumn(c.name(), Array(size, content)),
                    )

                    BOXED_ARRAY_WITH_NULL -> dataFrameOf(
                        DataColumn.createValueColumn(a.name(), Array<Double?>(size - 1, content) + null),
                        DataColumn.createValueColumn(b.name(), Array<Double?>(size - 1, content) + null),
                        DataColumn.createValueColumn(c.name(), Array<Double?>(size - 1, content) + null),
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

            val footprint = GraphLayout.parseInstance(df).toFootprint()
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
}
