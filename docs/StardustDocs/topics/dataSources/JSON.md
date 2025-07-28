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

Kotlin DataFrame supports reading from and writing to JSON files.

Requires the [`dataframe-json` module](Modules.md#dataframe-json), 
which is included by default in the general [`dataframe`](Modules.md#dataframe-general) 
artifact and in [`%use dataframe`](gettingStartedKotlinNotebook.md#integrate-kotlin-dataframe) 
for Kotlin Notebook.

> Kotlin DataFrame is suitable only for working with table-like structured JSON — 
> a list of objects where each object represents a row and all objects share the same structure.
>
> Experimental support for [OpenAPI JSON schemas](OpenAPI.md) is also available.  
> {style="note"}

## Read

You can read a [`DataFrame`](DataFrame.md) or [`DataRow`](DataRow.md) 
from a JSON file (via a file path or URL) using the [`readJson()`](read.md#read-from-json) method:

```kotlin
val df = DataFrame.readJson("example.json")
```

```kotlin
val df = DataFrame.readJson("https://kotlin.github.io/dataframe/resources/example.json")
```

## Write

You can write a [`DataFrame`](DataFrame.md) to a JSON file using the [`writeJson()`](write.md#writing-to-json) method:

```kotlin
df.writeJson("example.json")
```
