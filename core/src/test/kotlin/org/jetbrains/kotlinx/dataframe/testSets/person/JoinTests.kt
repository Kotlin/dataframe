package org.jetbrains.kotlinx.dataframe.testSets.person

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.JoinType
import org.jetbrains.kotlinx.dataframe.api.addId
import org.jetbrains.kotlinx.dataframe.api.all
import org.jetbrains.kotlinx.dataframe.api.append
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.dataframe.api.count
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.excludeJoin
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.filterJoin
import org.jetbrains.kotlinx.dataframe.api.fullJoin
import org.jetbrains.kotlinx.dataframe.api.innerJoin
import org.jetbrains.kotlinx.dataframe.api.join
import org.jetbrains.kotlinx.dataframe.api.leftJoin
import org.jetbrains.kotlinx.dataframe.api.remove
import org.jetbrains.kotlinx.dataframe.api.rightJoin
import org.jetbrains.kotlinx.dataframe.api.select
import org.junit.Test

class JoinTests : BaseJoinTest() {
    @Test
    fun `inner join`() {
        val res = typed.innerJoin(typed2) { name and it.city.match(right.origin) }
        res.columnsCount() shouldBe 6
        res.rowsCount() shouldBe 7
        res["age1"].hasNulls() shouldBe false
        res.count { name == "Charlie" && city == "Moscow" } shouldBe 4
        res.select { city and name }.distinct().rowsCount() shouldBe 3
        res[Person2::grade].hasNulls() shouldBe false
    }

    @Test
    fun `left join`() {
        val res = typed.leftJoin(typed2) { name and it.city.match(right.origin) }

        res.columnsCount() shouldBe 6
        res.rowsCount() shouldBe 10
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 6
        res.count { it["grade"] == null } shouldBe 3
        res.age.hasNulls() shouldBe false
    }

    @Test
    fun `right join`() {
        val res = typed.rightJoin(typed2) { name and it.city.match(right.origin) }

        res.columnsCount() shouldBe 6
        res.rowsCount() shouldBe 9
        res["age1"].hasNulls() shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 4
        res[Person2::grade].hasNulls() shouldBe false
        res.age.hasNulls() shouldBe true
        val newEntries = res.filter { it["age"] == null }
        newEntries.rowsCount() shouldBe 2
        newEntries.all { name == "Bob" && city == "Paris" && weight == null } shouldBe true
    }

    @Test
    fun `outer join`() {
        val res = typed.fullJoin(typed2) { name and it.city.match(right.origin) }
        println(res)
        res.columnsCount() shouldBe 6
        res.rowsCount() shouldBe 12
        res.name.hasNulls() shouldBe false
        res.columns().filter { it != res.name }.all { it.hasNulls() } shouldBe true
        res.select { city and name }.distinct().rowsCount() shouldBe 7
        val distinct = res.select { name and age and city and weight }.distinct()
        val expected = typed.append("Bob", null, "Paris", null)
        distinct shouldBe expected
    }

    @Test
    fun `filter join`() {
        val res = typed.filterJoin(typed2) { city.match(right.origin) }
        val expected = typed.innerJoin(typed2.select { origin }) { city.match(right.origin) }
        res shouldBe expected
    }

    @Test
    fun `filter not join`() {
        val res = typed.excludeJoin(typed2) { city.match(right.origin) }
        res.rowsCount() shouldBe 3
        res.city.toSet() shouldBe typed.city.toSet() - typed2.origin.toSet()

        val indexColumn = column<Int>("__index__")
        val withIndex = typed.addId(indexColumn)
        val joined = withIndex.filterJoin(typed2) { city.match(right.origin) }
        val joinedIndices = joined[indexColumn].toSet()
        val expected = withIndex.filter { !joinedIndices.contains(it[indexColumn]) }.remove(indexColumn)

        res shouldBe expected
    }

    @Test
    fun `test overloads contract`() {
        typed.innerJoin(typed2) {
            name and it.city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Inner) { name and it.city.match(right.origin) }
        typed.leftJoin(typed2) {
            name and it.city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Left) { name and it.city.match(right.origin) }
        typed.rightJoin(typed2) {
            name and it.city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Right) { name and it.city.match(right.origin) }
        typed.fullJoin(typed2) {
            name and it.city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Full) { name and it.city.match(right.origin) }
        typed.excludeJoin(typed2) {
            city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Exclude) { city.match(right.origin) }
        typed.filterJoin(typed2) {
            city.match(right.origin)
        } shouldBe typed.join(typed2, JoinType.Filter) { city.match(right.origin) }
    }
}
