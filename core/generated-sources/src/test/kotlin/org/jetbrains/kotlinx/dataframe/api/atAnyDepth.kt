package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test
import kotlin.reflect.typeOf

class AtAnyDepth : TestBase() {

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

    private val atAnyDepthGoal =
        dfGroup.getColumnsWithPaths { asSingleColumn().ensureIsColumnGroup().asColumnSet().dfsInternal { true } }
            .sortedBy { it.name }

    private val atAnyDepthNoGroups = dfGroup.getColumnsWithPaths {
        asSingleColumn().ensureIsColumnGroup().asColumnSet().run {
            dfsInternal { !it.isColumnGroup() }
        }
    }
        .sortedBy { it.name }

    private val atAnyDepthString = dfGroup.getColumnsWithPaths {
        asSingleColumn()
            .ensureIsColumnGroup()
            .asColumnSet()
            .dfsInternal { it.isSubtypeOf(typeOf<String?>()) }
    }
        .sortedBy { it.name }

    @Test
    fun `first, last, and single`() {
        listOf(
            dfGroup.select { name.firstName.firstName },

            dfGroup.select { first { col -> col.any { it == "Alice" } }.atAnyDepth() },
            dfGroup.select { last { col -> col.any { it == "Alice" } }.atAnyDepth() },
            dfGroup.select { single { col -> col.any { it == "Alice" } }.atAnyDepth() },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { city },

            dfGroup.select { first { col -> col.any { it == "London" } }.atAnyDepth() },
            dfGroup.select { last { col -> col.any { it == "London" } }.atAnyDepth() },
            dfGroup.select { single { col -> col.any { it == "London" } }.atAnyDepth() },
        ).shouldAllBeEqual()
    }

    @Test
    fun children() {
//        dfGroup.getColumnsWithPaths { children().atAnyDepth() }.print()
        dfGroup.getColumnsWithPaths { name.children() }.print()
    }

    @Test
    fun groups() {
        listOf(
            df.select { name },
            df.select { colGroups().atAnyDepth() },
            df.select { colGroups() },
            df.select { all().colGroups() },
            df.select { all().colGroups().atAnyDepth() },
        ).shouldAllBeEqual()

        dfGroup.select { colGroups() } shouldBe dfGroup.select { name }
        dfGroup.select { colGroups().atAnyDepth() } shouldBe dfGroup.select { name and name.firstName }
    }

    @Test
    fun `all atAnyDepth`() {
        dfGroup.getColumnsWithPaths { all().atAnyDepth() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup.getColumnsWithPaths { all().cols { !it.isColumnGroup() }.atAnyDepth() }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `cols atAnyDepth`() {
        dfGroup.getColumnsWithPaths { cols().atAnyDepth() }.sortedBy { it.name } shouldBe atAnyDepthGoal
    }

    @Test
    fun `colsOf atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsOf<String?>().atAnyDepth() }.sortedBy { it.name } shouldBe atAnyDepthString
    }

    @Test
    fun `all allAtAnyDepth`() {
        dfGroup.getColumnsWithPaths { all().all().atAnyDepth() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup.getColumnsWithPaths { all().cols { !it.isColumnGroup() }.atAnyDepth() }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `cols allAtAnyDepth`() {
        dfGroup.getColumnsWithPaths { cols().all().atAnyDepth() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup.getColumnsWithPaths { cols().cols { !it.isColumnGroup() }.atAnyDepth() }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `valueCols atAnyDepth`() {
        dfGroup.getColumnsWithPaths { valueCols().atAnyDepth() }.sortedBy { it.name } shouldBe
            atAnyDepthNoGroups
    }

    @Test
    fun `colGroups atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colGroups().atAnyDepth() } shouldBe
            dfGroup.getColumnsWithPaths { name and name.firstName }
    }

    @Test
    fun `frameCols atAnyDepth`() {
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

        dfWithFrames.getColumnsWithPaths { frameCols().atAnyDepth() } shouldBe
            dfWithFrames.getColumnsWithPaths { name[frameCol] and frameCol }
    }

    //    @Test
    fun `cols of kind atAnyDepth`() {
        listOf(
            dfGroup.getColumnsWithPaths {
                colsOfKind(Frame, Value) { "e" in it.name }.atAnyDepth()
            },
            dfGroup.getColumnsWithPaths {
                asSingleColumn().ensureIsColumnGroup().asColumnSet().dfsInternal { "e" in it.name }
            }
        ).map {
            it.sortedBy { it.name }.map { it.name to it.path }
        }.shouldAllBeEqual()
    }
}
