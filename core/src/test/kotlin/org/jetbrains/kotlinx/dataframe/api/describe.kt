package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.junit.Test
import kotlin.reflect.typeOf

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
            median shouldBe 4.toBigDecimal()
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
            mean.isNaN() shouldBe true
            std.isNaN() shouldBe true
            min shouldBe 1.0 // TODO should be NaN too?
            median shouldBe 3.0
            max.isNaN shouldBe true
        }
    }
}
