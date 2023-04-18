package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.impl.columns.asValueColumn

public open class TestBase {

    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Charlie", "Daniels", 20, "Moscow", null, false,
        "Charlie", "Chaplin", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Wolf", 20, null, 55, false,
        "Charlie", "Byrd", 30, "Moscow", 90, true
    ).group("firstName", "lastName").into("name").cast<Person>()

    val dfGroup = df.convert { name.firstName }.to {
        val firstName by it
        val secondName by it.map<_, String?> { null }.asValueColumn()
        val thirdName by it.map<_, String?> { null }.asValueColumn()

        dataFrameOf(firstName, secondName, thirdName)
            .cast<FirstNames>(verify = true)
            .asColumnGroup("firstName")
    }.cast<Person2>(verify = true)

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name>
        val weight: Int?
        val isHappy: Boolean
    }

    @DataSchema
    interface FirstNames {
        val firstName: String
        val secondName: String?
        val thirdName: String?
    }

    @DataSchema
    interface Name2 {
        val firstName: DataRow<FirstNames>
        val lastName: String
    }

    @DataSchema
    interface Person2 {
        val age: Int
        val city: String?
        val name: DataRow<Name2>
        val weight: Int?
        val isHappy: Boolean
    }


    infix fun <T, U : T> T.willBe(expected: U?) = shouldBe(expected)

    fun <T> Iterable<T>.shouldAllBeEqual(): Iterable<T> {
        this should {
            it.reduce { a, b -> a shouldBe b; b }
        }
        return this
    }
}
