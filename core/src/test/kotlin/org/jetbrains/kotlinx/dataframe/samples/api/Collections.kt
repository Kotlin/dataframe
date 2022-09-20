package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.add
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.junit.Test

class Collections {

    @Test
    fun listInterop1() {
        // SampleStart
        data class Input(val a: Int, val b: Int)

        val list = listOf(Input(1, 2), Input(3, 4))
        // SampleEnd
    }

    @Test
    fun listInterop2() {
        data class Input(val a: Int, val b: Int)
        val list = listOf(Input(1, 2), Input(3, 4))
        // SampleStart
        val df = list.toDataFrame()
        // SampleEnd
    }

    @Test
    fun listInterop3() {
        val list = listOf(Input(1, 2), Input(3, 4))
        val df = list.toDataFrame()

        // SampleStart
        @DataSchema
        data class Input(val a: Int, val b: Int)

        val df2 = df.add("c") { a + b }
        // SampleEnd
    }

    @DataSchema
    data class Input(val a: Int, val b: Int)

    @DataSchema
    interface Input2 {
        val a: Int
        val b: Int
    }

    @Test
    fun listInterop4() {
        val list = listOf(Input(1, 2), Input(3, 4))
        val df2 = list.toDataFrame().add("c") { a + b }

        // SampleStart
        data class Output(val a: Int, val b: Int, val c: Int)

        val result = df2.toListOf<Output>()
        // SampleEnd
    }

    @Test
    fun listInterop5() {
        // SampleStart
        val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
            .group("name", "lastName").into("fullName")

        data class FullName(val name: String, val lastName: String)
        data class Person(val fullName: FullName, val age: Int)

        val persons = df.toListOf<Person>() // [Person(fullName = FullName(name = "John", lastName = "Doe"), age = 21)]
        // SampleEnd

        persons shouldBe listOf(Person(FullName("John", "Doe"), 21))
    }
}
