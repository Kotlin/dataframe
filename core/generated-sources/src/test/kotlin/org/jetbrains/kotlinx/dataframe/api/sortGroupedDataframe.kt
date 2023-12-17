package org.jetbrains.kotlinx.dataframe.api

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.alsoDebug
import org.jetbrains.kotlinx.dataframe.io.read
import org.junit.Test

class SortGroupedDataframeTests {

    @Test
    fun `Sorted grouped iris dataset`() {
        val irisData = DataFrame.read("src/test/resources/irisDataset.csv")
        irisData.alsoDebug()

        irisData.groupBy("variety").let {
            it.sortBy("petal.length").toString() shouldBe
                it.sortBy { it["petal.length"] }.toString()
        }
    }

    enum class State {
        Idle, Productive, Maintenance
    }

    @Test
    fun test4() {
        class Event(val toolId: String, val state: State, val timestamp: Long)

        val tool1 = "tool_1"
        val tool2 = "tool_2"
        val tool3 = "tool_3"

        val events = listOf(
            Event(tool1, State.Idle, 0),
            Event(tool1, State.Productive, 5),
            Event(tool2, State.Idle, 0),
            Event(tool2, State.Maintenance, 10),
            Event(tool2, State.Idle, 20),
            Event(tool3, State.Idle, 0),
            Event(tool3, State.Productive, 25),
        ).toDataFrame()

        val lastTimestamp = events.maxOf { getValue<Long>("timestamp") }
        val groupBy = events
            .groupBy("toolId")
            .sortBy("timestamp")
            .add("stateDuration") {
                (next()?.getValue("timestamp") ?: lastTimestamp) - getValue<Long>("timestamp")
            }

        groupBy.toDataFrame().alsoDebug()
        groupBy.schema().print()
        groupBy.keys.print()
        groupBy.keys[0].print()

        val df1 = groupBy.updateGroups {
            val missingValues = State.values().asList().toDataFrame {
                "state" from { it }
            }

            val df = it
                .fullJoin(missingValues, "state")
                .fillNulls("stateDuration")
                .with { 100L }

            df.groupBy("state").sumFor("stateDuration")
        }

        df1.toDataFrame().alsoDebug().isNotEmpty() shouldBe true
    }
}
