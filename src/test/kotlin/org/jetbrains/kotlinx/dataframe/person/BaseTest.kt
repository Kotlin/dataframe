package org.jetbrains.kotlinx.dataframe.person

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.jetbrains.kotlinx.dataframe.impl.columns.asValues

@DataSchema
interface Person {
    val name: String
    val age: Int
    val city: String?
    val weight: Int?
}

val DataRow<Person>.name get() = this["name"] as String
val DataRow<Person>.age get() = this["age"] as Int
val DataRow<Person>.city get() = this["city"] as String?
val DataRow<Person>.weight get() = this["weight"] as Int?
val ColumnsContainer<Person>.name get() = this["name"].asValues<String>()
val ColumnsContainer<Person>.age get() = this.get("age").asValues<Int>()
val ColumnsContainer<Person>.city get() = this["city"].asValues<String?>()
val ColumnsContainer<Person>.weight get() = this["weight"].asValues<Int?>()

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
