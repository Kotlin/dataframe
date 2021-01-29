package org.jetbrains.dataframe.io

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.cast
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.getType
import org.junit.Test
import java.io.StringWriter

class CsvTests {

    @Test
    fun readNulls(){
        val src = """
            first,second
            2,,
            3,,
        """.trimIndent()
        val df = DataFrame.readDelimStr(src)
        df.nrow() shouldBe 2
        df.ncol() shouldBe 2
        df["first"].type shouldBe getType<Int>()
        df["second"].values.all { it == null } shouldBe true
        df["second"].type shouldBe getType<String?>()
    }

    @Test
    fun write(){

        val df = dataFrameOf("col1", "col2")(
            1,null,
            2,null
        ).cast("col2").to<String>()

        val str = StringWriter()
        df.writeCSV(str)

        val res = DataFrame.readDelimStr(str.buffer.toString())

        res shouldBe df
    }
}