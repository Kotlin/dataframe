package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test
import kotlin.Pair

class CompareDataFramesTest {
    @Test
    fun `Need both to delete and insert rows, preserving some rows`() {
        val path = myersDifferenceAlgorithmImpl("abcabba", "cbabac")
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 1),
            Pair(4, 1),
            Pair(5, 2),
            Pair(5, 3),
            Pair(6, 4),
            Pair(7, 5),
            Pair(7, 6),
        )
    }

    @Test
    fun `need to do nothing`() {
        val path = myersDifferenceAlgorithmImpl("aaaa", "aaaa")
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 1),
            Pair(2, 2),
            Pair(3, 3),
            Pair(4, 4),
        )
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB`() {
        val path = myersDifferenceAlgorithmImpl("abcd", "efgh")
        path shouldBe listOf(
            Pair(0, 0),
            Pair(1, 0),
            Pair(2, 0),
            Pair(3, 0),
            Pair(4, 0),
            Pair(4, 1),
            Pair(4, 2),
            Pair(4, 3),
            Pair(4, 4),
        )
    }

    @Test
    fun `need to add each row`() {
        val path = myersDifferenceAlgorithmImpl("", "abc")
        path shouldBe listOf(
            Pair(0, 0),
            Pair(0, 1),
            Pair(0, 2),
            Pair(0, 3),
        )
    }
}
