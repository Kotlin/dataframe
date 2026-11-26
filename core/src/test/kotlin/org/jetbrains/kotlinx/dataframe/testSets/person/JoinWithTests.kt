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
import org.jetbrains.kotlinx.dataframe.api.excludeJoinWith
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.filterJoinWith
import org.jetbrains.kotlinx.dataframe.api.fullJoinWith
import org.jetbrains.kotlinx.dataframe.api.innerJoinWith
import org.jetbrains.kotlinx.dataframe.api.joinWith
import org.jetbrains.kotlinx.dataframe.api.leftJoinWith
import org.jetbrains.kotlinx.dataframe.api.print
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rightJoinWith
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class JoinWithTests : BaseJoinTest() {

    @Test
    fun `inner join`() {
        val res = typed.joinWith(typed2) {
            name == right.name && city == right.origin
        }
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 7
        res["age1"].hasNulls() shouldBe false
        res["age1"].type() shouldBe typeOf<String>()
        res["age1"].values().all { it != null } shouldBe true
        res.count { name == "Charlie" && city == "Moscow" } shouldBe 4
        res.select { city and name }.distinct().rowsCount() shouldBe 3
        res[Person2::grade].hasNulls() shouldBe false
        res.age.type() shouldBe typeOf<Int>()
    }

    @Test
    fun `left join`() {
        val res = typed.leftJoinWith(typed2) { name == right.name && city == right.origin }

        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 10
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 6
        res.count { it["grade"] == null } shouldBe 3
        res.age.hasNulls() shouldBe false
        res.age.type() shouldBe typeOf<Int>()
        res["age1"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `right join`() {
        val res = typed.rightJoinWith(typed2) {
            name == right.name && city == right.origin
        }
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 9
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 4
        res[Person2::grade].hasNulls() shouldBe false
        res.age.hasNulls() shouldBe true
        res.age.type() shouldBe typeOf<Int?>()
        val newEntries = res.filter { it["age"] == null }
        newEntries.rowsCount() shouldBe 2
        newEntries.all { it["name1"] == "Bob" && it["origin"] == "Paris" && weight == null } shouldBe true
    }

    @Test
    fun `outer join`() {
        val res = typed.fullJoinWith(typed2) { name == right.name && city == right.origin }
        println(res)
        res.columnsCount() shouldBe 8
        res.rowsCount() shouldBe 12
        res.columns().all { it.hasNulls() } shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 7
        val distinct = res.select { name and age and city and weight }.distinct()
        val expected = typed.append(null, null, null, null)
        distinct shouldBe expected
        res.age.type() shouldBe typeOf<Int?>()
        res["age1"].type() shouldBe typeOf<String?>()
    }

    @Test
    fun `filter join`() {
        val res = typed.filterJoinWith(typed2) { city == right.origin }
        val expected = typed.innerJoinWith(typed2.select { origin }) { city == right.origin }.remove("origin")
        res shouldBe expected
    }

    @Test
    fun `filter not join`() {
        val res = typed.excludeJoinWith(typed2) { city == right.origin }
        res.rowsCount() shouldBe 3
        res.city.toSet() shouldBe typed.city.toSet() - typed2.origin.toSet()

        val indexColumn = column<Int>("__index__")
        val withIndex = typed.addId(indexColumn)
        val joined = withIndex.filterJoinWith(typed2) { city == right.origin }
        val joinedIndices = joined[indexColumn].toSet()
        val expected = withIndex.filter { !joinedIndices.contains(it[indexColumn]) }.remove(indexColumn)

        res shouldBe expected
    }

    @Test
    fun `exclude join`() {
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

        val res = df.append(4, "e").excludeJoin(df1)

        res.rowsCount() shouldBe 1
        res["a"].values() shouldBe listOf(4)
        res["b"].values() shouldBe listOf("e")
        res.columnsCount() shouldBe 2
        res["a"].type() shouldBe typeOf<Int>()
        res["b"].type() shouldBe typeOf<String>()
    }

    @Test
    fun `test overloads contract`() {
        typed.innerJoinWith(typed2) { name == right.name && city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Inner) { name == right.name && city == right.origin }
        typed.leftJoinWith(typed2) { name == right.name && city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Left) { name == right.name && city == right.origin }
        typed.rightJoinWith(typed2) { name == right.name && city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Right) { name == right.name && city == right.origin }
        typed.fullJoinWith(typed2) { name == right.name && city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Full) { name == right.name && city == right.origin }
        typed.excludeJoinWith(typed2) { city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Exclude) { city == right.origin }
        typed.filterJoinWith(typed2) { city == right.origin } shouldBe
            typed.joinWith(typed2, JoinType.Filter) { city == right.origin }
    }
}
