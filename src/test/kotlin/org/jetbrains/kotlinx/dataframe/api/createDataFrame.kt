package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.getType
import org.junit.Test

class CreateDataFrameTests {

    @Test
    fun `visibility test`() {
        class Data {
            private val a = 1
            protected val b = 2
            internal val c = 3
            public val d = 4
        }

        listOf(Data()).createDataFrame() shouldBe dataFrameOf("d")(4)
    }

    @Test
    fun `exception test`() {
        class Data {
            val a: Int get() = error("Error")
            val b = 1
        }

        val df = listOf(Data()).createDataFrame()
        df.ncol() shouldBe 2
        df.nrow() shouldBe 1
        df.columnTypes() shouldBe listOf(getType<IllegalStateException>(), getType<Int>())
        (df["a"][0] is IllegalStateException) shouldBe true
        df["b"][0] shouldBe 1
    }
}
