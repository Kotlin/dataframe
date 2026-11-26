@file:Suppress("EmptyRange")

package org.jetbrains.kotlinx.dataframe

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumn
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.jupyter.KotlinNotebookPluginUtils
import kotlin.random.Random

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class SortingBenchmark {

    @Param("10000", "100000", "1000000")
    var size: Int = 0

    @Param("int", "string", "double", "category", "list", "frame")
    lateinit var columnType: String

    private lateinit var df: DataFrame<*>
    private lateinit var columnPath: List<String>

    @Setup
    fun setup() {
        val random = Random(42)
        df = (0 until size).toDataFrame {
            "int" from { it }
            "string" from { "name_${random.nextInt(1000)}" }
            "double" from { random.nextDouble() }
            "category" from { listOf("A", "B", "C", "D").random(random) }
            "list" from { List(random.nextInt(1, 20)) { "tag$it" } }
            "frame" from {
                dataFrameOf("x" to List(random.nextInt(1, 50)) { random.nextInt() }.toColumn())
            }
        }
        columnPath = listOf(columnType)
    }

    @Benchmark
    fun sort(): DataFrame<*> {
        val sorted = KotlinNotebookPluginUtils.sortByColumns(df, listOf(columnPath), listOf(false))
        return KotlinNotebookPluginUtils.getRowsSubsetForRendering(sorted, 0, 20).value
    }
}
