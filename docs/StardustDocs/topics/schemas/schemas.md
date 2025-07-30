[//]: # (title: Data Schemas)

The Kotlin DataFrame library provides typed data access via
[generation of extension properties](extensionPropertiesApi.md) for the type
[`DataFrame<T>`](DataFrame.md) (as well as for [`DataRow<T>`](DataRow.md)), where
`T` is a marker class representing the `DataSchema` of the [`DataFrame`](DataFrame.md).

A *schema* of a [`DataFrame`](DataFrame.md) is a mapping from column names to column types.  
This data schema can be expressed as a Kotlin class or interface.  
If the DataFrame is hierarchical — contains a [column group](DataColumn.md#columngroup) or a
[column of dataframes](DataColumn.md#framecolumn) — the data schema reflects this structure,
with a separate class representing the schema of each column group or nested `DataFrame`.

For example, consider a simple hierarchical DataFrame from
<resource src="example.csv"></resource>.

This DataFrame consists of two columns:
- `name`, which is a `String` column
- `info`, which is a [column group](DataColumn.md#columngroup) containing two nested [value columns](DataColumn.md#valuecolumn):
    - `age` of type `Int`
    - `height` of type `Double`

<table width="705">
  <thead>
    <tr>
      <th>name</th>
      <th colspan="2">info</th>
    </tr>
    <tr>
      <th></th>
      <th>age</th>
      <th>height</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Alice</td>
      <td>23</td>
      <td>175.5</td>
    </tr>
    <tr>
      <td>Bob</td>
      <td>27</td>
      <td>160.2</td>
    </tr>
  </tbody>
</table>

The data schema corresponding to this DataFrame can be represented as:

```kotlin
// Data schema of the "info" column group
@DataSchema
data class Info(
    val age: Int,
    val height: Float
)

// Data schema of the entire DataFrame
@DataSchema
data class Person(
    val info: Info,
    val name: String
)
```

[Extension properties](extensionPropertiesApi.md) for `DataFrame<Person>`  
are generated based on this schema and allow accessing columns
or using them in operations:

```kotlin
// Assuming `df` has type `DataFrame<Person>`

// Get "age" column from "info" group
df.info.age

// Select "name" and "height" columns
df.select { name and info.height }

// Filter rows by "age"
df.filter { age >= 18 }
```

See [](extensionPropertiesApi.md) for more information.


## Schema Retrieving

Defining a data schema manually can be difficult, especially for dataframes with many columns or deeply nested 
structures, and may lead to mistakes in column names or types. 
Kotlin DataFrame provides several methods for generating data schemas.

* [**`generate..()` methods**](DataSchemaGenerationMethods.md) are extensions for [`DataFrame`](DataFrame.md) 
(or for its [`schema`](schema.md)) that generate a code string representing its `DataSchema`.

* [**Kotlin DataFrame Compiler Plugin**](Compiler-Plugin.md) **cannot automatically infer** a 
data schema from external sources such as files or URLs.
However, it **can** infer the schema if you construct the [`DataFrame`](DataFrame.md) 
manually — that is, by explicitly declaring the columns using the API.
It will also **automatically update** the schema during operations that modify the structure of the DataFrame.

> For best results when working with the Compiler Plugin, it's recommended to 
> generate the initial schema using one of 
> the [`generate..()` methods](DataSchemaGenerationMethods.md).
> Once generated, the Compiler Plugin will automatically keep the schema up to date 
> after any operations that change the structure of the DataFrame.

### Plugins

> The current Gradle plugin is **under consideration for deprecation** and 
> may be officially marked as deprecated in future releases.
> 
> The KSP plugin is **not compatible with [KSP2](https://github.com/google/ksp?tab=readme-ov-file#ksp2-is-here)**
> and may **not work properly with Kotlin 2.1 or newer**.
>
> At the moment, **[data schema generation is handled via dedicated methods](DataSchemaGenerationMethods.md)** instead of relying on the plugins.  
{style="warning"}

* The [Gradle plugin](Gradle-Plugin.md) allows generating a data schema automatically by specifying a source file path in the Gradle build script.

* The KSP plugin allows generating a data schema automatically using 
[Kotlin Symbol Processing](https://kotlinlang.org/docs/ksp-overview.html) by specifying 
a source file path in your code file.

## Extension Properties Generation

Once you have a data schema, you can generate [extension properties](extensionPropertiesApi.md).

The easiest and most convenient way is to use the [**Kotlin DataFrame Compiler Plugin**](Compiler-Plugin.md), 
which generates extension properties on the fly for declared data schemas 
and automatically keeps them up to date after operations 
that modify the structure of the [`DataFrame`](DataFrame.md).

> Extension properties generation was deprecated from the Gradle plugin in favor of the Compiler Plugin.  
> {style="warning"}

* When using Kotlin DataFrame inside [Kotlin Notebook](SetupKotlinNotebook.md), 
the schema and extension properties
are generated automatically after each cell execution for all `DataFrame` variables declared in that cell.
See [extension properties example in Kotlin Notebook](extensionPropertiesApi.md#example).

> Compiler Plugin is coming to Kotlin Notebook soon.

* If you're not using the Compiler Plugin, you can still generate 
[extension properties](extensionPropertiesApi.md) for a [`DataFrame`](DataFrame.md)
manually by calling one of the [`generate..()` methods](DataSchemaGenerationMethods.md) 
with the `extensionProperties = true` argument.
