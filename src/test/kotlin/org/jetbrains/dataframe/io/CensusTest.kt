package org.jetbrains.dataframe.io

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.*
import org.junit.Test

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
        grouped.select { cols { it.isGrouped() } }.ncol() shouldBe 93
    }
}