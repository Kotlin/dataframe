package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.api.addId
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.excludePredicateJoin
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.filterPredicateJoin
import org.jetbrains.kotlinx.dataframe.api.fullPredicateJoin
import org.jetbrains.kotlinx.dataframe.api.innerPredicateJoin
import org.jetbrains.kotlinx.dataframe.api.leftPredicateJoin
import org.jetbrains.kotlinx.dataframe.api.predicateJoin
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rightPredicateJoin
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.Test

class PredicateJoinTests : BaseJoinTest() {

    @Test
    fun `inner join`() {
        val res = typed.predicateJoin(typed2) {
            name == right.name && city == right.origin
        }
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 7
        res["age1"].hasNulls() shouldBe false
        res.count { name == "Charlie" && city == "Moscow" } shouldBe 4
        res.select { city and name }.distinct().rowsCount() shouldBe 3
        res[Person2::grade].hasNulls() shouldBe false
    }

    @Test
    fun `left join`() {
        val res = typed.leftPredicateJoin(typed2) { name == right.name && city == right.origin }

        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 10
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 6
        res.count { it["grade"] == null } shouldBe 3
        res.age.hasNulls() shouldBe false
    }

    @Test
    fun `right join`() {
        val res = typed.rightPredicateJoin(typed2) {
            name == right.name && city == right.origin
        }
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 9
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 4
        res[Person2::grade].hasNulls() shouldBe false
        res.age.hasNulls() shouldBe true
        val newEntries = res.filter { it["age"] == null }
        newEntries.rowsCount() shouldBe 2
        newEntries.all { it["name1"] == "Bob" && it["origin"] == "Paris" && weight == null } shouldBe true
    }

    @Test
    fun `outer join`() {
        val res = typed.fullPredicateJoin(typed2) { name == right.name && city == right.origin }
        println(res)
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 12
        res.columns().all { it.hasNulls() } shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 7
        val distinct = res.select { name and age and city and weight }.distinct()
        val expected = typed.append(null, null, null, null)
        distinct shouldBe expected
    }

    @Test
    fun `filter join`() {
        val res = typed.filterPredicateJoin(typed2) { city == right.origin }
        val expected = typed.innerPredicateJoin(typed2.select { origin }) { city == right.origin }.remove("origin")
        res shouldBe expected
    }

    @Test
    fun `filter not join`() {
        val res = typed.excludePredicateJoin(typed2) { city == right.origin }
        res.rowsCount() shouldBe 3
        res.city.toSet() shouldBe typed.city.toSet() - typed2.origin.toSet()

        val indexColumn = column<Int>("__index__")
        val withIndex = typed.addId(indexColumn)
        val joined = withIndex.filterPredicateJoin(typed2) { city == right.origin }
        val joinedIndices = joined[indexColumn].toSet()
        val expected = withIndex.filter { !joinedIndices.contains(it[indexColumn]) }.remove(indexColumn)

        res shouldBe expected
    }

    @Test
    fun rightJoin() {
        val df = dataFrameOf("a", "b")(
            1, "a",
            2, "b",
            3, "c",
        )

        val df1 = dataFrameOf("a", "c")(
            5, "V",
            1, "I",
            2, "II",
            3, "III",
        )
        df.append(4, "e").excludeJoin(df1).print()
    }

    @Test
    fun `test overloads contract`() {
        typed.innerPredicateJoin(typed2) { name == right.name && city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Inner) { name == right.name && city == right.origin }
        typed.leftPredicateJoin(typed2) { name == right.name && city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Left) { name == right.name && city == right.origin }
        typed.rightPredicateJoin(typed2) { name == right.name && city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Right) { name == right.name && city == right.origin }
        typed.fullPredicateJoin(typed2) { name == right.name && city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Full) { name == right.name && city == right.origin }
        typed.excludePredicateJoin(typed2) { city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Exclude) { city == right.origin }
        typed.filterPredicateJoin(typed2) { city == right.origin } shouldBe typed.predicateJoin(typed2, JoinType.Filter) { city == right.origin }
    }
}
