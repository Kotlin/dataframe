package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.columnNames
import org.jetbrains.kotlinx.dataframe.api.columnTypes
import org.jetbrains.kotlinx.dataframe.api.convert
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.with
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
            header = listOf("A", "B", "C", "D"),
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

    @Test
    fun readJsonRow() {
        val file = testJson("syntheticObj")
        // SampleStart
        val row = DataRow.readJson(file)
        // SampleEnd
        row.columnNames() shouldBe listOf("A", "B", "C", "D")
        row.columnTypes() shouldBe listOf(typeOf<String>(), typeOf<Int>(), typeOf<Double>(), typeOf<Boolean>())
    }

    @Test
    fun fixMixedColumn() {
        // SampleStart
        val df = dataFrameOf("IDS")(100.0, "A100", "B100", "C100")
        val df1 = df.convert("IDS").with(inferType = true) {
            if (it is Double) {
                it.toLong().toString()
            } else {
                it
            }
        }
        df1["IDS"].type() shouldBe typeOf<String>()
        // SampleEnd
    }
}
