package org.jetbrains.dataframe.withRealData

import io.kotest.matchers.shouldBe
import org.jetbrains.dataframe.into
import org.jetbrains.dataframe.pivot
import org.jetbrains.dataframe.rename
import org.jetbrains.dataframe.values
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.read
import org.jetbrains.kotlinx.dataframe.values
import org.junit.Test

class Securities {

    val df = DataFrame.read("data/securities.csv")

    @Test
    fun pivot() {
        val res = df.rename("id").into("rowId").pivot("columns").groupBy("rowId").values("data")
        res.nrow() shouldBe 100
        res.ncol() shouldBe 17
    }
}
