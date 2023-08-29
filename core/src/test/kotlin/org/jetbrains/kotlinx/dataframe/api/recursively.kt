package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
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

    private val recursivelyGoal = dfGroup.getColumnsWithPaths { dfs { true } }
        .sortedBy { it.name }

    private val recursivelyNoGroups = dfGroup.getColumnsWithPaths { allDfs(false) }
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
    fun children() {
//        dfGroup.getColumnsWithPaths { children().recursively() }.print()
        dfGroup.getColumnsWithPaths { name.children() }.print()
    }

    @Test
    fun groups() {
        listOf(
            df.select { name },
            df.select { colGroups().recursively() },
            df.select { colGroups() },
            df.select { all().colGroups() },
            df.select { all().colGroups().rec() },
        ).shouldAllBeEqual()

        dfGroup.select { colGroups() } shouldBe dfGroup.select { name }
        dfGroup.select { colGroups().rec() } shouldBe dfGroup.select { name and name.firstName }
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

    @Test
    fun `valueCols recursively`() {
        dfGroup.getColumnsWithPaths { valueCols().recursively() }.sortedBy { it.name } shouldBe
            recursivelyNoGroups
    }

    @Test
    fun `colGroups recursively`() {
        dfGroup.getColumnsWithPaths { colGroups().recursively() } shouldBe
            dfGroup.getColumnsWithPaths { name and name.firstName }
    }

    @Test
    fun `frameCols recursively`() {
        val frameCol by frameColumn<Person>()

        val dfWithFrames = df
            .add {
                expr { df } into frameCol
            }
            .convert { name }.to {
                val firstName by it.asColumnGroup().firstName
                val lastName by it.asColumnGroup().lastName

                @Suppress("NAME_SHADOWING")
                val frameCol by it.map { df }.asFrameColumn()

                dataFrameOf(firstName, lastName, frameCol).asColumnGroup("name")
            }

        dfWithFrames.getColumnsWithPaths { frameCols().recursively() } shouldBe
            dfWithFrames.getColumnsWithPaths { name[frameCol] and frameCol }
    }

//    @Test
    fun `cols of kind recursively`() {
        listOf(
            dfGroup.getColumnsWithPaths {
                colsOfKind(Frame, Value) { "e" in it.name }.rec()
            },
            dfGroup.getColumnsWithPaths {
                dfs { "e" in it.name }
            }
        ).map {
            it.sortedBy { it.name }.map { it.name to it.path }
        }.shouldAllBeEqual()
    }
}
