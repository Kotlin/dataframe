package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.testSets.person.BaseTest
import org.jetbrains.kotlinx.dataframe.testSets.person.age
import org.junit.Test
import kotlin.reflect.typeOf

@Suppress("ktlint:standard:argument-list-wrapping")
class GroupByTests : BaseTest() {

    @Test
    fun `groupBy values with nulls`() {
        val df = dataFrameOf(
            "a", "b",
        )(
            1, 1,
            1, null,
            2, null,
            3, 1,
        )

        df.groupBy("a").values { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c",
            )(
                1, listOf(1, null),
                2, listOf(null),
                3, listOf(1),
            )

        df.groupBy("a").values(dropNA = true) { "b" into "c" } shouldBe
            dataFrameOf(
                "a", "c",
            )(
                1, listOf(1),
                2, emptyList<Int>(),
                3, listOf(1),
            )
    }

    @Test
    fun `aggregate FrameColumns into new column`() {
        val df = dataFrameOf(
            "a", "b", "c",
        )(
            1, 2, 3,
            4, 5, 6,
        )
        val grouped = df.groupBy("a", "b").into("d")

        grouped.groupBy("a").aggregate {
            getColumn("d") into "e"
        }["e"].type() shouldBe typeOf<List<AnyFrame>>()

        grouped.groupBy("a").aggregate {
            getFrameColumn("d") into "e"
        }["e"].type() shouldBe typeOf<List<AnyFrame>>()
    }

    @Test
    fun `groupBy forEachEntry`() {
        val grouped = typed.groupBy { age }
        val entries1 = buildList {
            grouped.forEach { (key, group) ->
                add(key.toMap() to group)
            }
        }
        val entries2 = buildList {
            grouped.forEachEntry {
                add(it.toMap() to it.group())
            }
        }

        entries1 shouldBe entries2
    }

    @Test
    fun `groupBy mapEntries`() {
        // old mapToRows, and mapToFrames stick to the same return type, so let's make the types Any? for the test
        val grouped: GroupBy<Any?, Any?> = df.groupBy { age }
        val entries1 = grouped.map { (key, group) ->
            key.toMap() to group
        }
        val entries2 = grouped.mapEntries {
            it.toMap() to it.group()
        }
        entries1 shouldBe entries2

        val entries3 = grouped.mapToRows { (key, group) ->
            listOf(key.toMap() to group).toDataFrame().single()
        }
        val entries4 = grouped.mapEntriesToRows {
            listOf(it.toMap() to it.group()).toDataFrame().single()
        }
        entries3 shouldBe entries4

        val entries5 = grouped.mapToFrames { (key, group) ->
            listOf(key.toMap() to group).toDataFrame()
        }
        val entries6 = grouped.mapEntriesToFrames {
            listOf(it.toMap() to it.group()).toDataFrame()
        }
        entries5 shouldBe entries6

        // let's test the -Entries variants with typed versions
        val grouped2 = typed.groupBy { age }

        val entries7 = grouped2.mapEntries {
            it.toMap() to it.group()
        }
        val entries8 = grouped2.mapEntriesToRows {
            listOf(it.toMap() to it.group()).toDataFrame().single()
        }.toList()
        val entries9 = grouped2.mapEntriesToFrames {
            listOf(it.toMap() to it.group()).toDataFrame()
        }.map { it[0][0] to it[0][1] }.toList()
        entries7 shouldBe entries8
        entries8 shouldBe entries9
    }

    @Test
    fun `groupBy filterEntries`() {
        val grouped = typed.groupBy { age }

        val entries1 = grouped.filter { age == 20 }
            .mapEntries {
                it.toMap() to it.group()
            }
        val entries2 = grouped.filterEntries { age == 20 }
            .mapEntries {
                it.toMap() to it.group()
            }
        entries1 shouldBe entries2
    }
}
