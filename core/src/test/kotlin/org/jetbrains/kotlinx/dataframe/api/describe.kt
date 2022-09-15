package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.junit.Test

class DescribeTests {

    @Test
    fun `describe all nulls`() {
        val a by columnOf(1, null)
        val df = dataFrameOf(a).drop(1)
        df.describe()["min"][0] shouldBe null
    }
}
