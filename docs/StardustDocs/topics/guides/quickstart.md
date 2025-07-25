# Quickstart Guide

<web-summary>
Get started with Kotlin DataFrame in a few simple steps: load data, transform it, and visualize it — all in an interactive Kotlin Notebook.
</web-summary>

<card-summary>
Get started with Kotlin DataFrame right away — integrate it seamlessly and load process, analyze and visualize some data!
</card-summary>

<link-summary>
Learn the basics of Kotlin DataFrame: reading data, applying transformations, and building plots — with full interactivity in Kotlin Notebook.
</link-summary>

This guide shows how to quickly get started with **Kotlin DataFrame**:  
you'll learn how to load data, perform basic transformations, and build a simple plot using Kandy.

We recommend [starting with **Kotlin Notebook**](gettingStartedKotlinNotebook.md) for the best beginner experience —
everything works out of the box, including interactivity and rich DataFrame and plots rendering.  

You can instantly see the results of each operation: view the contents of your DataFrames after every transformation,
inspect individual rows and columns, and explore data step-by-step in a live and interactive way.

You can view this guide as a
[notebook on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/notebooks/quickstart/quickstart.ipynb)
or download <resource src="quickstart.ipynb"></resource>.


<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.guides.QuickStartGuide-->

To start working with Kotlin DataFrame in a notebook, run the cell with the next code:

```kotlin
%useLatestDescriptors
%use dataframe
```

This will load all necessary DataFrame dependencies (of the latest stable version) and all imports, as well as DataFrame
rendering. Learn more [here](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe).

## Read DataFrame

Kotlin DataFrame supports all popular data formats, including CSV, JSON, and Excel, as well as reading from various
databases. Read a CSV with the "Jetbrains Repositories" dataset into `df` variable:

<!---FUN notebook_test_quickstart_2-->

```kotlin
val df = DataFrame.readCsv(
    "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
)
```

<!---END-->

## Display And Explore

To display your dataframe as a cell output, place it in the last line of the cell:

<!---FUN notebook_test_quickstart_3-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_3.html" width="705px" height="500px"></inline-frame>

Kotlin Notebook has special interactive outputs for `DataFrame`. Learn more about them here.

Use `.describe()` method to get dataset summaries — column types, number of nulls, and simple statistics.

<!---FUN notebook_test_quickstart_4-->

```kotlin
df.describe()
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_4.html" width="705px" height="500px"></inline-frame>

## Select Columns

Kotlin DataFrame features a typesafe Columns Selection DSL, enabling flexible and safe selection of any combination of
columns.
Column selectors are widely used across operations — one of the simplest examples is `.select { }`, which returns a new
DataFrame with only the columns chosen in Columns Selection expression.

*After executing the cell* where a `DataFrame` variable is declared, 
[extension properties](extensionPropertiesApi.md) for its columns are automatically generated.
These properties can then be used in the Columns Selection DSL expression for typesafe and convenient column access.

Select some columns:

<!---FUN notebook_test_quickstart_5-->

```kotlin
// Select "full_name", "stargazers_count" and "topics" columns
val dfSelected = df.select { full_name and stargazers_count and topics }
dfSelected
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_5.html" width="705px" height="500px"></inline-frame>

> With a [Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) enabled,
> you can use auto-generated properties in your IntelliJ IDEA projects.

## Row Filtering

Some operations use the [DataRow API](DataRow.md), with expressions and conditions 
that apply for all `DataFrame` rows.
For example, `.filter { }` that returns a new `DataFrame` with rows that satisfy a condition given by row expression.

Inside a row expression, you can access the values of the current row by column names through auto-generated properties.
Similar to the [Columns Selection DSL](ColumnSelectors.md),
but in this case the properties represent actual values, not column references.

Filter rows by "stargazers_count" value:

<!---FUN notebook_test_quickstart_6-->

```kotlin
// Keep only rows where "stargazers_count" value is more than 1000
val dfFiltered = dfSelected.filter { stargazers_count >= 1000 }
dfFiltered
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_6.html" width="705px" height="500px"></inline-frame>

## Columns Rename

Columns can be renamed using the `.rename { }` operation, which also uses the Columns Selection DSL to select a column
to rename.
The `rename` operation does not perform the renaming immediately; instead, it creates an intermediate object that must
be finalized into a new `DataFrame` by calling the `.into()` function with the new column name.

Rename "full_name" and "stargazers_count" columns:

<!---FUN notebook_test_quickstart_7-->

```kotlin
// Rename "full_name" column into "name"
val dfRenamed = dfFiltered.rename { full_name }.into("name")
    // And "stargazers_count" into "starsCount"
    .rename { stargazers_count }.into("starsCount")
dfRenamed
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_7.html" width="705px" height="500px"></inline-frame>

## Modify Columns

Columns can be modified using the `update { }` and `convert { }` operations.
Both operations select columns to modify via the Columns Selection DSL and, similar to `rename`, create an intermediate
object that must be finalized to produce a new `DataFrame`.

The `update` operation preserves the original column types, while `convert` allows changing the type.
In both cases, column names and their positions remain unchanged.

Update "name" and convert "topics":

<!---FUN notebook_test_quickstart_8-->

```kotlin
val dfUpdated = dfRenamed
    // Update "name" values with only its second part (after '/')
    .update { name }.with { it.split("/")[1] }
    // Convert "topics" `String` values into `List<String>` by splitting:
    .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") }
dfUpdated
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_8.html" width="705px" height="500px"></inline-frame>

Check the new "topics" type out:

<!---FUN notebook_test_quickstart_9-->

```kotlin
dfUpdated.topics.type()
```

<!---END-->

Output:

```
kotlin.collections.List<kotlin.String>
```

## Adding New Columns

The `.add { }` function allows creating a `DataFrame` with a new column, where the value for each row is computed based
on the existing values in that row. These values can be accessed within the row expressions.

Add a new `Boolean` column "isIntellij":

<!---FUN notebook_test_quickstart_10-->

```kotlin
// Add a `Boolean` column indicating whether the `name` contains the "intellij" substring
// or the topics include "intellij".
val dfWithIsIntellij = dfUpdated.add("isIntellij") {
    name.contains("intellij") || "intellij" in topics
}
dfWithIsIntellij
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_10.html" width="705px" height="500px"></inline-frame>

## Grouping And Aggregating

A `DataFrame` can be grouped by column keys, meaning its rows are split into groups based on the values in the key
columns.
The `.groupBy { }` operation selects columns and groups the `DataFrame` by their values, using them as grouping keys.

The result is a `GroupBy` — a `DataFrame`-like structure that associates each key with the corresponding subset of the
original `DataFrame`.

Group `dfWithIsIntellij` by "isIntellij":

<!---FUN notebook_test_quickstart_11-->

```kotlin
val groupedByIsIntellij = dfWithIsIntellij.groupBy { isIntellij }
groupedByIsIntellij
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_11.html" width="705px" height="500px"></inline-frame>

A `GroupBy` can be aggregated — that is, you can compute one or several summary statistics for each group.
The result of the aggregation is a `DataFrame` containing the key columns along with new columns holding the computed
statistics for a corresponding group.

For example, `count()` computes size of group:

<!---FUN notebook_test_quickstart_12-->

```kotlin
groupedByIsIntellij.count()
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_12.html" width="705px" height="500px"></inline-frame>

Compute several statistics with `.aggregate { }` that provides an expression for aggregating:

<!---FUN notebook_test_quickstart_13-->

```kotlin
groupedByIsIntellij.aggregate {
    // Compute sum and max of "starsCount" within each group into "sumStars" and "maxStars" columns
    sumOf { starsCount } into "sumStars"
    maxOf { starsCount } into "maxStars"
}
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_13.html" width="705px" height="500px"></inline-frame>

## Sorting Rows

`.sort {}`/`.sortByDesc` sortes rows by value in selected columns, returning a DataFrame with sorted rows. `take(n)`
returns a new `DataFrame` with the first `n` rows.

Combine them to get Top-10 repositories by number of stars:

<!---FUN notebook_test_quickstart_14-->

```kotlin
val dfTop10 = dfWithIsIntellij
    // Sort by "starsCount" value descending
    .sortByDesc { starsCount }.take(10)
dfTop10
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_14.html" width="705px" height="500px"></inline-frame>

## Plotting With Kandy

Kandy is a Kotlin plotting library designed to bring Kotlin DataFrame features into chart creation, providing a
convenient and typesafe way to build data visualizations.

Kandy can be loaded into notebook using `%use kandy`:

```kotlin
%use kandy
```

Build a simple bar chart with `.plot { }` extension for DataFrame, that allows to use extension properties inside Kandy
plotting DSL (plot will be rendered as an output after cell execution):

<!---FUN notebook_test_quickstart_16-->

```kotlin
dfTop10.plot {
    bars {
        x(name)
        y(starsCount)
    }

    layout.title = "Top 10 JetBrains repositories by stars count"
}
```

<!---END-->

![notebook_test_quickstart_16](notebook_test_quickstart_16.svg)

## Write DataFrame

A `DataFrame` supports writing to all formats that it is capable of reading.

Write into Excel:

<!---FUN notebook_test_quickstart_17-->

```kotlin
dfWithIsIntellij.writeExcel("jb_repos.xlsx")
```

<!---END-->

## What's Next?

In this quickstart, we covered the basics — reading data, transforming it, and building a simple visualization.  
Ready to go deeper? Check out what’s next:

- 📘 **[Explore in-depth guides and various examples](Guides-And-Examples.md)** with different datasets,
  API usage examples, and practical scenarios that help you understand the main features of Kotlin DataFrame.

- 🛠️ **[Browse the operations overview](operations.md)** to learn what Kotlin DataFrame can do.

- 🧠 **Understand the design** and core concepts in the [library overview](concepts.md).

- 🔤 **[Learn more about Extension Properties](extensionPropertiesApi.md)**  
  and make working with your data both convenient and type-safe.

- 💡 **[Use Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md)**  
  for auto-generated column access in your IntelliJ IDEA projects.

- 📊 **Master Kandy** for stunning and expressive DataFrame visualizations learning
  [Kandy Documentation](https://kotlin.github.io/kandy).
