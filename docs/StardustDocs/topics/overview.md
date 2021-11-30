[//]: # (title: Overview)

<tip> 

This documentation is written in such a way that it could be read sequentially and in this case, it  provides all necessary information about the library, but at the same time the [Operations](operations.md) section could be used as an API reference

</tip>

## What is Data Frame

Data frame is an abstraction for working with structured data. Essentially it’s a 2-dimensional table with labeled columns of potentially different types. You can think of it like a spreadsheet or SQL table, or a dictionary of series objects.

The handiness of this abstraction is not in the table itself but in a set of operations defined on it. `Kotlin Dataframe` library is an idiomatic Kotlin DSL defining such operations. The process of working with data frame is often called *data wrangling* which is the process of transforming and mapping data from one "raw" data form into another format that is more appropriate for analytics and visualization. The goal of data wrangling is to assure quality and useful data.

## Main Features and Concepts

* [**Hierarchical**](hierarchical.md) — `Kotlin Dataframe`  is able to read and present data from different sources including not only plain **CSV** but also **JSON**. That’s why it has been designed hierarchical and allows nesting of columns and cells.

* [**Interoperable**](stdlib.md) — hierarchical data layout also opens a possibility of converting any objects structure in application memory to a data frame and vice versa.

* **Safe** —`Kotlin Dataframe` provides a mechanism of on-the-fly [**generation of extension properties**](extensionPropertiesApi.md) that correspond to the columns of frame. In interactive notebooks like Jupyter or Datalore, the generation runs after each cell execution. In IntelliJ IDEA there's a Gradle plugin for generation properties based on CSV and Json. Also, we’re working on a compiler plugin that infers and transforms data frame schema while typing. <br /> The generated properties ensures you’ll never misspell column name and don’t mess up with its type, and of course nullability is also preserved.

* **Generic** - columns can store objects of any type, not only numbers or strings.

* [**Polymorphic**](schemas.md) — if all columns of dataframe are presented in some other dataframe, then the first one could be a superclass for latter. Thus, one can define a function on an interface with some set of columns and then execute it in a safe way on any dataframe which contains this set of columns.

* **Immutable** — all operations on `DataFrame` produce new instance, while underlying data is reused wherever it's possible

## Basic Syntax

```kotlin
DataFrame.read("titanic.csv")
    .cast<Titanic>()
    .filter { survived && home.endsWith("NY") && age in 10..20 }
    .add("birthYear") { 1912 - age }
    .groupBy { pclass }.aggregate {
        pivot { sex }.mean { survived }
        maxBy { age }.name into "oldest person"
    }
    .sortByDesc { age }
    .print()
```
