package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.column
import org.jetbrains.kotlinx.dataframe.columnOf
import org.jetbrains.kotlinx.dataframe.dataFrameOf
import org.junit.Test

class SplitTests {

    @Test
    fun `split with default`() {
        val recentDelays = listOf(listOf(23, 47), listOf(), listOf(24, 43, 87), listOf(13), listOf(67, 32)).toColumn("RecentDelays")
        val df = dataFrameOf(recentDelays)
        val splitted = df.split(recentDelays).default(0).into { "delay$it" }
        splitted.columns().forEach {
            it.hasNulls() shouldBe false
        }
        splitted.values().count { it == 0 } shouldBe 7
        splitted.print()
    }

    @Test
    fun `split with regex`(){
        val title by columnOf(
            "Toy Story (1995)",
            "Jumanji (1995)",
            "Grumpier Old Men (1995)",
            "Waiting to Exhale (1995)"
        )
        val year by column<Int>()

        val regex = """(\s*) \((d{4})\)""".toRegex()
        title.toDataFrame()
            .split { title }
            .match(regex)
            .into("title", "year")
            .print()
    }
}
