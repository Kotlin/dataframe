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
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl1
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl2
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl3
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl4
import org.jetbrains.kotlinx.dataframe.impl.api.groupByImpl5
import kotlin.random.Random

/** Compares the original groupBy implementation with five cumulative optimization steps. */
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class GroupByBenchmark {

    @Param(
        "ROWS_10K_GROUPS_10_KEYS_1_VALUES_4",
        "ROWS_100K_GROUPS_10_KEYS_1_VALUES_4",
        "ROWS_100K_GROUPS_1K_KEYS_1_VALUES_4",
        "ROWS_100K_GROUPS_1K_KEYS_3_VALUES_4",
        "ROWS_100K_GROUPS_1K_KEYS_1_VALUES_32",
        "ROWS_1M_GROUPS_1K_KEYS_1_VALUES_4",
    )
    lateinit var scenario: String

    private lateinit var df: DataFrame<*>
    private lateinit var keyColumnNames: Array<String>

    @Setup
    fun setup() {
        val parameters = GroupByScenario.valueOf(scenario)
        val random = Random(42)
        val groupIds = (0 until parameters.rows).map { it % parameters.groups }.shuffled(random)

        df = (0 until parameters.rows).toDataFrame {
            "key1" from { groupIds[it] }
            if (parameters.keys == 3) {
                "key2" from { "bucket_${groupIds[it] % 17}" }
                "key3" from { groupIds[it] % 31 }
            }
            repeat(parameters.values) { columnIndex ->
                "value$columnIndex" from { random.nextInt() }
            }
        }
        keyColumnNames = Array(parameters.keys) { "key${it + 1}" }
    }

    @Benchmark
    fun baseline(): GroupBy<*, *> = df.groupBy { keyColumnNames.toColumnSet() }

    @Benchmark
    fun step1ImperativeGrouping(): GroupBy<*, *> = df.groupByImpl1(true) { keyColumnNames.toColumnSet() }

    @Benchmark
    fun step2RawKeyColumns(): GroupBy<*, *> = df.groupByImpl2(true) { keyColumnNames.toColumnSet() }

    @Benchmark
    fun step3SingleKeyFastPath(): GroupBy<*, *> = df.groupByImpl3(true) { keyColumnNames.toColumnSet() }

    @Benchmark
    fun step4ColumnFirst(): GroupBy<*, *> = df.groupByImpl4(true) { keyColumnNames.toColumnSet() }

    @Benchmark
    fun step5ParallelColumns(): GroupBy<*, *> = df.groupByImpl5(true) { keyColumnNames.toColumnSet() }
}

private enum class GroupByScenario(
    val rows: Int,
    val groups: Int,
    val keys: Int,
    val values: Int,
) {
    ROWS_10K_GROUPS_10_KEYS_1_VALUES_4(10_000, 10, 1, 4),
    ROWS_100K_GROUPS_10_KEYS_1_VALUES_4(100_000, 10, 1, 4),
    ROWS_100K_GROUPS_1K_KEYS_1_VALUES_4(100_000, 1_000, 1, 4),
    ROWS_100K_GROUPS_1K_KEYS_3_VALUES_4(100_000, 1_000, 3, 4),
    ROWS_100K_GROUPS_1K_KEYS_1_VALUES_32(100_000, 1_000, 1, 32),
    ROWS_1M_GROUPS_1K_KEYS_1_VALUES_4(1_000_000, 1_000, 1, 4),
}
