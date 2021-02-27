package org.jetbrains.dataframe.io

import io.kotlintest.shouldBe
import org.jetbrains.dataframe.DataFrame
import org.jetbrains.dataframe.api.columns.allNulls
import org.jetbrains.dataframe.getType
import org.jetbrains.dataframe.ncol
import org.jetbrains.dataframe.nrow
import org.jetbrains.dataframe.print
import org.junit.Ignore
import org.junit.Test

class ReadTests {

    @Test
    fun ghost(){
        DataFrame.read("data/ghost.json")
    }

    @Test
    fun readJsonNulls(){
        val data = """
            [{"a":null, "b":1},{"a":null, "b":2}]
        """.trimIndent()

        val df = DataFrame.readJsonStr(data)
        df.ncol shouldBe 2
        df.nrow shouldBe 2
        df["a"].hasNulls shouldBe true
        df["a"].allNulls() shouldBe true
        df.all { it["a"] == null } shouldBe true
        df["a"].type shouldBe getType<Any?>()
        df["b"].hasNulls shouldBe false
    }
}