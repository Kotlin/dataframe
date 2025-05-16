package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Frame
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Group
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.atAnyDepthImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.tree.flattenRecursively
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.age
import org.jetbrains.kotlinx.dataframe.samples.api.city
import org.jetbrains.kotlinx.dataframe.samples.api.firstName
import org.jetbrains.kotlinx.dataframe.samples.api.lastName
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.jetbrains.kotlinx.dataframe.samples.api.weight
import org.junit.Test
import kotlin.reflect.typeOf

class AtAnyDepth : TestBase() {

    // old function copied over to avoid breaking changes
    private fun ColumnSet<*>.dfsInternal(predicate: (ColumnWithPath<*>) -> Boolean): TransformableColumnSet<Any?> =
        transform {
            it.filter { it.isColumnGroup() }
                .flatMap { it.cols().flattenRecursively().filter(predicate) }
        }

    private val atAnyDepthGoal =
        dfGroup.getColumnsWithPaths {
            asSingleColumn().ensureIsColumnGroup().asColumnSet().dfsInternal { true }
        }.sortedBy { it.name }

    private val atAnyDepthNoGroups =
        dfGroup.getColumnsWithPaths {
            asSingleColumn().ensureIsColumnGroup().asColumnSet().dfsInternal { !it.isColumnGroup() }
        }.sortedBy { it.name }

    private val atAnyDepthString =
        dfGroup.getColumnsWithPaths {
            asSingleColumn()
                .ensureIsColumnGroup()
                .asColumnSet()
                .dfsInternal { it.isSubtypeOf(typeOf<String?>()) }
        }.sortedBy { it.name }

    @Test
    fun `first, last, and single`() {
        listOf(
            dfGroup.select { name.firstName.firstName },
            dfGroup.select { first { col -> col.any { it == "Alice" } }.atAnyDepthImpl() },
            dfGroup.select { colsAtAnyDepth().first { col -> col.any { it == "Alice" } } },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "Alice" } }.first() },
            dfGroup.select { colsAtAnyDepth().last { col -> col.any { it == "Alice" } } },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "Alice" } }.last() },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "Alice" } }.single() },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "Alice" } }.single() },
        ).shouldAllBeEqual()

        listOf(
            dfGroup.select { city },
            dfGroup.select { colsAtAnyDepth().first { col -> col.any { it == "London" } } },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "London" } }.first() },
            dfGroup.select { colsAtAnyDepth().last { col -> col.any { it == "London" } } },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "London" } }.last() },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "London" } }.single() },
            dfGroup.select { colsAtAnyDepth().filter { col -> col.any { it == "London" } }.single() },
        ).shouldAllBeEqual()
    }

    @Test
    fun groups() {
        listOf(
            df.select { name },
            df.select { colsAtAnyDepth().colGroups() },
            df.select { colsAtAnyDepth().filter { it.kind == Group } },
            df.select { colGroups() },
            df.select { all().colGroups() },
            df.select { all().colsAtAnyDepth().colGroups() },
        ).shouldAllBeEqual()

        dfGroup.select { colGroups() } shouldBe dfGroup.select { name }
        dfGroup.select { colsAtAnyDepth().colGroups() } shouldBe dfGroup.select { name and name.firstName }
    }

    @Test
    fun `all atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsAtAnyDepth().all() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup
            .getColumnsWithPaths { all().colsAtAnyDepth().cols { !it.isColumnGroup() } }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `cols atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsAtAnyDepth().cols() }.sortedBy { it.name } shouldBe atAnyDepthGoal
    }

    @Test
    fun `colsOf atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsAtAnyDepth().colsOf<String?>() }.sortedBy { it.name } shouldBe
            atAnyDepthString
    }

    @Test
    fun `all allAtAnyDepth`() {
        dfGroup.getColumnsWithPaths { all().colsAtAnyDepth().all() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup
            .getColumnsWithPaths { all().colsAtAnyDepth().filter { !it.isColumnGroup() } }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `cols allAtAnyDepth`() {
        dfGroup.getColumnsWithPaths { cols().colsAtAnyDepth().all() }.sortedBy { it.name } shouldBe atAnyDepthGoal
        dfGroup
            .getColumnsWithPaths { cols().colsAtAnyDepth().filter { !it.isColumnGroup() } }
            .sortedBy { it.name } shouldBe atAnyDepthNoGroups
    }

    @Test
    fun `valueCols atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsAtAnyDepth().valueCols() }.sortedBy { it.name } shouldBe
            atAnyDepthNoGroups
    }

    @Test
    fun `colGroups atAnyDepth`() {
        dfGroup.getColumnsWithPaths { colsAtAnyDepth().colGroups() } shouldBe
            dfGroup.getColumnsWithPaths { name and name.firstName }
    }

    @Test
    fun `frameCols atAnyDepth`() {
        val frameCol by frameColumn<Person>()

        val dfWithFrames = df
            .add { expr { df } into frameCol }
            .convert { name }.asColumn {
                val firstName by it.asColumnGroup().firstName
                val lastName by it.asColumnGroup().lastName

                @Suppress("NAME_SHADOWING")
                val frameCol by it.map { df }.asFrameColumn()

                dataFrameOf(firstName, lastName, frameCol).asColumnGroup("name")
            }

        dfWithFrames.getColumnsWithPaths { colsAtAnyDepth().frameCols() } shouldBe
            dfWithFrames.getColumnsWithPaths { name[frameCol] and frameCol }
    }

    @Test
    fun `cols of kind atAnyDepth`() {
        listOf(
            dfGroup.getColumnsWithPaths {
                colsAtAnyDepth().colsOfKind(Frame, Value) { "e" in it.name }
            },
            dfGroup.getColumnsWithPaths {
                name { firstName.allCols() and lastName } and cols(age, weight)
            },
        ).map { it.map { it.path.joinToString() } }.shouldAllBeEqual()
    }
}
