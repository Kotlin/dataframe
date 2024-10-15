package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.TearDown
import org.openjdk.jmh.annotations.Warmup
import java.io.File
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 20, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
open class BenchmarkTest {

    @Param("small", "medium", "large")
    var type = ""
    var file: File? = null

    @Setup
    fun setup() {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info")
        file = File(
            "src/test/resources/" + when (type) {
                "small" -> "testCSV.csv"
                "medium" -> "gross-domestic-product-june-2024-quarter.csv"
                "large" -> "largeCsv.csv.gz"
                else -> throw IllegalArgumentException("Invalid type")
            },
        )
    }

    @TearDown
    fun tearDown() {
        file = null
    }

    @Benchmark
    fun apache() {
        DataFrame.readCSV(file!!)
    }

    @OptIn(ExperimentalCsv::class)
    @Benchmark
    fun deephaven() {
        DataFrame.readCsv(file!!)
    }
}
