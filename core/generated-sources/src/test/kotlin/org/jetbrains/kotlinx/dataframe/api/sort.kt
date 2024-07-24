package org.jetbrains.kotlinx.dataframe.api

import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.io.readDataFrame
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.testResource
import org.jetbrains.kotlinx.dataframe.testSets.*
import org.jetbrains.kotlinx.dataframe.testSets.DsSalaries
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

        col.sortWith { df1, df2 -> df1[a] - df2[a] } shouldBe sortedCol
        col.sortWith(compareBy { it[a] }) shouldBe sortedCol
    }

    @Test
    fun `sort by nested column`() {
        val df = testResource("ds_salaries.csv").readDataFrame().cast<DsSalaries>()
        val aggregate = df.pivot(false) { companySize }.groupBy { companyLocation }.aggregate {
            maxOf { salaryInUsd } into "salary"
            maxBy { salaryInUsd } into "extra"
        }
        aggregate.sortBy(pathOf("L", "salary"))[0][pathOf("L", "salary")] shouldBe null
        aggregate.sortByDesc(pathOf("L", "salary"))[0][pathOf("L", "salary")] shouldBe 600_000
    }

    @Test
    fun `sort by invalid nested column`() {
        val df = testResource("ds_salaries.csv").readDataFrame().cast<DsSalaries>()
        val aggregate = df.pivot(false) { companySize }.groupBy { companyLocation }.aggregate {
            maxOf { salaryInUsd } into "salary"
            maxBy { salaryInUsd } into "extra"
        }
        shouldThrowMessage("Can not use ColumnGroup as sort column") {
            aggregate.sortBy(pathOf("L", "extra"))
        }
    }
}
