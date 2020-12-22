package org.jetbrains.dataframe.io

import org.jetbrains.dataframe.*
import org.junit.Ignore
import org.junit.Test

class CensusTest {

    val path = "data/census.csv"
    val df = readCSV(path)

    @Test
    @Ignore
    fun grouping(){

        df.columnNames().forEach { println(it) }
        val grouped = df.move { cols { it.name.contains(":") } }.into { it.name.split(":").reversed() }
        grouped.print()
    }
}