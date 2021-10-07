package docs.api

import org.jetbrains.dataframe.DataFrameBase
import org.jetbrains.dataframe.DataRow
import org.jetbrains.dataframe.DataRowBase
import org.jetbrains.dataframe.annotations.DataSchema
import org.jetbrains.dataframe.dataFrameOf
import org.jetbrains.dataframe.group
import org.jetbrains.dataframe.into
import org.jetbrains.dataframe.typed

public open class TestBase {

    val df = dataFrameOf("firstName", "lastName", "age", "city", "weight")(
        "Alice", "Cooper", 15, "London", 54,
        "Bob", "Dylan", 45, "Dubai", 87,
        "Mark", "Antony", 20, "Moscow", null,
        "Mark", "Avrely", 40, "Milan", null,
        "Bob", "Marley", 30, "Tokyo", 68,
        "Alice", "Lindt", 20, null, 55,
        "Mark", "Petrov", 30, "Moscow", 90
    ).group("firstName", "lastName").into("name").typed<Person>()

    @DataSchema
    interface Name {
        val firstName: String
        val lastName: String
    }

    val DataFrameBase<Name>.firstName: org.jetbrains.dataframe.columns.DataColumn<kotlin.String> @JvmName("Person1_first") get() = this["firstName"] as org.jetbrains.dataframe.columns.DataColumn<kotlin.String>
    val DataRowBase<Name>.firstName: kotlin.String @JvmName("Person1_first") get() = this["firstName"] as kotlin.String
    val DataFrameBase<Name>.lastName: org.jetbrains.dataframe.columns.DataColumn<kotlin.String> @JvmName("Person1_last") get() = this["lastName"] as org.jetbrains.dataframe.columns.DataColumn<kotlin.String>
    val DataRowBase<Name>.lastName: kotlin.String @JvmName("Person1_last") get() = this["lastName"] as kotlin.String

    @DataSchema
    interface Person {
        val age: Int
        val city: String?
        val name: DataRow<Name>
        val weight: Int?
    }

    val org.jetbrains.dataframe.DataFrameBase<Person>.age: org.jetbrains.dataframe.columns.ValueColumn<kotlin.Int>
        @JvmName(
            "Person_age"
        ) get() = this["age"] as org.jetbrains.dataframe.columns.ValueColumn<kotlin.Int>
    val org.jetbrains.dataframe.DataRowBase<Person>.age: kotlin.Int @JvmName("Person_age") get() = this["age"] as kotlin.Int
    val org.jetbrains.dataframe.DataFrameBase<Person>.city: org.jetbrains.dataframe.columns.DataColumn<kotlin.String?>
        @JvmName(
            "Person_city"
        ) get() = this["city"] as org.jetbrains.dataframe.columns.DataColumn<kotlin.String?>
    val org.jetbrains.dataframe.DataRowBase<Person>.city: kotlin.String? @JvmName("Person_city") get() = this["city"] as kotlin.String?
    val org.jetbrains.dataframe.DataFrameBase<Person>.name: org.jetbrains.dataframe.columns.ColumnGroup<Name> @JvmName("Person_name") get() = this["name"] as org.jetbrains.dataframe.columns.ColumnGroup<Name>
    val org.jetbrains.dataframe.DataRowBase<Person>.name: org.jetbrains.dataframe.DataRow<Name> @JvmName("Person_name") get() = this["name"] as org.jetbrains.dataframe.DataRow<Name>
    val org.jetbrains.dataframe.DataFrameBase<Person>.weight: org.jetbrains.dataframe.columns.DataColumn<kotlin.Int?>
        @JvmName(
            "Person_weight"
        ) get() = this["weight"] as org.jetbrains.dataframe.columns.DataColumn<kotlin.Int?>
    val org.jetbrains.dataframe.DataRowBase<Person>.weight: kotlin.Int? @JvmName("Person_weight") get() = this["weight"] as kotlin.Int?
}
