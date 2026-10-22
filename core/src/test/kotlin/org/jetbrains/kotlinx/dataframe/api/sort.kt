package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.testResource
import org.jetbrains.kotlinx.dataframe.testSets.DsSalaries
import org.jetbrains.kotlinx.dataframe.testSets.companyLocation
import org.jetbrains.kotlinx.dataframe.testSets.companySize
import org.jetbrains.kotlinx.dataframe.testSets.salaryInUsd
import org.junit.Test

class SortTests : TestBase() {

    @Test
    fun `value column sort with`() {
        val col = DataColumn.createValueColumn("", listOf(1, 6, 8, 4, 2, 9))
        val sortedCol = col.sort()
        val descSortedCol = col.sortDesc()

        col.sortWith { a, b -> a - b } shouldBe sortedCol
        col.sortWith { a, b -> b - a } shouldBe descSortedCol

        col.sortWith(Int::compareTo) shouldBe sortedCol
        col.sortWith(compareBy { it }) shouldBe sortedCol
    }

    @Test
    fun `frame column sort with`() {
        val col = DataColumn.createFrameColumn(
            "",
            listOf(
                dataFrameOf("a")(1, 2),
                dataFrameOf("a")(1),
                dataFrameOf("a")(1, 2, 3),
            ),
        )
        val sortedCol = DataColumn.createFrameColumn(
            "",
            listOf(
                dataFrameOf("a")(1),
                dataFrameOf("a")(1, 2),
                dataFrameOf("a")(1, 2, 3),
            ),
        )

        col.sortWith { df1, df2 -> df1.nrow - df2.nrow } shouldBe sortedCol
        col.sortWith(compareBy { it.nrow }) shouldBe sortedCol
    }

    @Test
    fun `column group sort with`() {
        val a by column<Int>()
        val b by column<String>()

        val col = DataColumn.createColumnGroup(
            "",
            dataFrameOf(
                columnOf(1, 3, 2) named a,
                columnOf("hello", "world", "!") named b,
            ),
        )

        val sortedCol = DataColumn.createColumnGroup(
            "",
            dataFrameOf(
                columnOf(1, 2, 3) named a,
                columnOf("hello", "!", "world") named b,
            ),
        )

        col.sortWith { df1, df2 -> a.getValue(df1) - a.getValue(df2) } shouldBe sortedCol
        col.sortWith(compareBy { a.getValue(it) }) shouldBe sortedCol
        col.sortWith(compareBy { a.getValue(it) }) shouldBe sortedCol
    }

    @Test
    fun `sort by nested column`() {
        val df = DataFrame.readCsv(testResource("ds_salaries.csv")).cast<DsSalaries>()
        val aggregate = df.pivot(false) { companySize }.groupBy { companyLocation }.aggregate {
            maxOf { salaryInUsd } into "salary"
            maxBy { salaryInUsd } into "extra"
        }
        aggregate.sortBy { pathOf("L", "salary") }[0][pathOf("L", "salary")] shouldBe null
        aggregate.sortByDesc { pathOf("L", "salary") }[0][pathOf("L", "salary")] shouldBe 600_000
    }

    @Test
    fun `sort by invalid nested column`() {
        val df = DataFrame.readCsv(testResource("ds_salaries.csv")).cast<DsSalaries>()
        val aggregate = df.pivot(false) { companySize }.groupBy { companyLocation }.aggregate {
            maxOf { salaryInUsd } into "salary"
            maxBy { salaryInUsd } into "extra"
        }
        shouldThrowMessage("Can not use ColumnGroup as sort column") {
            aggregate.sortBy { pathOf("L", "extra") }
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun `grouped sort reports a missing nested column`() {
        val grouped = df.group { age }.into("group").groupBy { name.firstName }
        val path = pathOf("group", "missing")
        val message = "Could not update column 'group': " +
            "Column 'group/missing' not found among columns of 'group': [age]."

        shouldThrow<IllegalStateException> { grouped.sortBy { path } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc { path } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortBy(path) }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc(path) }.message shouldBe message
    }

    @Test
    fun `grouped sort rejects an extra group segment in the path`() {
        val grouped = df.group { age }.into("group").groupBy { name.firstName }
        val path = pathOf("group", "group", "age")
        val message = "Could not update column 'group': " +
            "Column 'group/group' not found among columns of 'group': [age]."

        shouldThrow<IllegalStateException> { grouped.sortByDesc { path } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortBy { path } }.message shouldBe message
    }

    @Test
    fun `grouped sort reports a missing column even when another column exists`() {
        val grouped = df.groupBy { name.firstName }
        val message = "Could not update column 'group': " +
            "Column 'missing' not found among [name, age, city, weight, isHappy]."

        shouldThrow<IllegalStateException> { grouped.sortBy("age", "missing") }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc("age", "missing") }.message shouldBe message
        shouldThrow<IllegalStateException> {
            grouped.sortBy { age and "missing"<Int>() }
        }.message shouldBe message
        shouldThrow<IllegalStateException> {
            grouped.sortByDesc { age and "missing"<Int>() }
        }.message shouldBe message
    }

    @Test
    fun `grouped sort reports a missing column in an expression`() {
        val grouped = df.groupBy { name.firstName }
        val message = "Could not update column 'group': " +
            "Column 'missing' not found among [name, age, city, weight, isHappy]."

        shouldThrow<IllegalStateException> {
            grouped.sortBy { expr { "missing"<Int>() * 2 } }
        }.message shouldBe message
        shouldThrow<IllegalStateException> {
            grouped.sortByDesc { expr { "missing"<Int>() * 2 } }
        }.message shouldBe message
    }

    @Test
    fun `grouped sort reports a missing column with sort flags`() {
        val grouped = df.groupBy { name.firstName }
        val path = pathOf("name", "missing")
        val message = "Could not update column 'group': " +
            "Column 'name/missing' not found among columns of 'name': [firstName, lastName]."

        shouldThrow<IllegalStateException> { grouped.sortBy { path.reversed() } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc { path.reversed() } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortBy { path.nullsLast() } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc { path.nullsLast() } }.message shouldBe message
    }

    @Test
    fun `grouped sort reports a column missing only in a later group`() {
        val grouped = df.groupBy { name.firstName }.updateGroups {
            if (name.firstName[0] == "Bob") remove { age } else this
        }
        grouped.groups.toList().map { it.containsColumn("age") } shouldBe listOf(true, false, true)
        val message = "Could not update column 'group': Column 'age' not found among [name, city, weight, isHappy]."

        shouldThrow<IllegalStateException> { grouped.sortBy { age } }.message shouldBe message
        shouldThrow<IllegalStateException> { grouped.sortByDesc { age } }.message shouldBe message
    }

    @Test
    fun `grouped sort resolves nested paths within each group`() {
        val grouped = df.group { age }.into("group").groupBy { name.firstName }
        val path = pathOf("group", "age")

        val ascending = grouped.sortBy { path }
        ascending.keys shouldBe grouped.keys
        ascending.groups.toList().map { it[path].toList() } shouldBe
            listOf(listOf(15, 20), listOf(30, 45), listOf(20, 30, 40))

        val descending = grouped.sortByDesc { path }
        descending.keys shouldBe grouped.keys
        descending.groups.toList().map { it[path].toList() } shouldBe
            listOf(listOf(20, 15), listOf(45, 30), listOf(40, 30, 20))
    }

    @Test
    fun `grouped sort allows an empty column selection`() {
        val grouped = df.groupBy { name.firstName }

        grouped.sortBy { none() }.toDataFrame() shouldBe grouped.toDataFrame()
        grouped.sortByDesc { none() }.toDataFrame() shouldBe grouped.toDataFrame()
    }

    @Test
    fun `grouped sort with type-safe selector sorts rows within groups`() {
        val grouped = df.groupBy { name.firstName }
        val actual = grouped.sortBy { age }

        actual.keys shouldBe grouped.keys
        actual.groups.toList().map { it.age.toList() } shouldBe
            listOf(listOf(15, 20), listOf(30, 45), listOf(20, 30, 40))
    }

    @Test
    fun `grouped sort by a grouping key preserves group order`() {
        val grouped = dataFrameOf(
            "key" to columnOf("b", "b", "a", "a"),
            "value" to columnOf(2, 1, 4, 3),
        ).groupBy("key")

        listOf(
            grouped.sortBy("key"),
            grouped.sortByDesc("key"),
            grouped.sortBy { "key"<String>() },
            grouped.sortByDesc { "key"<String>() },
        ).forEach { it.toDataFrame() shouldBe grouped.toDataFrame() }
    }

    @Test
    fun `grouped sort sorts rows without reordering groups`() {
        val grouped = dataFrameOf(
            "key" to columnOf("b", "b", "a", "a"),
            "value" to columnOf(2, 1, 4, 3),
        ).groupBy("key")

        listOf(grouped.sortBy("value"), grouped.sortBy { "value"<Int>() }).forEach { sorted ->
            sorted.keys shouldBe grouped.keys
            sorted.groups.toList().map { it["value"].toList() } shouldBe listOf(listOf(1, 2), listOf(3, 4))
        }

        listOf(grouped.sortByDesc("value"), grouped.sortByDesc { "value"<Int>() }).forEach { sorted ->
            sorted.keys shouldBe grouped.keys
            sorted.groups.toList().map { it["value"].toList() } shouldBe listOf(listOf(2, 1), listOf(4, 3))
        }
    }

    @Test
    fun `grouped sort preserves column priority and reversed flag`() {
        val grouped = dataFrameOf(
            "key" to columnOf("single", "single", "single"),
            "primary" to columnOf(1, 1, 2),
            "secondary" to columnOf(1, 2, 3),
            "id" to columnOf("A", "B", "C"),
        ).groupBy("key")
        val selector: SortColumnsSelector<Any?, *> = { "primary"<Int>() and "secondary"<Int>().reversed() }

        grouped.sortBy(selector).groups.single()["id"].toList() shouldBe listOf("B", "A", "C")
        grouped.sortByDesc(selector).groups.single()["id"].toList() shouldBe listOf("C", "A", "B")
    }

    @Test
    fun `grouped sort preserves nullsLast flag`() {
        val grouped = dataFrameOf(
            "key" to columnOf("single", "single", "single"),
            "value" to columnOf<Int?>(null, 1, 2),
        ).groupBy("key")
        val selector: SortColumnsSelector<Any?, *> = { "value"<Int?>().nullsLast() }

        grouped.sortBy(selector).groups.single()["value"].toList() shouldBe listOf(1, 2, null)
        grouped.sortByDesc(selector).groups.single()["value"].toList() shouldBe listOf(null, 2, 1)
    }

    @Test
    fun `grouped sort by an expression uses group rows`() {
        val grouped = df.groupBy { name.firstName }

        val ascending = grouped.sortBy { expr { age * 2 } }
        ascending.keys shouldBe grouped.keys
        ascending.groups.toList().map { it.age.toList() } shouldBe
            listOf(listOf(15, 20), listOf(30, 45), listOf(20, 30, 40))

        val descending = grouped.sortByDesc { expr { age * 2 } }
        descending.keys shouldBe grouped.keys
        descending.groups.toList().map { it.age.toList() } shouldBe
            listOf(listOf(20, 15), listOf(45, 30), listOf(40, 30, 20))
    }

    @Test
    fun `grouped outer sorting preserves group contents`() {
        val grouped = dataFrameOf(
            "key" to columnOf("b", "b", "a", "c", "c", "c"),
            "value" to columnOf(4, 2, 1, 6, 5, 3),
        ).groupBy("key")

        listOf(
            grouped.sortByKey(),
            grouped.sortByGroup { min("value") },
            grouped.sortByCountAsc(),
        ).forEach { it.toDataFrame() shouldBe grouped.toDataFrame()[listOf(1, 0, 2)] }

        listOf(
            grouped.sortByKeyDesc(),
            grouped.sortByGroupDesc { min("value") },
            grouped.sortByCount(),
        ).forEach { it.toDataFrame() shouldBe grouped.toDataFrame()[listOf(2, 0, 1)] }
    }
}
