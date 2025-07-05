# Concepts And Principles

<web-summary>
Learn what Kotlin DataFrame is about — its core concepts, design principles, and usage philosophy.
</web-summary>

<card-summary>
Discover the fundamentals of the library —
understand key concepts, motivation, and the overall structure of the library.
</card-summary>

<link-summary>
Explore the fundamentals of Kotlin DataFrame — 
understand key concepts, motivation, and the overall structure of the library.
</link-summary>


<show-structure depth="3"/>


## What is a dataframe

A *dataframe* is an abstraction for working with structured data. 
Essentially, it’s a 2-dimensional table with labeled columns of potentially different types. 
You can think of it like a spreadsheet or SQL table, or a dictionary of series objects.

The handiness of this abstraction is not in the table itself but in a set of operations defined on it. 
The Kotlin DataFrame library is an idiomatic Kotlin DSL defining such operations. 
The process of working with dataframe is often called *data wrangling* which 
is the process of transforming and mapping data from one "raw" data form into another format 
that is more appropriate for analytics and visualization. 
The goal of data wrangling is to ensure quality and useful data.

## Main Features and Concepts

* [**Hierarchical**](hierarchical.md) — the Kotlin DataFrame library provides an ability to read and present data from different sources, 
including not only plain **CSV** but also **JSON** or **[SQL databases](readSqlDatabases.md)**.
This is why it was designed to be hierarchical and allows nesting of columns and cells.
* **Functional** — the data processing pipeline is organized in a chain of [`DataFrame`](DataFrame.md)  transformation operations.
* **Immutable** — every operation returns a new instance of [`DataFrame`](DataFrame.md)  reusing underlying storage wherever it's possible.
* **Readable** — data transformation operations are defined in DSL close to natural language.
* **Practical** — provides simple solutions for common problems and the ability to perform complex tasks.
* **Minimalistic** — simple, yet powerful data model of three [column kinds](DataColumn.md#column-kinds).
* [**Interoperable**](collectionsInterop.md) — convertable with Kotlin data classes and collections.
  This also means conversion to/from other libraries' data structures is usually quite straightforward!
  See our [examples](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples) 
  for some conversions between DataFrame and [Apache Spark](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/spark), [Multik](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/multik), and [JetBrains Exposed](https://github.com/Kotlin/dataframe/tree/master/examples/idea-examples/unsupported-data-sources/src/main/kotlin/org/jetbrains/kotlinx/dataframe/examples/exposed).
* **Generic** — can store objects of any type, not only numbers or strings.
* **Typesafe** — the Kotlin DataFrame library provides a mechanism of on-the-fly [**generation of extension properties**](extensionPropertiesApi.md) 
that correspond to the columns of a dataframe. 
In interactive notebooks like Jupyter or Datalore, the generation runs after each cell execution. 
In IntelliJ IDEA there's a Gradle plugin for generation properties based on CSV file or JSON file. 
Also, we’re working on a compiler plugin that infers and transforms [`DataFrame`](DataFrame.md) schema while typing.
You can now clone this [project with many examples](https://github.com/koperagen/df-plugin-demo) showcasing how it allows you to reliably use our most convenient extension properties API.
The generated properties ensure you’ll never misspell column name and don’t mess up with its type, and of course nullability is also preserved.
* [**Polymorphic**](schemas.md) —
  if all columns of a [`DataFrame`](DataFrame.md) instance are presented in another dataframe,
  then the first one will be seen as a superclass for the latter. 
This means you can define a function on an interface with some set of columns
  and then execute it safely on any [`DataFrame`](DataFrame.md) which contains this same set of columns.
  In notebooks, this works out-of-the-box.
  In ordinary projects, this requires casting (for now).
