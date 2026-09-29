# JSON

<web-summary>
Support for working with JSON data — load, explore, and save structured JSON using Kotlin DataFrame.
</web-summary>

<card-summary>
Easily handle JSON data in Kotlin — read from files or URLs, and export your data back to JSON format.
</card-summary>

<link-summary>
Kotlin DataFrame support for reading and writing JSON files in a structured and type-safe way.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.io.Json-->

Kotlin DataFrame supports reading from and writing to JSON files.

Requires the [`dataframe-json` module](Modules.md#dataframe-json), 
which is included by default in the general [`dataframe`](Modules.md#dataframe-general) 
artifact and in [`%use dataframe`](SetupKotlinNotebook.md#integrate-kotlin-dataframe) 
for Kotlin Notebook.

> Kotlin DataFrame is suitable only for working with table-like structured JSON — 
> a list of objects where each object represents a row and all objects share the same structure.
>
> Experimental support for [OpenAPI JSON schemas](OpenAPI.md) is also available.  
> {style="note"}

## Read

You can read a [`DataFrame`](DataFrame.md) or [`DataRow`](DataRow.md) 
from a JSON file (via a file path or URL) using the [`readJson()`](read.md#read-from-json) method:

<!---FUN readJson-->

```kotlin
val df = DataFrame.readJson("example.json")
```

<!---END-->

<!---FUN readJsonViaUrl-->

```kotlin
val df = DataFrame.readJson("https://kotlin.github.io/dataframe/resources/example.json")
```

<!---END-->

A JSON `null` is read as `null`, so the corresponding column becomes nullable. A property that some records
simply don't have is read as `null` as well — a [`DataFrame`](DataFrame.md) is rectangular, so every row needs
a value in every column.

This also holds for array columns: `[null, [123], []]` is read as a `List<Int>?` column holding `null`, `[123]`,
and an empty list.

JSON elements that aren't objects have no property name to use as a column name, so they're read into the
special [`value` and `array` columns](read.md#value-and-array-columns). 
The same special columns are also used
to resolve [type clashes](read.md#manage-type-clashes) 
when the same property contains values of different shapes
across records.

## Write

You can write a [`DataFrame`](DataFrame.md) to a JSON file using the [`writeJson()`](write.md#writing-to-json) method:

<!---FUN writeJson-->

```kotlin
df.writeJson("example.json")
```

<!---END-->

A [`DataFrame`](DataFrame.md) read from JSON with a top-level type clash is written back to its original form;
see [writing to JSON](write.md#writing-to-json) for the details and for the cases that don't round-trip.
