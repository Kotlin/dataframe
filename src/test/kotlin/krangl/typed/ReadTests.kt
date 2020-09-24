package krangl.typed

import org.apache.commons.csv.CSVFormat
import org.junit.Test

class ReadTests {

    @Test
    fun readCensus(){
        val df = readCSV("../jupyter notebooks/Kotlin/Census/cleanedCensus.csv")
        println(df)

        println(df.summary())
    }
}