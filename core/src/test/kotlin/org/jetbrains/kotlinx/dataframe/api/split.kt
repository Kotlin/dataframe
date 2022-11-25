package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.hasNulls
import org.jetbrains.kotlinx.dataframe.type
import org.junit.Test
import kotlin.reflect.typeOf

class SplitTests {

    @Test
    fun `split with default`() {
        val recentDelays = listOf(listOf(23, 47), listOf(), listOf(24, 43, 87), listOf(13), listOf(67, 32)).toColumn("RecentDelays")
        val df = dataFrameOf(recentDelays)
        val split = df.split(recentDelays).default(0).into { "delay$it" }
        split.columns().forEach {
            it.hasNulls() shouldBe false
        }
        split.values().count { it == 0 } shouldBe 7
    }

    @Test
    fun `split with regex`() {
        val title by columnOf(
            "Toy Story (1995)",
            "Jumanji (1995)",
            "Grumpier Old Men (1995)",
            "Waiting to Exhale (1995)"
        )

        val regex = """(.*) \((\d{4})\)""".toRegex()
        val split = title.toDataFrame()
            .split { title }
            .match(regex)
            .into("title", "year")
            .parse()
        split.schema().print()
        split["title"].hasNulls shouldBe false
        split["year"].type shouldBe typeOf<Int>()
    }

    @Test
    fun `split into columns`() {
        val df = dataFrameOf("a", "b", "c")(
            1, 2, 3,
            1, 4, 5,
            2, 3, 4,
            3, 6, 7
        )
        val res = df.groupBy("a").updateGroups { it.remove("a") }.into("g")
            .update("g").at(1).with { DataFrame.empty() }
            .update("g").at(2).withNull()
            .split { "g"<AnyFrame>() }.intoColumns()
            .ungroup("g")
        res shouldBe dataFrameOf("a", "b", "c")(
            1, listOf(2, 4), listOf(3, 5),
            2, emptyList<Int>(), emptyList<Int>(),
            3, emptyList<Int>(), emptyList<Int>()
        )
    }
}
