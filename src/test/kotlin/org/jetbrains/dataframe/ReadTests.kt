package org.jetbrains.dataframe

import org.apache.commons.csv.CSVFormat
import org.junit.Ignore
import org.junit.Test

class ReadTests {

    @Test
    @Ignore
    fun readCensus(){
        val df = readCSV("../jupyter notebooks/Kotlin/Census/cleanedCensus.csv")

        println(df.summary())
    }
}