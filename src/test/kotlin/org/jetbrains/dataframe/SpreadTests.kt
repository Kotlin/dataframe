package org.jetbrains.dataframe

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.io.read
import org.junit.Test

class SpreadTests {

    @Test
    fun spread(){
        val df = DataFrame.read("data/securities.csv")
        val res = df.rename("id").into("rowId").spread("columns").by("data").into { it.toString() }
        res.nrow() shouldBe 100
        res.ncol() shouldBe 17
    }
}