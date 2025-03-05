package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.junit.Test
import java.math.BigDecimal

class DescribeTests {

    @Test
    fun `describe all nulls`() {
        val a by columnOf(1, null)
        val df = dataFrameOf(a).drop(1)
        df.describe()["min"][0] shouldBe null
    }

    @Test
    fun `describe nullable Number column`() {
        val a by columnOf(
            1,
            2.0,
            3f,
            4L,
            5.toShort(),
            6.toByte(),
            7.toBigInteger(),
            8.toBigDecimal(),
            null,
        )
        val df = dataFrameOf(a)
        val describe = df.describe()
            .alsoDebug()
            .single()
        with(describe) {
            name shouldBe "a"
            type shouldBe "Number?"
            count shouldBe 9
            unique shouldBe 9
            nulls shouldBe 1
            top shouldBe 1
            freq shouldBe 1
            mean shouldBe 4.5
            std shouldBe 2.449489742783178
            min shouldBe 1.toBigDecimal()
            (p25 as BigDecimal).setScale(2) shouldBe 2.75.toBigDecimal()
            median shouldBe 4.toBigDecimal()
            p75 shouldBe 6.25.toBigDecimal()
            max shouldBe 8.toBigDecimal()
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
            min shouldBe 1.0 // TODO should be NaN too?
            p25 shouldBe 1.75
            median shouldBe 3.0
            p75.isNaN shouldBe true
            max.isNaN shouldBe true
        }
    }
}
