package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.group
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
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

    val ColumnsContainer<Name>.firstName: DataColumn<String> @JvmName("Person1_first") get() = this["firstName"] as DataColumn<String>
    val DataRow<Name>.firstName: kotlin.String @JvmName("Person1_first") get() = this["firstName"] as kotlin.String
    val ColumnsContainer<Name>.lastName: DataColumn<String> @JvmName("Person1_last") get() = this["lastName"] as DataColumn<String>
    val DataRow<Name>.lastName: kotlin.String @JvmName("Person1_last") get() = this["lastName"] as kotlin.String

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name>
        val weight: Int?
        val isHappy: Boolean
    }

    val ColumnsContainer<Person>.age: ValueColumn<Int>
        @JvmName(
            "Person_age"
        ) get() = this["age"] as ValueColumn<Int>
    val DataRow<Person>.age: kotlin.Int @JvmName("Person_age") get() = this["age"] as kotlin.Int
    val ColumnsContainer<Person>.city: DataColumn<String?>
        @JvmName(
            "Person_city"
        ) get() = this["city"] as DataColumn<String?>
    val DataRow<Person>.city: kotlin.String? @JvmName("Person_city") get() = this["city"] as kotlin.String?
    val ColumnsContainer<Person>.name: ColumnGroup<Name> @JvmName("Person_name") get() = this["name"] as ColumnGroup<Name>
    val DataRow<Person>.name: DataRow<Name> @JvmName("Person_name") get() = this["name"] as DataRow<Name>
    val ColumnsContainer<Person>.weight: DataColumn<Int?>
        @JvmName(
            "Person_weight"
        ) get() = this["weight"] as DataColumn<Int?>
    val DataRow<Person>.weight: kotlin.Int? @JvmName("Person_weight") get() = this["weight"] as kotlin.Int?

    val ColumnsContainer<Person>.isHappy: DataColumn<Boolean>
        @JvmName(
            "Person_isHappy"
        ) get() = this["isHappy"] as DataColumn<Boolean>
    val DataRow<Person>.isHappy: kotlin.Boolean @JvmName("Person_isHappy") get() = this["isHappy"] as Boolean

    infix fun <T, U : T> T.willBe(expected: U?) = shouldBe(expected)
}
