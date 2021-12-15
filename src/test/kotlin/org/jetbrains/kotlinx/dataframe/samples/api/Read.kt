package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readJson
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.testCsv
import org.jetbrains.kotlinx.dataframe.testJson
import org.junit.Test
import kotlin.reflect.typeOf

class Read : TestBase() {
    @Test
    fun readCsvCustom() {
        val file = testCsv("syntheticSample")
        // SampleStart
        val df = DataFrame.readCSV(
            file,
            delimiter = '|',
            headers = listOf("A", "B", "C", "D"),
            parserOptions = ParserOptions(nullStrings = setOf("not assigned"))
        )
        // SampleEnd
        df.nrow shouldBe 3
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }

    @Test
    fun readJson() {
        val file = testJson("synthetic")
        // SampleStart
        val df = DataFrame.readJson(file)
        // SampleEnd
        df.nrow shouldBe 4
        df.columnNames() shouldBe listOf("A", "B", "C", "D")
        df["A"].type() shouldBe typeOf<String>()
        df["B"].type() shouldBe typeOf<Int>()
        df["D"].type() shouldBe typeOf<Boolean?>()
    }
}
