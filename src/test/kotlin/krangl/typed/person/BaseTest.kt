package krangl.typed.person

import krangl.typed.*

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

    val TypedDataFrameRow<Person>.name get() = this["name"] as String
    val TypedDataFrameRow<Person>.age get() = this["age"] as Int
    val TypedDataFrameRow<Person>.city get() = this["city"] as String?
    val TypedDataFrameRow<Person>.weight get() = this["weight"] as Int?
    val TypedDataFrame<Person>.name get() = this["name"].cast<String>()
    val TypedDataFrame<Person>.age get() = this["age"].cast<Int>()
    val TypedDataFrame<Person>.city get() = this["city"].cast<String?>()
    val TypedDataFrame<Person>.weight get() = this["weight"].cast<Int?>()

    val typed: TypedDataFrame<Person> = df.typed()

// Manual Column Definitions

    val name by column<String>()
    val age = column<Int>("age")
    val city = Person::city.toColumn()
    val weight by column<Int?>("weight")
}