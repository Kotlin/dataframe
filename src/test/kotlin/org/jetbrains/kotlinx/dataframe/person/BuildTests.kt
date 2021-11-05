package org.jetbrains.kotlinx.dataframe.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.column
import org.junit.Test

class BuildTests {

    data class Person(val name: String, val age: Int)

    val persons = listOf(Person("Alice", 15), Person("Bob", 20))

    @Test
    fun test3() {
        val list = persons + listOf(null)
        val df = list.toDataFrame()
        df.nrow() shouldBe 3
    }

    @Test(expected = IllegalArgumentException::class)
    fun `unequal column sizes`() {
        persons.toDataFrame() + column("id", listOf(1, 2, 3))
    }
}
