@file:Suppress("UNUSED_VARIABLE", "unused")

package org.jetbrains.kotlinx.dataframe.samples.io

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.columnOf
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.io.readExcel
import org.jetbrains.kotlinx.dataframe.io.writeExcel
import org.junit.Ignore
import org.junit.Test

class Excel {

    @Ignore
    @Test
    fun readExcel() {
        // SampleStart
        val df = DataFrame.readExcel("example.xlsx")
        // SampleEnd
    }

    @Ignore
    @Test
    fun readExcelViaUrl() {
        // SampleStart
        val df = DataFrame.readExcel("https://kotlin.github.io/dataframe/resources/example.xlsx")
        // SampleEnd
    }

    @Ignore
    @Test
    fun writeExcel() {
        val df = dataFrameOf("a" to columnOf(1))
        // SampleStart
        df.writeExcel("example.xlsx")
        // SampleEnd
    }
}
