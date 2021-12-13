package org.jetbrains.kotlinx.dataframe.statistics

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.math.quickSelect
import org.junit.Test
import kotlin.random.Random

class QuickSelectTests {

    @Test
    fun empty() {
        shouldThrow<IndexOutOfBoundsException> {
            listOf<Int>().quickSelect(0)
        }
    }

    @Test
    fun short() {
        val list = listOf(2, 5, 1)
        list.quickSelect(0) shouldBe 1
        list.quickSelect(1) shouldBe 2
        list.quickSelect(2) shouldBe 5
    }

    @Test
    fun long() {
        val random = Random(120)
        val list = (0..20).shuffled(random)
        for (i in list)
            list.quickSelect(i) shouldBe i
    }
}
