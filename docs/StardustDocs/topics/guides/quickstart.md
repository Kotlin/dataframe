# Quickstart Guide

<web-summary>
Get started with Kotlin DataFrame in a few simple steps: load data, transform it, and visualize it — all in an interactive Kotlin Notebook.
</web-summary>

<card-summary>
Get started with Kotlin DataFrame right away — 
effortlessly integrate it into your project and load, process, analyze, and visualize data!
</card-summary>

<link-summary>
Learn the basics of Kotlin DataFrame: reading data, applying transformations, and building plots — with full interactivity in Kotlin Notebook.
</link-summary>

This guide shows how to quickly get started with **Kotlin DataFrame**:  
you'll learn how to integrate it into your [Gradle](SetupGradle.md) (or [Maven](SetupMaven.md)) project,
load data, perform basic transformations, and build a simple plot using Kandy.

You can also view the similar guide for [Kotlin Notebook](SetupKotlinNotebook.md) and 
[Jupyter notebooks with Kotlin kernel](SetupJupyter.md) as a
[notebook on GitHub](https://github.com/Kotlin/dataframe/tree/master/examples/notebooks/quickstart/quickstart.ipynb)
or download 
<resource src="quickstart.ipynb"></resource>.


<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.guides.QuickStartGuide-->

## Set up Kotlin DataFrame in your project

> See **[](SetupGradle.md)** and **[](SetupMaven.md)** for detailed setup instructions.

Add the Kotlin DataFrame library [general artifact](Modules.md#dataframe-general) dependency:

<tabs>
<tab title="Gradle (Kotlin DSL)">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:dataframe:%dataFrameVersion%")
}
```

</tab>

<tab title="Gradle (Groovy DSL)">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:dataframe:%dataFrameVersion%'
}
```

</tab>

<tab title="Maven">

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>dataframe</artifactId>
    <version>%dataFrameVersion%</version>
</dependency>
```

</tab>
</tabs>

This will add the [Kotlin DataFrame core API and implementation](Modules.md#dataframe-core) as well as all
[IO modules](Modules.md#io-modules) (excluding [experimental ones](Modules.md#experimental-modules)).  
For flexible dependencies configuration see [Custom configuration](SetupCustomGradle.md).

### Set up Kotlin DataFrame Compiler Plugin

The [Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md) generates 
[extension properties](extensionPropertiesApi.md) for dataframe columns,
allowing name- and type-safe access.

Add it to your project in the "plugins" section:

<tabs>
<tab title="Gradle(Kotlin DSL)">

```kotlin
plugins {
    kotlin("plugin.dataframe") version "%compilerPluginKotlinVersion%"
}
```

</tab>

<tab title="Gradle(Groovy DSL)">

```groovy
plugins {
    id 'org.jetbrains.kotlin.plugin.dataframe' version '%compilerPluginKotlinVersion%'
}
```

</tab>

<tab title="Maven">

```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>%compilerPluginKotlinVersion%</version>

    <configuration>
        <compilerPlugins>
            <plugin>kotlin-dataframe</plugin>
        </compilerPlugins>
    </configuration>

    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-dataframe</artifactId>
            <version>%compilerPluginKotlinVersion%</version>
        </dependency>
    </dependencies>
</plugin>
```

</tab>
</tabs>

## Read dataframe from sources

Kotlin DataFrame supports all popular data formats, including 
[CSV](read.md#read-from-csv), [JSON](read.md#read-from-json), 
[Excel](read.md#read-from-excel), [Apache Parquet](Parquet.md),
and [Apache Arrow](read.md#read-apache-arrow-formats),
as well as [reading from various databases](readSqlDatabases.md). 

Read a CSV with the "Jetbrains Repositories" dataset into the `df` variable
using [`DataFrame.readCsv()`](read.md#read-from-csv) method:

<!---FUN notebook_test_quickstart_2-->

```kotlin
// Read a csv file from the given URL string
val df = DataFrame.readCsv(
    "https://raw.githubusercontent.com/Kotlin/dataframe/master/data/jetbrains_repositories.csv",
)
```

<!---END-->

You can also download this file:
<resource src="jetbrains_repositories.csv"></resource>
and read it locally.

Variable `df` has the type [`DataFrame`](DataFrame.md).

## Display And Explore

To print your dataframe into the stdout (console), you can use the `.print()` extension method:

<!---FUN dfPrint-->

```kotlin
df.print()
```

<!---END-->

Output:

```text
  ㅤ                        
                                 full_name                                 html_url stargazers_count                                   topics watchers
  0                          JetBrains/JPS         https://github.com/JetBrains/JPS               23                                       []       23
  1                JetBrains/YouTrackSharp https://github.com/JetBrains/YouTrack...              115 [jetbrains, jetbrains-youtrack, youtr...      115
  2              JetBrains/colorSchemeTool https://github.com/JetBrains/colorSch...              290                                       []      290
  3                      JetBrains/ideavim     https://github.com/JetBrains/ideavim             6120 [ideavim, intellij, intellij-platform...     6120
  4           JetBrains/youtrack-vcs-hooks https://github.com/JetBrains/youtrack...                5                                       []        5
  5   JetBrains/youtrack-rest-ruby-library https://github.com/JetBrains/youtrack...                8                                       []        8
  6                     JetBrains/emacs4ij    https://github.com/JetBrains/emacs4ij               47                                       []       47
  7          JetBrains/codereview4intellij https://github.com/JetBrains/coderevi...               11                                       []       11
  8       JetBrains/teamcity-nuget-support https://github.com/JetBrains/teamcity...               41 [nuget, nuget-feed, teamcity, teamcit...       41
  9                  JetBrains/Grammar-Kit https://github.com/JetBrains/Grammar-Kit              534                                       []      534
 10     JetBrains/intellij-starteam-plugin https://github.com/JetBrains/intellij...                6                                       []        6
...

```

### Interactive web outputs

It’s much easier to explore your data as an interactive web table! Use [`.toHtml()`](toHTML.md) 
and then write it to a file using `.writeHtml()`. 

Our guides and examples actually use these web tables!

<!---FUN dfToHtml-->

```kotlin
df.toHtml().writeHtml("df.html")
```

<!---END-->

<inline-frame src="./resources/dfToHtml.html" width="705px" height="500px"></inline-frame>

Alternatively, you can open it directly in your browser without saving it to a file
using the `openInBrowser()` method.

<!---FUN dfToHtmlOpenInBrowser-->

```kotlin
df.toHtml().openInBrowser()
```

<!---END-->

### Describe

Use the [`.describe()`](describe.md) method to get dataframe summaries — column types, number of nulls, and simple statistics.
The result of `describe()` is also of the type [`DataFrame`](DataFrame.md), so you can use `.print()` on it or
save it as a web table:

<!---FUN dfDescribe-->

```kotlin
df.describe()
```

<!---END-->

<inline-frame src="./resources/dfDescribe.html" width="705px" height="500px"></inline-frame>

## Provide Data Schema

A [*schema*](schemas.md) describes the structure of a [`DataFrame`](DataFrame.md):
it defines which columns the DataFrame contains and what types of values are stored in each column.

With the [Compiler Plugin](Compiler-Plugin.md) enabled, Kotlin DataFrame can automatically generate
[extension properties](extensionPropertiesApi.md) from a schema. 
These properties provide type-safe access to columns when working with a [`DataFrame`](DataFrame.md), 
allowing you to refer to columns by property name instead of using string literals.

A schema can be represented as a Kotlin interface (or data class). 
Each property in the interface corresponds to a column in the DataFrame:

- the property name corresponds to the column name;
- the property type corresponds to the type of values stored in that column.

For example, a schema with properties `val name: String` and `val age: Int` 
describes a `DataFrame` that contains two columns: 
`name` with string values and `age` with integer values.

You can define schema manually, but the most convenient way is to use 
[`generateInterfaces()`](DataSchemaGenerationMethods.md#generateinterfaces) —
a special method that returns a string with a schema of a receiver `DataFrame`:

<!---FUN dfGenerateInterfaces-->

```kotlin
df.generateInterfaces("Repository", nameNormalizer = NameNormalizer.id()).print()
```

Output:

```kotlin
@DataSchema
interface Repository {
    val full_name: String
    val html_url: java.net.URL
    val stargazers_count: Int
    val topics: String
    val watchers: Int
}
```

<!---END-->

Now you can copy-paste this schema into your code and use it in the [`cast()`](cast.md)
method to specify the schema of your `DataFrame` (assigning the result to a new variable `dfRepository`)

<!---FUN dfCast-->

```kotlin
val dfRepository = df.cast<Repository>()
```

<!---END-->

Now you can use [extension properties](extensionPropertiesApi.md), for example, 
to get the "full_name" column as a property:

<!---FUN dfRepositoryGetFullName-->

```kotlin
val fullNameColumn: DataColumn<String> = dfRepository.full_name
```

<!---END-->

But most importantly, you can use these properties in various operations!

> In a notebook, you don't need to specify schema manually, but 
> but you need to have **run a cell** containing a `DataFrame` variable 
> to generate extension properties for it.
> They can be used from the **next** cell onwards.
> {style="warning"}

After performing some operations, the schema may change: 
existing columns may have been removed,
new columns can be added, and both column names and types may be modified. 
The [Compiler Plugin](Compiler-Plugin.md) automatically tracks these changes, updates the schema, 
and generates new [extension properties](extensionPropertiesApi.md) on the fly.

You can inspect the current schema at any time by hovering over a [`DataFrame`](DataFrame.md) 
variable or any `DataFrame` expression.
 
![](schema_hover.png)

## Select Columns

Kotlin DataFrame features a typesafe [Columns Selection DSL](ColumnSelectors.md), 
enabling flexible and safe selection of any combination of columns.
Column selectors are widely used across operations — one of the simplest examples is [`.select { }`](select.md), 
which returns a new `DataFrame` with only the columns chosen in a *Columns Selection expression*.

Select some columns:

<!---FUN notebook_test_quickstart_5-->

```kotlin
// Select "full_name", "stargazers_count" and "topics" columns
val dfSelected = dfRepository.select { full_name and stargazers_count and topics }
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_5.html" width="705px" height="500px"></inline-frame>

## Row Filtering

Some operations use the [DataRow API](DataRow.md), with [expressions](DataRow.md#row-expressions) 
and [conditions](DataRow.md#row-conditions) that are applied for all `DataFrame` rows.
For example, [`.filter { }`](filter.md) returns a new `DataFrame` with rows 
that satisfy a condition given by the row expression.

Inside a row expression, you can access the values of the current row by column names through 
[extension properties](extensionPropertiesApi.md).
This is similar to the [Columns Selection DSL](ColumnSelectors.md),
but in this case the properties represent actual values, not column references.

Filter rows by "stargazers_count" value:

<!---FUN notebook_test_quickstart_6-->

```kotlin
// Keep only rows where "stargazers_count" value is more than 1000
val dfFiltered = dfSelected.filter { stargazers_count >= 1000 }
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_6.html" width="705px" height="500px"></inline-frame>

## Columns Rename

Columns can be renamed using the [`.rename { }`](rename.md) operation, 
which also uses the Columns Selection DSL to select a column to rename.
The [`rename`](rename.md) operation does not perform the renaming immediately; instead, 
it creates an intermediate object that must
be finalized into a new [`DataFrame`](DataFrame.md) by calling the `.to()` function with the new column name.

Rename "full_name" and "stargazers_count" columns:

<!---FUN notebook_test_quickstart_7-->

```kotlin
val dfRenamed = dfFiltered
    // Rename "full_name" column to "name"
    .rename { full_name }.to("name")
    // and "stargazers_count" to "starsCount"
    .rename { stargazers_count }.to("starsCount")
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_7.html" width="705px" height="500px"></inline-frame>

The schema is updated automatically after `rename` so you can use new extension properties right away:

<!---FUN dfRenamedSelectName-->

```kotlin
dfRenamed.select { name }
```

<!---END-->

> In a notebook, you need to have **run a cell** containing a `DataFrame` variable
> with a column renamed / changed type
> to get an updated schema for `DataFrame` property and the relevant extension properties. 
> They can be used from the **next** cell onwards.
> {style="warning"}

## Modify Columns

Column values can be modified using the [`update { }`](update.md) and [`convert { }`](convert.md) operations.
Both operations select columns to modify via the Columns Selection DSL and, similar to `rename`, create an intermediate
object that must be finalized to produce a new `DataFrame`.

The [`update`](update.md) operation preserves the original value types, 
while [`convert`](convert.md) allows changing the type.
In both cases, column names and their positions remain unchanged.

Update "name" and convert "topics":

<!---FUN notebook_test_quickstart_8-->

```kotlin
val dfUpdated = dfRenamed
    // Update "name" values with only its second part (after '/')
    .update { name }.with { it.split("/")[1] }
    // Convert "topics" `String` values into `List<String>` by splitting:
    .convert { topics }.with { it.removePrefix("[").removeSuffix("]").split(", ") }
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_8.html" width="705px" height="500px"></inline-frame>

Check the new "topics" type out:

<!---FUN notebook_test_quickstart_9-->

```kotlin
println(dfUpdated.topics.type())
```

Output:

```kotlin
kotlin.collections.List<kotlin.String>
```

<!---END-->

## Adding New Columns

The [`.add("name") { rowExpression }`](add.md) operation allows creating a `DataFrame` with a new column, 
where the value for each row is computed based
on the existing values in that row. 
These values can be accessed within the [row expressions](DataRow.md#row-expressions).

Add a new `Boolean` column "isIntellij":

<!---FUN notebook_test_quickstart_10-->

```kotlin
// Add a `Boolean` column indicating whether the `name` contains the "intellij" substring
// or the topics include "intellij".
val dfWithIsIntellij = dfUpdated.add("isIntellij") {
    name.lowercase().contains("intellij") || "intellij" in topics
}
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_10.html" width="705px" height="500px"></inline-frame>

## Grouping And Aggregating

A [`DataFrame`](DataFrame.md) can be grouped by key columns, 
meaning its rows are split into groups based on the values in the key
columns.
The [`.groupBy { }`](groupBy.md) operation selects columns and groups the `DataFrame` by their values, 
using them as grouping keys.

The result is a `GroupBy` — a `DataFrame`-like structure that associates each key with the corresponding subset of the
original `DataFrame`.

Group `dfWithIsIntellij` by "isIntellij":

<!---FUN notebook_test_quickstart_11-->

```kotlin
val groupedByIsIntellij = dfWithIsIntellij.groupBy { isIntellij }
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_11.html" width="705px" height="500px"></inline-frame>

A `GroupBy` can be [aggregated](groupBy.md#aggregation) — that is, 
you can compute one or several summary statistics for each group.
The result of the aggregation is a [`DataFrame`](DataFrame.md) containing 
the key columns along with new columns holding the computed
statistics for a corresponding group.

For example, `count()` computes the size of each group.
It returns a new `DataFrame` where each row corresponds to a group 
and contains the group's unique key (or combination of keys), 
along with a new "count" column with the group size:

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
    // Compute sum and max of "starsCount" within each group
    // into "sumStars" and "maxStars" columns
    sumOf { starsCount } into "sumStars"
    maxOf { starsCount } into "maxStars"
}
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_13.html" width="705px" height="500px"></inline-frame>

## Sorting Rows

[`.sortBy { }`/`.sortByDesc { }`](sortBy.md) sorts rows by value in selected columns, 
returning a `DataFrame` with sorted rows. 
`take(n)` returns a new `DataFrame` with the first `n` rows.

Combine them to get the Top-10 repositories by number of stars:

<!---FUN notebook_test_quickstart_14-->

```kotlin
val dfTop10 = dfWithIsIntellij
    // Sort by "starsCount" value descending
    .sortByDesc { starsCount }.take(10)
```

<!---END-->

<inline-frame src="./resources/notebook_test_quickstart_14.html" width="705px" height="500px"></inline-frame>

## Plotting With Kandy

Kandy is a Kotlin plotting library designed to bring Kotlin DataFrame features into chart creation, providing a
convenient and typesafe way to build data visualizations.

Add kandy to your project:

<tabs>
<tab title="Gradle (Kotlin DSL)">

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:%kandyVersion%")
}
```

</tab>

<tab title="Gradle (Groovy DSL)">

```groovy
dependencies {
    implementation 'org.jetbrains.kotlinx:kandy-lets-plot:%kandyVersion%'
}
```

</tab>

<tab title="Maven">

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kandy-lets-plot</artifactId>
    <version>%kandyVersion%</version>
</dependency>
```

</tab>
</tabs>

Build a simple bar chart with the `.plot { }` extension for `DataFrame`, 
that allows using DataFrame [extension properties](extensionPropertiesApi.md) inside the Kandy
plotting DSL:

<!---FUN notebook_test_quickstart_16-->

```kotlin
dfTop10.plot {
    // Create a bar layer
    bars {
        // Use values from "name" as bars categories
        x(name)
        // Use values from "starsCount" as bars heights
        y(starsCount)
    }

    layout.title = "Top 10 JetBrains repositories by stars count"
}
    // save the plot as an SVG image
    .save("top_10_repos.svg", path = "plots/")
```

<!---END-->

![notebook_test_quickstart_16](notebook_test_quickstart_16.svg)

## Write DataFrame

Kotlin DataFrame supports writing to all formats that it is capable of reading 
(except writing to databases, OpenAPI JSON and Apache Parquet, for now).

Write a dataframe into an Excel file:

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

- 🔤 **Learn more about [](schemas.md)
  and [Extension Properties](extensionPropertiesApi.md)**  
  and make working with your data both convenient and type-safe.

- 🛠️ **[Browse the operations overview](operations.md)** to learn what Kotlin DataFrame can do.

- 🧠 [**Understand the design** and core concepts](concepts.md).

- 💡 **[Learn more about Kotlin DataFrame Compiler Plugin](Compiler-Plugin.md)**  
  for auto-generated column access in your IntelliJ IDEA projects.

- 📊 **Master Kandy** for stunning and expressive DataFrame visualizations learning
  [Kandy Documentation](https://kotlin.github.io/kandy).
