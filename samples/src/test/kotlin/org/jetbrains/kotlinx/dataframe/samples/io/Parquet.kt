package org.jetbrains.kotlinx.dataframe.samples.io

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.readSqlQuery
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test
import java.sql.DriverManager
import org.jetbrains.kotlinx.dataframe.io.readArrowFeather
import org.jetbrains.kotlinx.dataframe.io.readParquet
import org.jetbrains.kotlinx.dataframe.testArrowFeather
import org.jetbrains.kotlinx.dataframe.testParquet

class Parquet {
    @Test
    fun readParquetFilePath() {
        val url = testParquet("sales")
        // SampleStart
        val df = DataFrame.readParquet(url)
        // SampleEnd
        df.rowsCount() shouldBe 1
        df.columnsCount() shouldBe 4
    }
}
