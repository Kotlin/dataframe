package org.jetbrains.kotlinx.dataframe.person

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.dataFrameOf

@DataSchema
interface Person {
    val name: String
    val age: Int
    val city: String?
    val weight: Int?
}

open class BaseTest {

// Data set

    val df = dataFrameOf("name", "age", "city", "weight")(
        "Alice", 15, "London", 54,
        "Bob", 45, "Dubai", 87,
        "Mark", 20, "Moscow", null,
        "Mark", 40, "Milan", null,
        "Bob", 30, "Tokyo", 68,
        "Alice", 20, null, 55,
        "Mark", 30, "Moscow", 90
    )

    val typed: DataFrame<Person> = df.cast()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>() named "age"
    val city = Person::city.toColumnAccessor()
    val weight by column<Int?>()
}
