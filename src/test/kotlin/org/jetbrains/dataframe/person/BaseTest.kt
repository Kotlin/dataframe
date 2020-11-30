package org.jetbrains.dataframe.person

import org.jetbrains.dataframe.*

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

// Generated Code

    @DataFrameType
    interface Person {
        val name: String
        val age: Int
        val city: String?
        val weight: Int?
    }

    val DataFrameRow<Person>.name get() = this["name"] as String
    val DataFrameRow<Person>.age get() = this["age"] as Int
    val DataFrameRow<Person>.city get() = this["city"] as String?
    val DataFrameRow<Person>.weight get() = this["weight"] as Int?
    val DataFrame<Person>.name get() = this["name"].typed<String>()
    val DataFrame<Person>.age get() = this["age"].typed<Int>()
    val DataFrame<Person>.city get() = this["city"].typed<String?>()
    val DataFrame<Person>.weight get() = this["weight"].typed<Int?>()

    val typed: DataFrame<Person> = df.typed()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>("age")
    val city = Person::city.toColumnDef()
    val weight by column<Int?>("weight")
}