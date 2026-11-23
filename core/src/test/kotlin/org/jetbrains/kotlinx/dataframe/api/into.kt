package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class IntoTests {

    private val cityDf = dataFrameOf(
        "city", "name", "age",
    )(
        "London", "Alice", 30,
        "London", "Bob", 20,
        "Paris", "Charlie", 25,
    )

    private val nameDf = dataFrameOf(
        "city", "firstName", "lastName", "age",
    )(
        "London", "Alice", "Smith", 30,
        "London", "Bob", "Brown", 20,
        "Paris", "Charlie", "Cooper", 25,
    ).group("firstName", "lastName").into("name")

    @Test
    fun `into on a reduced group by stores a value from the reduced row`() {
        val result = cityDf.groupBy("city").minBy("age").into("youngest") { "name"<String>() }

        result shouldBe
            dataFrameOf(
                "city", "youngest",
            )(
                "London", "Bob",
                "Paris", "Charlie",
            )
        result["youngest"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `into on a reduced group by computes the expression on the single reduced row of each group`() {
        // `next` must come from the reduced (minimal age) row: 20 + 1 and 25 + 1,
        // not from the first row (30 + 1) or from all rows of the group
        val result = cityDf.groupBy("city").minBy("age").into("next") { "age"<Int>() + 1 }

        result["city"].toList() shouldBe listOf("London", "Paris")
        result["next"].toList() shouldBe listOf(21, 26)
        result["next"].type() shouldBe typeOf<Int>()
    }

    @Test
    fun `into on a reduced group by stores a column group value from the reduced row`() {
        val result = nameDf.groupBy("city").minBy("age").into("youngest") { getColumnGroup("name") }

        result["youngest"].kind() shouldBe ColumnKind.Group
        val youngest = result["youngest"].asColumnGroup()
        youngest["firstName"].toList() shouldBe listOf("Bob", "Charlie")
        youngest["lastName"].toList() shouldBe listOf("Brown", "Cooper")
    }

    @Test
    fun `into with only a column name on a reduced group by stores the whole reduced row in a column group`() {
        val result = cityDf.groupBy("city").minBy("age").into("youngest")

        result["youngest"].kind() shouldBe ColumnKind.Group
        val youngest = result["youngest"].asColumnGroup()
        youngest["city"].toList() shouldBe listOf("London", "Paris")
        youngest["name"].toList() shouldBe listOf("Bob", "Charlie")
        youngest["age"].toList() shouldBe listOf(20, 25)
    }

    @Test
    fun `into on a reduced group by keeps a row with null value for a group with no reduced row`() {
        // no row of the "Paris" group satisfies the predicate of `first`
        val result = cityDf.groupBy("city").first { "age"<Int>() > 25 }.into("old") { "name"<String>() }

        result["city"].toList() shouldBe listOf("London", "Paris")
        result["old"].toList() shouldBe listOf("Alice", null)
        result["old"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `into with only a column name on a reduced group by keeps a row with missing values for a group with no reduced row`() {
        val result = cityDf.groupBy("city").first { "age"<Int>() > 25 }.into("old")

        result["city"].toList() shouldBe listOf("London", "Paris")
        result["old"].kind() shouldBe ColumnKind.Group
        val old = result["old"].asColumnGroup()
        old[0]["name"] shouldBe "Alice"
        old[1]["city"] shouldBe null
        old[1]["name"] shouldBe null
        old[1]["age"] shouldBe null
        old["city"].type() shouldBe typeOf<String?>()
        old["name"].type() shouldBe typeOf<String?>()
        old["age"].type() shouldBe typeOf<Int?>()
    }

    @Test
    fun `into on a reduced group by without a column name uses the name of the groups column`() {
        val result = cityDf.groupBy("city").minBy("age").into { "name"<String>() }

        result.columnNames() shouldBe listOf("city", "group")
        result["group"].toList() shouldBe listOf("Bob", "Charlie")
    }

    @Test
    fun `into on a reduced group by passes the reduced row as both the receiver and the argument of the expression`() {
        val viaReceiver = cityDf.groupBy("city").minBy("age").into("viaThis") { this["name"] }
        val viaArgument = cityDf.groupBy("city").minBy("age").into("viaIt") { it["name"] }

        // the values come from the reduced (minimal age) row: "Bob", not "Alice" from the first row
        viaReceiver["viaThis"].toList() shouldBe listOf("Bob", "Charlie")
        viaArgument["viaIt"].toList() shouldBe listOf("Bob", "Charlie")
    }

    @Test
    fun `into with only a column name on a reduced group by keeps a nested column group of a missing reduced row filled with nulls`() {
        // no row of the "Paris" group satisfies the predicate of `first`
        val result = nameDf.groupBy("city").first { "age"<Int>() > 25 }.into("old")

        val old = result["old"].asColumnGroup()
        // the nested `name` column group stays a column group; the missing cell is filled recursively
        old["name"].kind() shouldBe ColumnKind.Group
        val name = old["name"].asColumnGroup()
        name[0]["firstName"] shouldBe "Alice"
        name[1]["firstName"] shouldBe null
        name[1]["lastName"] shouldBe null
        name["firstName"].type() shouldBe typeOf<String?>()
        name["lastName"].type() shouldBe typeOf<String?>()
    }
}
