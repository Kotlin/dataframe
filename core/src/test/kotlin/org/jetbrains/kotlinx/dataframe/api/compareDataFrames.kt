package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.impl.api.myersDifferenceAlgorithmImpl
import org.junit.Test

class CompareDataFramesTest {
    @Test
    fun `Need both to delete and insert rows, preserving some rows`() {
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
    fun `need to do nothing`() {
        val path = myersDifferenceAlgorithmImpl("aaaa", "aaaa")
        path shouldBe listOf()
    }

    @Test
    fun `need to remove each row of dfA and insert each row of dfB`() {
        val path = myersDifferenceAlgorithmImpl("abcd", "efgh")
        path shouldBe listOf()
    }

    @Test
    fun `need to add each row`() {
        val path = myersDifferenceAlgorithmImpl("", "abc")
        path shouldBe listOf()
    }
}
