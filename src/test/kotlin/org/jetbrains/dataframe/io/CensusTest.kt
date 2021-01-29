package org.jetbrains.dataframe.io

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.jetbrains.dataframe.api.columns.allNulls
import org.jetbrains.dataframe.api.columns.isType
import org.junit.Test
import java.io.StringWriter

class CensusTest {

    val path = "data/census.csv"
    val df = DataFrame.read(path)

    @Test
    fun grouping() {

        df.columnNames().forEach { println(it) }
        val singleNames = df.columnNames().filter { !it.contains(":") }.toSet()
        val grouped = df.move {
            cols { it.name().split(":").let { it.size > 1 && !singleNames.contains(it.last()) } }
        }.into { it.name.split(":").filter { it.isNotBlank() }.reversed() }
        grouped.select { cols { it.isGroup() } }.ncol() shouldBe 93
    }

    @Test
    fun write(){

        val str = StringWriter()

        val fixed = df
            .cast { cols { it.allNulls() && !it.isType<String?>() } }
            .to<String>()

        fixed.writeCSV(str)
        val res = DataFrame.readDelimStr(str.toString())
        res shouldBe fixed
    }
}