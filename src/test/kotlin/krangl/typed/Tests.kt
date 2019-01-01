package krangl.typed

import io.kotlintest.shouldBe
import krangl.*
import krangl.typed.tracking.trackColumnAccess
import org.junit.Test

class TypedDataFrameTests {
    val df = dataFrameOf("name", "age", "city")(
            "Alice", 15, "London",
            "Bob", 45, "Dubai",
            "Mark", 20, "Moscow",
            "Mark", 30, "Paris",
            "Mark", 40, "Milan",
            "Bob", 30, "Tokyo"
    )

    // Generated code

    @DataFrameType
    interface Person {
        val name: String
        val age: Int
        val city: String
    }

    val TypedDataFrameRow<Person>.name get() = this["name"] as String
    val TypedDataFrameRow<Person>.age get() = this["age"] as Int
    val TypedDataFrameRow<Person>.city get() = this["city"] as String
    val TypedDataFrame<Person>.name get() = this["name"] as StringCol
    val TypedDataFrame<Person>.age get() = this["age"] as IntCol
    val TypedDataFrame<Person>.city get() = this["city"] as StringCol

    val typed = df.typed<Person>()

    @Test
    fun `size`() {
        typed.size shouldBe DataFrameSize(df.ncol, df.nrow)
    }

    @Test
    fun `slicing`() {
        val sliced = typed[1..2]
        sliced.nrow shouldBe 2
        sliced[0].name shouldBe typed[1].name
    }

    @Test
    fun `access tracking`(){
        trackColumnAccess {
            typed[2].age
        } shouldBe listOf("age")
    }

    @Test
    fun `indexing`(){
        typed[1].age shouldBe 45
    }

    @Test
    fun `update`(){
        val updated = typed.update { age } with { age*2 }
        updated.age.values.toList() shouldBe (df["age"] * 2).values().toList()
    }

    @Test
    fun `resetToNull`(){
        val updated = typed.update { allColumns }.withNull()
        updated.columns.forEach{
            it.values().toList().forEach { it shouldBe null }
        }
    }

    @Test
    fun `groupBy`(){
        val a = typed.groupBy {name}.aggregate {
            count into "n"
            count {age > 25} into "old count"
            median {age} into "median age"
            min {age} into "min age"
            maxBy {age}.map { city } into "oldest origin"
            map { sortedBy{age}.first().city } into "youngest origin"
        }
        a.nrow shouldBe 3
        a["n"].values().toList() shouldBe listOf(1, 2, 3)
        a["old count"].values().toList() shouldBe listOf(0, 2, 2)
        a["median age"].values().toList() shouldBe listOf(15.0, 37.5, 30.0)
        a["min age"].values().toList() shouldBe listOf(15, 30, 20)
        a["oldest origin"].values().toList() shouldBe listOf("London", "Dubai", "Milan")
        a["youngest origin"].values().toList() shouldBe listOf("London", "Tokyo", "Moscow")
    }

    @Test
    fun `sorting`() {
        val data = typed.sortedBy{age}.sortedByDesc{name}.map { city }
        val expected = typed.rows.sortedBy { it.age }.sortedByDescending { it.name }.map { it.city }
        data shouldBe expected
    }
}