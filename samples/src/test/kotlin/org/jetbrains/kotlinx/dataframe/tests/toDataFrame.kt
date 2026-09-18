package org.jetbrains.kotlinx.dataframe.tests

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.ColumnName
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.toList
import org.jetbrains.kotlinx.dataframe.api.toListOf
import org.junit.Test

class ToDataFrameTests {

    data class SimplePerson(val name: String, val age: Int)

    @DataSchema
    data class DataSchemaPerson(val name: String, val age: Int)

    // Test case for #1880
    @Test
    fun `simple Iterable to DataFrame`() {
        val people = listOf(
            SimplePerson("John", 25),
            SimplePerson("Jane", 30),
        )

        // DataFrame<SimplePerson_XX>
        val df = people.toDataFrame()
        df.name
        df.age

        shouldThrow<IllegalArgumentException> { df.toList() }
            .message shouldContain "is not a data class. `toList` is supported only for data classes."

        df.toListOf<SimplePerson>() shouldBe people
    }

    // Test case for #1880
    @Test
    fun `DataSchema Iterable to DataFrame`() {
        val people = listOf(
            DataSchemaPerson("John", 25),
            DataSchemaPerson("Jane", 30),
        )

        // DataFrame<DataSchemaPerson>
        val df: DataFrame<DataSchemaPerson> = people.toDataFrame()
        df.name
        df.age

        df.toList() shouldBe people
    }
}
