package org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.jetbrains.kotlinx.dataframe.api.toSequence
import org.jetbrains.kotlinx.dataframe.api.toSequenceOf
import org.jetbrains.kotlinx.dataframe.samples.DataFrameSampleHelper
import org.junit.Test

@Suppress("ktlint:standard:argument-list-wrapping")
class ToListSamples : DataFrameSampleHelper("toList", "api/collectionsInterop") {

    data class Input(val a: Int, val b: Int)

    @Test
    fun toListAfterCast() {
        // SampleStart
        data class Input(val a: Int, val b: Int)

        val df = dataFrameOf("a", "b")(1, 2, 3, 4)

        df.cast<Input>().toList()
            // SampleEnd
            .also { it shouldBe listOf(Input(1, 2), Input(3, 4)) }
            .toString().saveTextSample()
    }

    @Test
    fun toListOfNestedDataClass() {
        // SampleStart
        val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
            .group("name", "lastName").into("fullName")

        data class FullName(val name: String, val lastName: String)

        data class Person(val fullName: FullName, val age: Int)

        df.toListOf<Person>()
            // SampleEnd
            .also { it shouldBe listOf(Person(FullName("John", "Doe"), 21)) }
            .toString().saveTextSample()
    }

    @Test
    fun toSequenceAfterCast() {
        val df = dataFrameOf("a", "b")(1, 2, 3, 4)

        // SampleStart
        df.cast<Input>().toSequence().first()
            // SampleEnd
            .also { it shouldBe Input(1, 2) }
            .toString().saveTextSample()
    }

    @Test
    fun toSequenceOfFirst() {
        val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
            .group("name", "lastName").into("fullName")

        data class FullName(val name: String, val lastName: String)

        data class Person(val fullName: FullName, val age: Int)

        // SampleStart
        df.toSequenceOf<Person>().first()
            // SampleEnd
            .also { it shouldBe Person(FullName("John", "Doe"), 21) }
            .toString().saveTextSample()
    }

    // Not a sample: pins the page and KDoc sentence about `toList` and the compiler plugin.
    // TODO: see ISSUE-toList-compiler-plugin.md. Once fixed, update the "With the compiler plugin" sentences in
    //  `CommonToListDocs.FromTypeArgument` (`toList.kt`), in `toList.md#tolist` and in `collectionsInterop.md`.
    @Test
    fun `with the compiler plugin toList throws right after toDataFrame`() {
        // The compiler plugin gives `df` a generated type argument instead of `Input`.
        val df = listOf(Input(1, 2)).toDataFrame()

        shouldThrow<IllegalArgumentException> { df.toList() }
        df.toListOf<Input>() shouldBe listOf(Input(1, 2))
    }

    data class Output(val a: Int, val b: Int, val c: Int)

    // Not a sample: pins the "Interop with data classes" sentence about `df2` on collectionsInterop.md.
    @Test
    fun `with the compiler plugin toList throws after add`() {
        val df2 = listOf(Input(1, 2)).toDataFrame().add("c") { a + b }

        shouldThrow<IllegalArgumentException> { df2.toList() }
        df2.toListOf<Output>() shouldBe listOf(Output(1, 2, 3))
    }
}
