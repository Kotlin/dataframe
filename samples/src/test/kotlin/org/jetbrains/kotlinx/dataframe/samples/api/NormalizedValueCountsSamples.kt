package org.jetbrains.kotlinx.dataframe.samples.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.concatWithKeys
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.groupBy
import org.jetbrains.kotlinx.dataframe.api.valueCounts
import org.junit.Test

class NormalizedValueCountsSamples {

    @Test
    fun valueCountsProportions() {
        // SampleStart
        val observations = dataFrameOf("value")("a", "a", null, "b")
        val proportions = observations["value"].valueCounts(normalize = true)
        // SampleEnd
        proportions["count"].values() shouldBe listOf(2.0 / 3, 1.0 / 3)
    }

    @Test
    fun valueCountsProportionsIncludingNA() {
        // SampleStart
        val observations = dataFrameOf("value")("a", "a", null, "b")
        val proportions = observations.valueCounts("value", dropNA = false, normalize = true)
        // SampleEnd
        proportions["count"].values() shouldBe listOf(0.5, 0.25, 0.25)
    }

    @Test
    fun valueCountsProportionsPerGroup() {
        // SampleStart
        val observations = dataFrameOf(
            "group" to listOf("A", "A", "B", "B", "B"),
            "value" to listOf("x", "y", "x", "x", "y"),
        )
        val proportions = observations.groupBy("group")
            .updateGroups { valueCounts("value", normalize = true) }
            .concatWithKeys()
        // SampleEnd
        proportions["group"].values() shouldBe listOf("A", "A", "B", "B")
        proportions["count"].values() shouldBe listOf(0.5, 0.5, 2.0 / 3, 1.0 / 3)
    }
}
