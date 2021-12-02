# Kotlin DataFrame: data manipulation library
[![JetBrains incubator project](https://jb.gg/badges/incubator.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
[![Kotlin](https://img.shields.io/badge/kotlin-1.6.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/org.jetbrains.kotlinx/dataframe?color=blue&label=Maven%20Central)](https://search.maven.org/artifact/org.jetbrains.kotlinx/dataframe)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

Kotlin DataFrame is a library for in-memory data manipulation
* Simple, readable and powerful DSL for data wrangling
* Supports hierarchical data layouts
* Reads CSV and JSON
* Tracks column nullability
* Provides statically typed API via [code generation](https://kotlin.github.io/dataframe/overview.html)
* Integrates with [Kotlin kernel for Jupyter](https://github.com/Kotlin/kotlin-jupyter)

Inspired by [krangl](https://github.com/holgerbrandl/krangl) and [pandas](https://pandas.pydata.org/)

Explore [**documentation**](https://kotlin.github.io/dataframe/overview.html) for details.

## Setup

### Gradle
```groovy
repositories {
    mavenCentral()
}
dependencies {
    implementation 'org.jetbrains.kotlin:dataframe:0.8.0-rc'
}
```
### Jupyter Notebook

Install [Kotlin kernel](https://github.com/Kotlin/kotlin-jupyter) for [Jupyter](https://jupyter.org/)

Import stable `dataframe` version into notebook: 
```
%use dataframe
```
or specific version:
```
%use dataframe(<version>)
```

## Example

**Create:**
```kotlin
// create columns
val fromTo by columnOf("LoNDon_paris", "MAdrid_miLAN", "londON_StockhOlm", "Budapest_PaRis", "Brussels_londOn")
val flightNumber by columnOf(10045.0, Double.NaN, 10065.0, Double.NaN, 10085.0)
val recentDelays by columnOf("23,47", null, "24, 43, 87", "13", "67, 32")
val airline by columnOf("KLM(!)", "{Air France} (12)", "(British Airways. )", "12. Air France", "'Swiss Air'")

// create dataframe
val df = dataFrameOf(fromTo, flightNumber, recentDelays, airline)
```

**Clean:**
```kotlin
// typed accessors for columns
// that will appear during
// dataframe transformation
val origin by column<String>()
val destination by column<String>()

val clean = df
    // fill missing flight numbers
    .fillNA { flightNumber }.with { prev()!!.flightNumber + 10 }

    // convert flight numbers to int
    .convert { flightNumber }.toInt()

    // clean 'Airline' column
    .update { airline }.with { "([a-zA-Z\\s]+)".toRegex().find(it)?.value ?: "" }

    // split 'From_To' column into 'From' and 'To'
    .split { fromTo }.by("_").into(origin, destination)

    // clean 'From' and 'To' columns
    .update { origin and destination }.with { it.lowercase().replaceFirstChar(Char::uppercase) }

    // split lists of delays in 'RecentDelays' into separate columns
    // 'delay1', 'delay2'... and nest them inside original column `RecentDelays`
    .split { recentDelays }.inward { "delay$it" }

    // convert string values in `delay1`, `delay2` into ints
    .parse { recentDelays }
```

**Aggregate:**
```kotlin
// group by flight origin
clean
    // group by flight origin
    .groupBy { origin into "from" }.aggregate {
        // we are in the context of single data group

        // number of flights from origin
        count() into "count"

        // list of flight numbers
        flightNumber into "flight numbers"

        // counts of flights per airline
        airline.valueCounts() into "airlines"

        // max delay across all delays in `delay1` and `delay2`
        recentDelays.maxOrNull { delay1 and delay2 } into "major delay"

        // separate lists of recent delays for `delay1`, `delay2` and `delay3`
        recentDelays.implode(dropNulls = true) into "recent delays"

        // total delay per city of destination
        pivot { destination }.sum { recentDelays.intCols() } into "total delays to"
    }
```

[Try this example in **Datalore**](https://datalore.jetbrains.com/view/notebook/vq5j45KWkYiSQnACA2Ymij)

Explore [**more examples**](examples).
