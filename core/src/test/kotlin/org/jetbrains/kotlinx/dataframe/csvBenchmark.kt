package org.jetbrains.kotlinx.dataframe

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.Measurement
import kotlinx.benchmark.Mode
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.Warmup
import org.jetbrains.kotlinx.dataframe.io.readDelim
import org.jetbrains.kotlinx.dataframe.io.readDelimApacheSequential
import org.jetbrains.kotlinx.dataframe.io.readDelimDeephavenCsv
import org.jetbrains.kotlinx.dataframe.io.readDelimKotlinCsv
import org.jetbrains.kotlinx.dataframe.io.readDelimKotlinCsvSequential
import org.openjdk.jmh.annotations.Fork
import java.io.File
import java.io.InputStream
import java.io.Reader
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 10)
@BenchmarkMode(Mode.SingleShotTime)
@Measurement(iterations = 10, timeUnit = java.util.concurrent.TimeUnit.MILLISECONDS)
open class CsvBenchmark {

    val mediumFile = File(
        "/mnt/data/Download/Age-sex-by-ethnic-group-grouped-total-responses-census-usually-resident-population-counts-2006-2013-2018-Censuses-RC-TA-SA2-DHB/DimenLookupArea8277.csv",
    )

    val file = mediumFile
//    val file = largeFile

//    @Setup
//    fun setUp() {
//    }

//    @Benchmark
    fun kotlinCsvReader() {
        DataFrame.readDelimKotlinCsv(file.inputStream())
    }

//    @Benchmark
    fun kotlinCsvReaderSequential() {
        DataFrame.readDelimKotlinCsvSequential(file.inputStream())
    }

//    @Benchmark
    fun apacheCsvReader() {
        DataFrame.readDelim(file.reader())
    }

//    @Benchmark
    fun apacheCsvReaderSequential() {
        DataFrame.readDelimApacheSequential(file.reader())
    }

//    @Benchmark
    fun fastCsvReader() {
        DataFrame.readDelimFastCsv(file.reader())
    }

//    @Benchmark
    fun fastCsvReaderSequential() {
        DataFrame.readDelimFastCsvSequential(file.reader())
    }

    @Benchmark
    fun deephavenCsvReader() {
        DataFrame.readDelimDeephavenCsv(file.inputStream())
    }
}
