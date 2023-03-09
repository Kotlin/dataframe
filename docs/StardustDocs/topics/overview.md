[//]: # (title: Overview)
<show-structure depth="3"/>

<tip> 

This documentation is written in such a way that it could be read sequentially and in this case, it  provides all necessary information about the library, but at the same time the [Operations](operations.md) section could be used as an API reference

</tip>

## What is Data Frame

Data frame is an abstraction for working with structured data. Essentially it’s a 2-dimensional table with labeled columns of potentially different types. You can think of it like a spreadsheet or SQL table, or a dictionary of series objects.

The handiness of this abstraction is not in the table itself but in a set of operations defined on it. 
The Kotlin Dataframe library is an idiomatic Kotlin DSL defining such operations. 
The process of working with data frame is often called *data wrangling* which 
is the process of transforming and mapping data from one "raw" data form into another format 
that is more appropriate for analytics and visualization. 
The goal of data wrangling is to assure quality and useful data.

## Main Features and Concepts

* [**Hierarchical**](hierarchical.md) — the Kotlin DataFrame library provides an ability to read and present data from different sources including not only plain **CSV** but also **JSON**. That’s why it has been designed hierarchical and allows nesting of columns and cells.

* [**Interoperable**](collectionsInterop.md) — hierarchical data layout also opens a possibility of converting any objects 
structure in application memory to a data frame and vice versa.

* **Safe** — the Kotlin DataFrame library provides a mechanism of on-the-fly [**generation of extension properties**](extensionPropertiesApi.md) 
that correspond to the columns of a data frame. 
In interactive notebooks like Jupyter or Datalore, the generation runs after each cell execution. 
In IntelliJ IDEA there's a Gradle plugin for generation properties based on CSV file or JSON file. 
Also, we’re working on a compiler plugin that infers and transforms [`DataFrame`](DataFrame.md) schema while typing.
The generated properties ensures you’ll never misspell column name and don’t mess up with its type, and of course nullability is also preserved.

* **Generic** — columns can store objects of any type, not only numbers or strings.

* [**Polymorphic**](schemas.md) — if all columns of [`DataFrame`](DataFrame.md) are presented in some other [`DataFrames`](DataFrame.md), then the first one could be a superclass for latter. 
Thus, one can define a function on an interface with some set of columns and then execute it in a safe way on any [`DataFrames`](DataFrame.md) which contains this set of columns.

* **Immutable** — all operations on [`DataFrame`](DataFrame.md) produce new instance, while underlying data is reused wherever it's possible

## Syntax

**Basics:**

```kotlin
val df = DataFrame.read("titanic.csv", delimiter = ';')
```

```kotlin
// filter rows
df.filter { survived && home.endsWith("NY") && age in 10..20 }
    
// add column
df.add("birthYear") { 1912 - age }

// sort rows
df.sortByDesc { age }

// aggregate data
df.groupBy { pclass }.aggregate {
    maxBy { age }.name into "oldest person"
    count { survived } into "survived"
}
```

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

val dfClean = df
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
dfClean.groupBy { From into "origin" }.aggregate {
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
    pivot { To }.sum { recentDelays.intCols() } into "total delays to"
}
```

## Contribute and give feedback

If you find a bug, or have an idea for a new feature, [file an issue](https://github.com/Kotlin/dataframe/issues/new) in our DataFrame GitHub repository.

Additionally, we welcome contributions. To get stared, choose an issue and let us know that you're working on it. When you're ready, create a [pull request](https://github.com/Kotlin/dataframe/pulls).

You can also contact us in the [#datascience](https://kotlinlang.slack.com/archives/C4W52CFEZ) channel of Kotlin Slack.

For more information on how to contribute, see our [Contributing guidelines](https://github.com/Kotlin/dataframe/blob/master/CONTRIBUTING.md).

Good luck!
