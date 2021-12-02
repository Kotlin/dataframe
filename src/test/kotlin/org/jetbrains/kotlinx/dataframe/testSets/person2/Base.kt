package org.jetbrains.kotlinx.dataframe.testSets.person2

import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.dataFrameOf

open class Base {
    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight")(
        "Alice", "Cooper", 15, "London", 54,
        "Bob", "Dylan", 45, "Dubai", 87,
        "Mark", "Antony", 20, "Moscow", null,
        "Mark", "Avrely", 40, "Milan", null,
        "Bob", "Marley", 30, "Tokyo", 68,
        "Alice", "Lindt", 20, null, 55,
        "Mark", "Petrov", 30, "Moscow", 90
    ).group("firstName", "lastName").into("name")
        .cast<Person>()

    @DataSchema
    interface FullName {
        val firstName: String
        val lastName: String
    }

    @DataSchema
    interface Person {
        val name: DataRow<FullName>
        val age: Int
        val city: String?
        val weight: Int?
    }

}
