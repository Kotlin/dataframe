# Frequently Asked Questions

Here's a list of frequently asked questions about Kotlin DataFrame.  
If you haven’t found an answer to yours, feel free to ask it on:

- [GitHub Issues](https://github.com/Kotlin/dataframe/issues)
- [#datascience](https://slack-chats.kotlinlang.org/c/datascience) channel in Kotlin Slack 
([request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up?_gl=1*1ssyqy3*_gcl_au*MTk5NzUwODYzOS4xNzQ2NzkxMDMz*FPAU*MTk5NzUwODYzOS4xNzQ2NzkxMDMz*_ga*MTE0ODQ1MzY3OS4xNzM4OTY1NzM3*_ga_9J976DJZ68*czE3NTE1NDUxODUkbzIyNyRnMCR0MTc1MTU0NTE4NSRqNjAkbDAkaDA.)).

## What is Kotlin DataFrame?

**Kotlin DataFrame** is an official open-source Kotlin framework written in pure 
Kotlin for working with tabular data.  
Its goal is to reconcile Kotlin’s static typing with the dynamic nature of data,  
providing a flexible and convenient idiomatic DSL for working with data in Kotlin.

## Is Kotlin DataFrame a Multiplatform Library?

Not yet — Kotlin DataFrame currently supports only the **JVM** target.

We’re actively exploring multiplatform support.  
To stay updated on progress, subscribe to the 
[corresponding issue](https://github.com/Kotlin/dataframe/issues/24).

### Does Kotlin DataFrame work on Android?

Yes — Kotlin DataFrame can be used in Android projects.

There is no dedicated Android artifact yet, but you can include the standard **JVM artifact**  
by setting up a [custom Gradle configuration](SetupAndroid.md).

## How to start with Kotlin DataFrame ?

If you're new to Kotlin DataFrame, the [Quickstart guide](quickstart.md) is the perfect place to begin —  
it gives a brief yet comprehensive introduction to the basics of working with DataFrame.

You can also check out [other guides and examples](Guides-And-Examples.md)  
to explore various use cases and deepen your understanding of Kotlin DataFrame.

## What is the best environment to use Kotlin DataFrame?

For the best experience, Kotlin DataFrame is most effective in an interactive environment.

- **[Kotlin Notebook](SetupKotlinNotebook.md)** is ideal for exploring Kotlin DataFrame.  
  Everything works out of the box — interactivity, rich rendering of DataFrames and plots.  
  You can instantly see the results of each operation, view the contents of your DataFrames after every transformation,  
  inspect individual rows and columns, and explore data step-by-step in a live and interactive way.  
  See the [](quickstart.md) to get started quickly.

- **[Kotlin DataFrame Compiler Plugin for IDEA projects](Compiler-Plugin.md)** enhances your usual
  [IntelliJ IDEA](https://www.jetbrains.com/idea/) Kotlin projects by enabling compile-time  
  [extension properties](extensionPropertiesApi.md) generation.  
  This allows you to work with DataFrames in a name- and type-safe manner,  
  integrating seamlessly with the IDE.

## Is `DataFrame` mutable?

No, [`DataFrame`](DataFrame.md) is a completely immutable structure.  
Kotlin DataFrame follows the functional style of Kotlin —  
each [operation](operations.md) that modifies the data returns a new, updated `DataFrame` instance.

This means original data is never changed in-place, which improves code safety.

## How do I interoperate with collections like `List` or `Map`?

[`DataFrame`](DataFrame.md) integrates seamlessly with Kotlin collections.

You can:
- Create a `DataFrame` from a `Map` using [`toDataFrame()`](createDataFrame.md#todataframe).
- Convert a `DataFrame` back to a `Map` using [`toMap()`](toMap.md).
- Create a [`DataColumn`](DataColumn.md) from a `List` using [`toColumn()`](createColumn.md#tocolumn).
- Convert a `DataColumn` to a `List` of values.
- Convert a `DataFrame<T>` into a `List<T>` of data class instances corresponding to each row  
  using [`toList()`](toList.md).

## Are there any limitations on the types used in a DataFrame?

No! You can store values of **any Kotlin or Java types** inside a [`DataFrame`](DataFrame.md)  
and work with them in a type-safe manner using [extension properties](extensionPropertiesApi.md)  
across various [operations](operations.md).

For some commonly used types — such as  
[Kotlin basic types](https://kotlinlang.org/docs/basic-types.html) and  
[Kotlin date-time types](https://github.com/Kotlin/kotlinx-datetime) —  
there is built-in support for automatic conversion and parsing.

## What data sources are supported?

<!------TODO data sources---->

Kotlin DataFrame supports all popular data sources — CSV, JSON, Excel, Apache Arrow, SQL databases, and more!  
See the [Data Sources section](Data-Sources.md) for a complete list of supported formats  
and instructions on how to integrate them into your workflow.

Some sources — such as Apache Spark, [Exposed](https://www.jetbrains.com/help/exposed/home.html),  
and [Multik](https://github.com/Kotlin/multik) — are not supported directly (yet),  
but you can find [official integration examples here](Integrations.md).

If the data source you need isn't supported yet,  
feel free to open an [issue](https://github.com/Kotlin/dataframe/issues)  
and describe your use case — we’d love to hear from you!
 
## I see magically appearing properties in examples. What is it?

These are [extension properties](extensionPropertiesApi.md) — one of the key features of Kotlin DataFrame.

Extension properties correspond to the columns of a [`DataFrame`](DataFrame.md), allowing you to access and select them
in a **type-safe** and **name-safe** way.

They are generated automatically when working with Kotlin DataFrame in:

- [Kotlin Notebook](SetupKotlinNotebook.md), where extension properties are generated 
after each cell execution.
- A Kotlin project in [IntelliJ IDEA](https://www.jetbrains.com/idea/) with the
  [](Compiler-Plugin.md) enabled, where the properties are generated at compile time.

## I used the KProperties API in older versions, what should I use now that it's deprecated?

The KProperty API was a useful access mechanism in earlier versions.  
However, with the introduction of [extension properties](extensionPropertiesApi.md) 
and the [Kotlin DataFrame compiler plugin](Compiler-Plugin.md),
you now have a more flexible and powerful alternative.

Annotate your Kotlin class with [`@DataSchema`](Compiler-Plugin.md#dataschema-declarations),  
and the plugin will automatically generate type-safe extension properties 
for your [`DataFrame`](DataFrame.md).
Or alternatively, call [`toDataFrame()`](createDataFrame.md#todataframe) on a list of Kotlin or Java objects, 
and the resulting `DataFrame` will have schema according to their properties or getters.

See [compiler plugin examples](Compiler-Plugin.md#examples).

## How to visualize data from a DataFrame?

[Kandy](https://kotlin.github.io/kandy) is a Kotlin plotting library  
designed to integrate seamlessly with Kotlin DataFrame.  
It provides a convenient and idiomatic Kotlin DSL for building charts,  
leveraging all Kotlin DataFrame features — including [extension properties](extensionPropertiesApi.md).

See the [Kandy Quick Start Guide](https://kotlin.github.io/kandy/quick-start-guide.html)
and explore the [Examples Gallery](https://kotlin.github.io/kandy/examples.html).

## Can I work with hierarchical/nested data?

Yes, Kotlin DataFrame is designed to work with hierarchical data.

You can read JSON or any other nested format into a [`DataFrame`](DataFrame.md)
with hierarchical structure — using `FrameColumn` 
(a column of data frames) and `ColumnGroup` (a column with nested subcolumns).

Both [dataframe schemas](schemas.md) and [extension properties](extensionPropertiesApi.md)
fully support nested data structures, allowing type-safe access and transformations at any depth.

See [](hierarchical.md) for more information.

Also, you can transform your data into grouped structures using [`groupBy`](groupBy.md) or [`pivot`](pivot.md).

## Does Kotlin DataFrame support OpenAPI schemas?

Yes — the experimental `dataframe-openapi` module adds support for OpenAPI JSON schemas.  
You can use it to parse and work with OpenAPI-defined structures directly in Kotlin DataFrame.
 
See the [OpenAPI Guide](https://github.com/Kotlin/dataframe/blob/master/examples/notebooks/json/KeyValueAndOpenApi.ipynb) 
for details and examples.

## Does Kotlin DataFrame support geospatial data?

Yes — the experimental `dataframe-geo` module provides functionality for working with geospatial data,  
including support for reading and writing GeoJSON and Shapefile formats, as well as tools for manipulating geometry types.

See the [GeoDataFrame Guide](https://kotlin.github.io/kandy/geo-plotting-guide.html) for details 
and examples with beautiful [Kandy](https://kotlin.github.io/kandy) geo visualizations.

## What is the difference between Compiler Plugin, Gradle Plugin, and KSP Plugin?

> The current Gradle plugin is **under consideration for deprecation** and may be officially marked as deprecated 
> in future releases.
>
> The KSP plugin is **not compatible with [KSP2](https://github.com/google/ksp?tab=readme-ov-file#ksp2-is-here)**
> and may **not work properly with Kotlin 2.1 or newer**.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead 
> of relying on the plugins. 
> See [](Migration-From-Plugins.md).
{style="warning"}

All these plugins relate to working with [dataframe schemas](schemas.md), but they serve different purposes:

- **[Gradle Plugin](Gradle-Plugin.md)** and **[KSP Plugin](https://github.com/Kotlin/dataframe/tree/master/plugins/symbol-processor)** 
 are used to **generate data schemas** from external sources as part of the Gradle build process.

    - **Gradle Plugin**: You declare the data source in your `build.gradle.kts` file  
      using the `dataframes { ... }` block.

    - **KSP Plugin**: You annotate your Kotlin file with `@ImportDataSchema` file annotation,  
      and the schema will be generated via Kotlin Symbol Processing.

  See [Data Schemas in Gradle Projects](https://kotlin.github.io/dataframe/schemasgradle.html) for more.

- **[Compiler Plugin](Compiler-Plugin.md)** provides **on-the-fly generation** of 
[extension properties](extensionPropertiesApi.md)
based on an existing schema **during compilation**, and updates the [`DataFrame`](DataFrame.md)
schema seamlessly after operations.
However, when reading data from files or external sources (like SQL),
the initial `DataFrame` schema cannot be inferred automatically —
you need to specify it manually or generate it using the [`generate..()` methods](DataSchemaGenerationMethods.md).

## How do I contribute or report an issue?

We’re always happy to receive contributions!

If you’d like to contribute, please refer to our  
[contributing guidelines](https://github.com/Kotlin/dataframe/blob/master/CONTRIBUTING.md).

To report bugs or suggest improvements, open an issue on the  
[DataFrame GitHub repository](https://github.com/Kotlin/dataframe/issues).

You’re also welcome to ask questions or discuss anything related to Kotlin DataFrame in the  
[#datascience](https://slack-chats.kotlinlang.org/c/datascience) channel on Kotlin Slack.  
If you’re not yet a member, you can 
[request an invitation](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up?_gl=1*1ssyqy3*_gcl_au*MTk5NzUwODYzOS4xNzQ2NzkxMDMz*FPAU*MTk5NzUwODYzOS4xNzQ2NzkxMDMz*_ga*MTE0ODQ1MzY3OS4xNzM4OTY1NzM3*_ga_9J976DJZ68*czE3NTE1NDUxODUkbzIyNyRnMCR0MTc1MTU0NTE4NSRqNjAkbDAkaDA.).
