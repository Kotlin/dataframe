package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.dataFrameOf

public open class TestBase {

    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight", "isHappy")(
        "Alice", "Cooper", 15, "London", 54, true,
        "Bob", "Dylan", 45, "Dubai", 87, true,
        "Mark", "Antony", 20, "Moscow", null, false,
        "Mark", "Avrely", 40, "Milan", null, true,
        "Bob", "Marley", 30, "Tokyo", 68, true,
        "Alice", "Lindt", 20, null, 55, false,
        "Mark", "Petrov", 30, "Moscow", 90, true
    ).group("firstName", "lastName").into("name").cast<Person>()

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

    infix fun <T, U : T> T.willBe(expected: U?) = shouldBe(expected)
}
