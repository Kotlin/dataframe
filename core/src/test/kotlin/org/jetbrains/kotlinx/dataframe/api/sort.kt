package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.testResource
import org.jetbrains.kotlinx.dataframe.testSets.DsSalaries
import org.jetbrains.kotlinx.dataframe.testSets.companyLocation
import org.jetbrains.kotlinx.dataframe.testSets.companySize
import org.jetbrains.kotlinx.dataframe.testSets.salaryInUsd
import org.junit.Test

class SortDataColumn {

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

    @Test
    fun `group by sort validates nested paths`() {
        val df = DataFrame.readCsv(testResource("ds_salaries.csv")).cast<DsSalaries>()
        val grouped = df.group { salaryInUsd }.into("group").groupBy { companyLocation }
        val salaryPath = pathOf("group", "group", "salary_in_usd")

        grouped.sortBy { salaryPath }.groups.values().forEach {
            val salaries = it[pathOf("group", "salary_in_usd")].values().map { value -> value as Int }
            salaries shouldBe salaries.sorted()
        }
        grouped.sortByDesc { salaryPath }.groups.values().forEach {
            val salaries = it[pathOf("group", "salary_in_usd")].values().map { value -> value as Int }
            salaries shouldBe salaries.sortedDescending()
        }

        val invalidPath = pathOf("group", "salaryInUsd")
        val ascendingError = shouldThrow<IllegalStateException> {
            grouped.sortBy { invalidPath }
        }.message
        val descendingError = shouldThrow<IllegalStateException> {
            grouped.sortByDesc { invalidPath }
        }.message

        ascendingError shouldBe descendingError
        ascendingError.orEmpty() shouldContain "group/salaryInUsd"
        ascendingError.orEmpty() shouldNotContain "Can not apply sort flag to column kind"

        shouldThrowMessage(ascendingError.orEmpty()) {
            grouped.sortBy { salaryPath and invalidPath }
        }
    }

    @Test
    fun `group by sort preserves group and key sorting`() {
        val df = DataFrame.readCsv(testResource("ds_salaries.csv")).cast<DsSalaries>()

        df.groupBy { companyLocation }.sortBy { salaryInUsd }.groups.values().forEach {
            val salaries = it[salaryInUsd].values()
            salaries shouldBe salaries.sorted()
        }

        val grouped = df.group { salaryInUsd }.into("group").groupBy { companyLocation }
        val sortedKeys = grouped.sortBy { companyLocation }.keys[companyLocation].values()
        sortedKeys shouldBe sortedKeys.sorted()
    }

    @Test
    fun `group by sort accepts columns present in later heterogeneous groups`() {
        val grouped = dataFrameOf("key", "groups")(
            "first",
            dataFrameOf("other")(2),
            "second",
            dataFrameOf("value")(2, 1),
        ).asGroupBy("groups")

        grouped.sortBy("value").groups.values().toList()[1]["value"].values() shouldBe listOf(1, 2)
    }

    @Test
    fun `group by sort validates paths when no groups are present`() {
        val emptySource = dataFrameOf("value") { emptyList<Int>() }.groupBy("value")
        val filteredGroups = dataFrameOf("value")(1).groupBy("value").filter { false }
        val invalidPath = pathOf("missing", "nested")

        shouldThrow<IllegalStateException> {
            emptySource.sortBy { invalidPath }
        }.message.orEmpty() shouldContain "missing/nested"
        shouldThrow<IllegalStateException> {
            filteredGroups.sortBy { invalidPath }
        }.message.orEmpty() shouldContain "missing/nested"
    }
}
