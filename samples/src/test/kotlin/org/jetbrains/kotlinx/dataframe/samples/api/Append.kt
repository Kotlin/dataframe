package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.junit.Test

class Append {

    @DataSchema
    data class Person(val name: String, val age: Int)

    @Test
    fun `append uses the compiler plugin overload`() {
        val df = dataFrameOf(Person("Alice", 20))

        val result = df.append(Person("Bill", 30))

        result shouldBe dataFrameOf("name", "age")("Alice", 20, "Bill", 30)
    }
}
