package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test
import kotlin.Pair

class CompareDataFramesTest {
    @Test
    fun `Need both to delete and insert rows, preserving some rows`() {
        //dfA
        val x by columnOf(0, 1, 2, 0, 1, 1, 0)
        val y by columnOf("a", "b", "c", "a", "b", "b", "a")
        val dfA = dataFrameOf(x, y)
        //dfB
        val k by columnOf(2, 1, 0, 1, 0, 2)
        val z by columnOf("c", "b", "a", "b", "a", "c")
        val dfB = dataFrameOf(k, z)
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 1),
            Pair(3, 2),
            Pair(4, 3),
            Pair(5, 4),
            Pair(6, 4),
            Pair(7, 5),
            Pair(7, 6),
        )
    }

    @Test
    fun `need to do nothing`() {
        //dfA
        val x by columnOf(0, 0, 0)
        val y by columnOf("a", "a", "a")
        val dfA = dataFrameOf(x, y)
        //dfB
        val k by columnOf(0, 0, 0)
        val z by columnOf("a", "a", "a")
        val dfB = dataFrameOf(k, z)
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 1),
            Pair(2, 2),
            Pair(3, 3),
        )
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB`() {
        //dfA
        val x by columnOf(0, 1, 2)
        val y by columnOf("a", "b", "c")
        val dfA = dataFrameOf(x, y)
        //dfB
        val k by columnOf(3, 4, 5)
        val z by columnOf("d", "e", "f")
        val dfB = dataFrameOf(k, z)
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 0),
            Pair(3, 1),
            Pair(3, 2),
            Pair(3, 3),
        )
    }

    @Test
    fun `need to add each row`() {
        //dfA
        val x by columnOf(listOf())
        val y by columnOf(listOf())
        val dfA = dataFrameOf(x, y)
        //dfB
        val k by columnOf(0, 1, 2)
        val z by columnOf("a", "b", "c")
        val dfB = dataFrameOf(k, z)
        val path = myersDifferenceAlgorithmImpl(dfA, dfB)
        path shouldBe listOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 2),
            Pair(0, 3),
        )
    }

    @Test
    fun `describe`() {
        //dfA
        val x by columnOf(0, 1, 2, 0, 1, 1, 0)
        val y by columnOf("a", "b", "c", "a", "b", "b", "a")
        val dfA = dataFrameOf(x, y)
        val r = dfA.describe()
        r shouldBe emptyDataFrame()
    }
}
