package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.junit.Test

class DescribeTests {

    @Test
    fun `describe all nulls`() {
        val a by columnOf(1, null)
        val df = dataFrameOf(a).drop(1)
        df.describe()["min"][0] shouldBe null
    }

    @Test
    fun `describe nullable Number column`() {
        val a by columnOf<Number?>(
            1,
            2.0,
            3f,
            4L,
            5.toShort(),
            6.toByte(),
            null,
        )
        val df = dataFrameOf(a)
        val describe = df.describe()
            .alsoDebug()
            .single()
        with(describe) {
            name shouldBe "a"
            type shouldBe "Number?"
            count shouldBe 7
            unique shouldBe 7
            nulls shouldBe 1
            top shouldBe 1
            freq shouldBe 1
            mean shouldBe 3.5
            std shouldBe 1.8708286933869707
            min shouldBe 1.0
            p25 shouldBe 2.0
            median shouldBe 3.0
            p75 shouldBe 4.0
            max shouldBe 6.0
        }
    }

    @Test
    fun `describe with NaNs`() {
        val a by columnOf(1.0, 2.0, Double.NaN, 4.0)
        val df = dataFrameOf(a)
        val describe = df.describe()
            .alsoDebug()
            .single()
        with(describe) {
            name shouldBe "a"
            type shouldBe "Double"
            count shouldBe 4
            unique shouldBe 4
            nulls shouldBe 0
            top shouldBe 1
            freq shouldBe 1
            mean.shouldBeNaN()
            std.shouldBeNaN()
            min.isNaN shouldBe true
            p25 shouldBe 1.75
            median.isNaN shouldBe true
            p75.isNaN shouldBe true
            max.isNaN shouldBe true
        }
    }
}
