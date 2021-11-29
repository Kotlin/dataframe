package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class CreateDataFrameTests {

    @Test
    public fun `visibility test`() {
        class Data {
            private val a = 1
            protected val b = 2
            internal val c = 3
            public val d = 4
        }

        listOf(Data()).createDataFrame() shouldBe dataFrameOf("d")(4)
    }
}
