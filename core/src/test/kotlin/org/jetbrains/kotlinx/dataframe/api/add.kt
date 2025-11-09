package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test
import kotlin.reflect.typeOf

class AddTests {

    @Test
    fun `add with new`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        val added = df.add("Y") { if (x() == 0) 0 else (prev()?.newValue() ?: 0) + 1 }
        val expected = listOf(1, 2, 0, 1, 2, 3, 4, 0, 1, 2)
        added["Y"].values() shouldBe expected
    }

    @Test
    fun `throw for newValue at the next row`() {
        val x by columnOf(7, 2, 0, 3, 4, 2, 5, 0, 3, 4)
        val df = dataFrameOf(x)
        shouldThrow<IndexOutOfBoundsException> {
            df.add("y") { next()?.newValue() ?: 1 }
        }
    }

    private fun <T> AnyFrame.addValue(value: T) = add("value") { listOf(value) }

    @Test
    fun `add with generic function`() {
        val df = dataFrameOf("a")(1).addValue(2)
        df["value"].type() shouldBe typeOf<List<Any?>>()
    }

    @Test
    fun `fibonacci simple add`() {
        val df = DataFrame.empty(10).add("fibonacci") {
            if (index() < 2) 1 else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
        }

        df["fibonacci"].toList() shouldBe listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55)
    }

    @Test
    fun `fibonacci add dsl`() {
        val df = DataFrame.empty(10).add {
            "fibonacci1" from {
                if (index() < 2) 1 else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
            }
            expr {
                if (index() < 2) 1 else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
            } into "fibonacci2"
        }

        df["fibonacci1"].toList() shouldBe listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55)
        df["fibonacci2"].toList() shouldBe listOf(1, 1, 2, 3, 5, 8, 13, 21, 34, 55)
    }

    @Test
    fun `compare`() {
        val path = myersDifferenceAlgorithmImpl("abcabba", "cbabac")
        path shouldBe listOf()
//        path[5][1+13] shouldBe 7
//        //path[4][1+13] shouldBe 7
//
//        path[4][2+13] shouldBe 7
//        //path[3][2+13] shouldBe 7
//
//        path[3][1+13] shouldBe 5
//        //path[2][1+13] shouldBe 5
    }

    @Test
    fun `compare2`() {
        val path = myersDifferenceAlgorithmImpl("aaaa", "aaaa")
        path shouldBe listOf()
    }

    @Test
    fun `compare3`() {
        val path = myersDifferenceAlgorithmImpl("a", "ab")
        path shouldBe 1
    }

    @Test
    fun `compare4`() {
        val path = myersDifferenceAlgorithmImpl("ab", "a")
        path shouldBe 1
    }
}
