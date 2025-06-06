[//]: # (title: Compiler plugin examples)

This page provides a few examples that you can copy directly to your project.
[Schema info](staticInterpretation.md#schema-info) will be a convenient way to observe the result of different operations.

> See also a [Kotlin DataFrame in the IntelliJ IDEA project example](https://github.com/Kotlin/dataframe/blob/master/examples/kotlin-dataframe-plugin-example) 
> — an IntelliJ IDEA project showcasing simple DataFrame expressions using the Compiler Plugin.

### Example 1

```kotlin
import org.jetbrains.kotlinx.dataframe.api.*

fun main() {
    val df = dataFrameOf("location", "income")(
        "mall", "2.49",
        "university", "2.99",
        "university", "1.49",
        "school", "0.99",
        "hospital", "2.99",
        "university", "0.49",
        "hospital", "1.49",
        "mall", "0.99",
        "hospital", "0.49",
    )

    df
        .convert { income }.with { it.toDouble() }
        .groupBy { location }.aggregate {
            income.toList() into "allTransactions"
            sumOf { income } into "totalIncome"
        }.forEach {
            println(location)
            println("totalIncome = $totalIncome")
        }
}
```

### Example 2

```kotlin
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

enum class State {
    Idle, Productive, Maintenance
}

class Event(val toolId: String, val state: State, val timestamp: Long)

fun main() {
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

    val lastTimestamp = events.maxOf { timestamp }

    val groupBy = events
        .groupBy { toolId }
        .sortBy { timestamp }
        .add("stateDuration") {
            (next()?.timestamp ?: lastTimestamp) - timestamp
        }

    groupBy.updateGroups {
        val allStates = State.entries.toDataFrame {
            "state" from { it }
        }

        val df = allStates.leftJoin(it) { state }
            .fillNulls { stateDuration }
            .with { -1 }

        df.groupBy { state }.sumFor { stateDuration }
    }
        .toDataFrame()
        .toStandaloneHtml()
        .openInBrowser()
}
```
