package org.jetbrains.kotlinx.dataframe.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrameByProperties
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.getType
import org.junit.Test

class BuildTests {

    data class Person(val name: String, val age: Int)

    val persons = listOf(Person("Alice", 15), Person("Bob", 20))

    @Test
    fun test1() {
        val df = persons.toDataFrameByProperties()
        df.ncol() shouldBe 2
        df.nrow() shouldBe 2
        df["name"].type() shouldBe getType<String>()
        df["age"].type() shouldBe getType<Int>()
    }

    @Test
    fun test2() {
        val df = persons.toDataFrame {
            "name" { name }
            "year of birth" { 2020 - age }
        }
        df.ncol() shouldBe 2
        df.nrow() shouldBe 2
        df["name"].type() shouldBe getType<String>()
        df["year of birth"].type() shouldBe getType<Int>()
    }

    @Test
    fun test3() {
        val list = persons + listOf(null)
        val df = list.toDataFrameByProperties()
        df.nrow() shouldBe 3
    }

    @Test(expected = IllegalArgumentException::class)
    fun `unequal column sizes`() {
        persons.toDataFrameByProperties() + column("id", listOf(1, 2, 3))
    }
}
