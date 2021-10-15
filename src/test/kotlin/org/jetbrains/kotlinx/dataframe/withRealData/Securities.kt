package org.jetbrains.kotlinx.dataframe.withRealData

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.pivot
import org.jetbrains.kotlinx.dataframe.api.rename
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.io.read
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
