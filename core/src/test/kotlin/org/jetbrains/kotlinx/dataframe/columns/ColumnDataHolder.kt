package org.jetbrains.kotlinx.dataframe.columns

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.BOXED_ARRAY
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.DOUBLE_ARRAY
import org.jetbrains.kotlinx.dataframe.columns.ColumnDataHolder.ColumnType.LIST
import org.junit.Test
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime

class ColumnDataHolder {

    enum class ColumnType {
        LIST,
        BOXED_ARRAY,
        DOUBLE_ARRAY,
    }

    @Test
    fun `measuring speed of ColumnDataHolder creation`() {
        val size = 50_000
        val content = { i: Int -> Random.nextDouble() }
        val tests = buildList {
            repeat(2_000) {
                add(LIST)
                add(BOXED_ARRAY)
                add(DOUBLE_ARRAY)
            }
        }.shuffled()

        val results = mapOf(
            LIST to mutableListOf<Duration>(),
            BOXED_ARRAY to mutableListOf(),
            DOUBLE_ARRAY to mutableListOf(),
        )

        for (test in tests) {
            val time = measureTime {
                val df = when (test) {
                    LIST -> dataFrameOf(
                        DataColumn.createValueColumn("a", List(size, content)),
                        DataColumn.createValueColumn("b", List(size, content)),
                    )

                    BOXED_ARRAY -> dataFrameOf(
                        DataColumn.createValueColumn("a", Array(size, content)),
                        DataColumn.createValueColumn("b", Array(size, content)),
                    )

                    DOUBLE_ARRAY -> dataFrameOf(
                        DataColumn.createValueColumn("a", DoubleArray(size, content)),
                        DataColumn.createValueColumn("b", DoubleArray(size, content)),
                    )
                }

                df.filter { "a"<Double>() > "b"<Double>() }
            }

            results[test]!!.add(time)
        }

        println("Results:")
        results.forEach { (type, times) ->
            println("$type: ${times.mean()}")
        }
    }

    fun Collection<Duration>.mean(): Duration = reduce { acc, duration -> acc + duration } / size
}
