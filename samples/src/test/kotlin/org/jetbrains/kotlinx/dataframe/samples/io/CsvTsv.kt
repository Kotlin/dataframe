@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.writeCsv
import org.junit.Ignore
import org.junit.Test

class CsvTsv {

    @Ignore
    @Test
    fun readCsv() {
        // SampleStart
        val df = DataFrame.readCsv("example.csv")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readCsvViaUrl() {
        // SampleStart
        val df = DataFrame.readCsv("https://kotlin.github.io/dataframe/resources/example.csv")
        // SampleEnd
    }

    @Ignore
    @Test
    fun writeCsv() {
        val df = dataFrameOf("a" to columnOf(1))
        // SampleStart
        df.writeCsv("example.csv")
        // SampleEnd
    }
}
