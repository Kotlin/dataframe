package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class ToSequenceTests {

    data class Input(val a: Int, val b: Int)

    data class FullName(val name: String, val lastName: String)

    data class Person(val fullName: FullName, val age: Int)

    data class Student(val name: String, val age: Int)

    data class Counted(val a: Int) {
        init {
            created++
        }
    }

    companion object {
        // Number of `Counted` instances created so far
        var created = 0
    }

    // Backs the example in the `toSequence` KDoc.
    @Test
    fun `KDoc example toSequence gives instances of the type argument`() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)

        val sequence: Sequence<Input> = df.cast<Input>().toSequence()

        sequence.first() shouldBe Input(1, 2)
        sequence.toList() shouldBe listOf(Input(1, 2), Input(3, 4))
    }

    // Backs the example in the `toSequenceOf` KDoc.
    @Test
    fun `KDoc example toSequenceOf turns a column group into a nested data class`() {
        val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
            .group("name", "lastName").into("fullName")

        df.toSequenceOf<Person>().first() shouldBe Person(FullName("John", "Doe"), 21)
    }

    @Test
    fun `instances are created only when the sequence is iterated`() {
        val df = dataFrameOf("a")(1, 2, 3)
        created = 0

        val sequence = df.toSequenceOf<Counted>()
        created shouldBe 0

        // Reading the first row creates one instance, not three.
        sequence.first().a shouldBe 1
        created shouldBe 1
    }

    @Test
    fun `each iteration of the sequence creates the instances again`() {
        val df = dataFrameOf("a")(1, 2, 3)
        created = 0

        val sequence = df.toSequenceOf<Counted>()
        sequence.toList()
        sequence.toList()

        // Two iterations over three rows.
        created shouldBe 6
    }

    @Test
    fun `missing column throws when the function is called, before the iteration`() {
        val df = dataFrameOf("name")("Alice")

        // The sequence is never iterated.
        shouldThrow<IllegalStateException> { df.toSequenceOf<Student>() }
    }

    @Test
    fun `toSequence throws on a dataframe whose type argument is unknown`() {
        val df: DataFrame<*> = dataFrameOf("a", "b")(1, 2)

        // `T` resolves to `Any?`, which is not a data class.
        shouldThrow<IllegalArgumentException> { df.toSequence() }
    }

    @Test
    fun `toSequenceOf does not depend on the type argument of the dataframe`() {
        val df: DataFrame<Input> = listOf(Input(1, 2), Input(3, 4)).toDataFrame()

        data class OnlyB(val b: Int)

        df.toSequenceOf<OnlyB>().toList() shouldBe listOf(OnlyB(2), OnlyB(4))
    }
}
