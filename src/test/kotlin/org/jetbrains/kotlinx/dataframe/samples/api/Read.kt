package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.test.testCsv
import org.jetbrains.kotlinx.dataframe.test.testJson
import org.junit.Test

class Read : TestBase() {
    @Test
    fun readCsvCustom() {
        val file = testCsv("syntheticSample")
        // SampleStart
        val df = DataFrame.readCSV(
            file,
            delimiter = '|',
            headers = listOf("A", "B", "C", "D"),
            nullStrings = setOf("not assigned")
        )
        // SampleEnd
        df.nrow() shouldBe 3
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe getType<Int>()
        df["D"].type() shouldBe getType<Boolean?>()
    }

    @Test
    fun readJson() {
        val file = testJson("synthetic")
        // SampleStart
        val df = DataFrame.readJson(file)
        // SampleEnd
        df.nrow() shouldBe 4
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe getType<String>()
        df["B"].type() shouldBe getType<Int>()
        df["D"].type() shouldBe getType<Boolean?>()
    }
}
