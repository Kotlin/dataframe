package org.jetbrains.kotlinx.dataframe.testSets.person

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.toColumnAccessor

@DataSchema
interface Person {
    val name: String
    val age: Int
    val city: String?
    val weight: Int?
}

val DataRow<Person>.name: String get() = this["name"] as String
val DataRow<Person>.age: Int get() = this["age"] as Int
val DataRow<Person>.city: String? get() = this["city"] as String?
val DataRow<Person>.weight: Int? get() = this["weight"] as Int?
val ColumnsContainer<Person>.name: DataColumn<String> get() = this["name"] as DataColumn<String>
val ColumnsContainer<Person>.age: DataColumn<Int> get() = this["age"] as DataColumn<Int>
val ColumnsContainer<Person>.city: DataColumn<String?> get() = this["city"] as DataColumn<String?>
val ColumnsContainer<Person>.weight: DataColumn<Int?> get() = this["weight"] as DataColumn<Int?>

open class BaseTest {

// Data set

    val df = dataFrameOf("name", "age", "city", "weight")(
        "Alice", 15, "London", 54,
        "Bob", 45, "Dubai", 87,
        "Charlie", 20, "Moscow", null,
        "Charlie", 40, "Milan", null,
        "Bob", 30, "Tokyo", 68,
        "Alice", 20, null, 55,
        "Charlie", 30, "Moscow", 90
    )

    val typed: DataFrame<Person> = df.cast()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>() named "age"
    val city = Person::city.toColumnAccessor()
    val weight by column<Int?>()
}
