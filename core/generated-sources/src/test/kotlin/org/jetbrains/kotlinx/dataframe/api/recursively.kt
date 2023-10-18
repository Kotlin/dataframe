package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

class Recursively : TestBase() {

    fun List<ColumnWithPath<*>>.print() {
        forEach {
            if (it.isValueColumn()) println("${it.name}: ${it.type()}")
            else it.print()
        }
        println()
    }

    infix fun List<ColumnWithPath<*>>.shouldBe(other: List<ColumnWithPath<*>>) {
        this.map { it.name to it.path } shouldBe other.map { it.name to it.path }
    }

    infix fun List<ColumnWithPath<*>>.shouldNotBe(other: List<ColumnWithPath<*>>) {
        this.map { it.name to it.path } shouldNotBe other.map { it.name to it.path }
    }

    private val recursivelyGoal = dfGroup.getColumnsWithPaths { cols { true }.recursively() }
        .sortedBy { it.name }

    private val recursivelyNoGroups = dfGroup.getColumnsWithPaths { cols { !it.isColumnGroup() }.recursively() }
        .sortedBy { it.name }

    private val recursivelyString = dfGroup.getColumnsWithPaths { dfsOf<String?>() }
        .sortedBy { it.name }

    @Test
    fun `first, last, and single`() {
        listOf(
            dfGroup.select { name.firstName.firstName },

            dfGroup.select { first { col -> col.any { it == "Alice" } }.recursively() },
            dfGroup.select { last { col -> col.any { it == "Alice" } }.recursively() },
            dfGroup.select { single { col -> col.any { it == "Alice" } }.recursively() },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { city },

            dfGroup.select { first { col -> col.any { it == "London" } }.recursively() },
            dfGroup.select { last { col -> col.any { it == "London" } }.recursively() },
            dfGroup.select { single { col -> col.any { it == "London" } }.recursively() },
        ).shouldAllBeEqual()
    }

    @Test
    fun `children`() {
        dfGroup.getColumnsWithPaths { children().recursively() }.print()
        dfGroup.getColumnsWithPaths { name.children() }.print()
    }

    @Test
    fun `groups`() {
        listOf(
            df.select { name },
            df.select { groups().recursively() },
            df.select { groups() },
            df.select { all().groups() },
            df.select { all().groups().rec() },
        ).shouldAllBeEqual()

        dfGroup.select { groups() } shouldBe dfGroup.select { name }
        dfGroup.select { groups().rec() } shouldBe dfGroup.select { name and name.firstName }
    }

    @Test
    fun `all recursively`() {
        dfGroup.getColumnsWithPaths { all().recursively() }.sortedBy { it.name } shouldBe recursivelyGoal
        dfGroup.getColumnsWithPaths { all().cols { !it.isColumnGroup() }.rec() }
            .sortedBy { it.name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `cols recursively`() {
        dfGroup.getColumnsWithPaths { cols().recursively() }.sortedBy { it.name } shouldBe recursivelyGoal
    }

    @Test
    fun `colsOf recursively`() {
        dfGroup.getColumnsWithPaths { colsOf<String?>().recursively() }.sortedBy { it.name } shouldBe recursivelyString
    }

    @Test
    fun `all allRecursively`() {
        dfGroup.getColumnsWithPaths { all().all().recursively() }.sortedBy { it.name } shouldBe recursivelyGoal
        dfGroup.getColumnsWithPaths { all().cols { !it.isColumnGroup() }.recursively() }
            .sortedBy { it.name } shouldBe recursivelyNoGroups
    }

    @Test
    fun `cols allRecursively`() {
        dfGroup.getColumnsWithPaths { cols().all().recursively() }.sortedBy { it.name } shouldBe recursivelyGoal
        dfGroup.getColumnsWithPaths { cols().cols { !it.isColumnGroup() }.recursively() }
            .sortedBy { it.name } shouldBe recursivelyNoGroups
    }
}
