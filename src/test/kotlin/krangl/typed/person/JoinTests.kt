package krangl.typed.person

import io.kotlintest.shouldBe
import krangl.typed.*
import org.junit.Test

class JoinTests : BaseTest() {

    val df2 = dataFrameOf("name", "origin", "grade", "age")(
            "Alice", "London", 3, "young",
            "Alice", "London", 5, "old",
            "Bob", "Tokyo", 4, "young",
            "Bob", "Paris", 5, "old",
            "Mark", "Moscow", 1, "young",
            "Mark", "Moscow", 2, "old",
            "Bob", "Paris", 4, null
    )

// Generated Code

    @DataFrameType
    interface Person2 {
        val name: String
        val origin: String?
        val grade: Int
    }


    val TypedDataFrameRow<Person2>.name @JvmName("get-name") get() = this["name"] as String
    val TypedDataFrameRow<Person2>.origin get() = this["origin"] as String?
    val TypedDataFrameRow<Person2>.grade get() = this["grade"] as Int
    val TypedDataFrame<Person2>.name @JvmName("get-name") get() = this["name"].typed<String>()
    val TypedDataFrame<Person2>.origin get() = this["origin"].typed<String?>()
    val TypedDataFrame<Person2>.grade get() = this["grade"].typed<Int>()

    val typed2: TypedDataFrame<Person2> = df2.typed()

    @Test
    fun `inner join`() {

        val res = typed.innerJoin(typed2) { name and it.city.match(right.origin) }
        res.ncol shouldBe 6
        res.nrow shouldBe 7
        res["age_2"].nullable shouldBe false
        res.count { name == "Mark" && city == "Moscow" } shouldBe 4
        res.select { city and name }.distinct().nrow shouldBe 3
        res[Person2::grade].nullable shouldBe false
    }

    @Test
    fun `left join`() {

        val res = typed.leftJoin(typed2) { name and it.city.match(right.origin) }

        res.ncol shouldBe 6
        res.nrow shouldBe 10
        res["age_2"].nullable shouldBe true
        res.select { city and name }.distinct().nrow shouldBe 6
        res.count { it["grade"] == null } shouldBe 3
        res.age.nullable shouldBe false
    }

    @Test
    fun `right join`() {

        val res = typed.rightJoin(typed2) { name and it.city.match(right.origin) }

        res.ncol shouldBe 6
        res.nrow shouldBe 9
        res["age_2"].nullable shouldBe true
        res.select { city and name }.distinct().nrow shouldBe 4
        res[Person2::grade].nullable shouldBe false
        res.age.nullable shouldBe true
        val newEntries = res.filter { it["age"] == null }
        newEntries.nrow shouldBe 2
        newEntries.all { name == "Bob" && city == "Paris" && weight == null } shouldBe true
    }

    @Test
    fun `outer join`() {

        val res = typed.outerJoin(typed2) { name and it.city.match(right.origin) }

        res.ncol shouldBe 6
        res.nrow shouldBe 12
        res.name.nullable shouldBe false
        res.columns.filter { it != res.name }.all { it.nullable } shouldBe true
        res.select { city and name }.distinct().nrow shouldBe 7
        res.select { name and age and city and weight }.distinct() shouldBe typed.addRow("Bob", null, "Paris", null)
    }

    @Test
    fun `filter join`() {

        val res = typed.filterJoin(typed2) { city.match(right.origin) }
        val expected = typed.innerJoin(typed2.select { origin }) { city.match(right.origin) }
        res shouldBe expected
    }

    @Test
    fun `filter not join`() {

        val res = typed.filterNotJoin(typed2) { city.match(right.origin) }
        res.nrow shouldBe 3
        res.city.toSet() shouldBe typed.city.toSet() - typed2.origin.toSet()

        val indexColumn by column<Int>("__index__")
        val withIndex = typed.addRowNumber(indexColumn)
        val joined = withIndex.filterJoin(typed2) { city.match(right.origin) }
        val joinedIndices = joined[indexColumn].toSet()
        val expected = withIndex.filter { !joinedIndices.contains(it[indexColumn]) }.remove(indexColumn)

        res shouldBe expected
    }
}