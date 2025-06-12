[//]: # (title: Extension Properties API)

When working with a [`DataFrame`](DataFrame.md), the most convenient and reliable way 
to access its columns — including for operations and retrieving column values 
in row expressions — is through auto-generated extension properties.
They are generated based on a [dataframe schema](schemas.md),
with the name and type of properties inferred from the name and type of the corresponding columns.
It also works for all types of hierarchical dataframes.

> The behavior of data schema generation differs between the 
> [Compiler Plugin](Compiler-Plugin.md) and [Kotlin Notebook](gettingStartedKotlinNotebook.md).
>
> * In **Kotlin Notebook**, a schema is generated *only after cell execution* for 
> `DataFrame` variables defined within that cell.
> * With the **Compiler Plugin**, a new schema is generated *after every operation*
> — but support for all operations is still in progress. 
> Retrieving the schema for `DataFrame` read from a file or URL is *not yet supported* either.
>
> This behavior may change in future releases. See the [example](#example) below that demonstrates these differences.
{style="warning"}

## Example

Consider a simple hierarchical dataframe from
<resource src="example.csv"></resource>.

This table consists of two columns: `name`, which is a `String` column, and `info`, 
which is a [**column group**](DataColumn.md#columngroup) containing two nested 
[value columns](DataColumn.md#valuecolumn) — 
`age` of type `Int`, and `height` of type `Double`.

<table>
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

<tabs>
<tab title="Kotlin Notebook">
Read the [`DataFrame`](DataFrame.md) from the CSV file:

```kotlin
val df = DataFrame.readCsv("example.csv")
```

*After cell execution* data schema and extensions for this `DataFrame` will be generated 
so you can use extensions for accessing columns, 
using it in operations inside the [Column Selector DSL](ColumnSelectors.md) 
and [DataRow API](DataRow.md):


```kotlin
// Get nested column
df.info.age
// Sort by multiple columns
df.sortBy { name and info.height }
// Filter rows using a row condition. 
// These extensions express the exact value in the row 
// with the corresponding type:
df.filter { name.startsWith("A") && info.age >= 16 }
```

If you change the dataframe's schema by changing any column [name](rename.md), 
or [type](convert.md) or [add](add.md) a new one, you need to 
run a cell with a new [`DataFrame`](DataFrame.md) declaration first. 
For example, rename the `name` column into "firstName":

```kotlin
val dfRenamed = df.rename { name }.into("firstName")
```

After running the cell with the code above, you can use `firstName` extensions in the following cells:

```kotlin
dfRenamed.firstName
dfRenamed.rename { firstName }.into("name")
dfRenamed.filter { firstName == "Nikita" }
```

See the [](quickstart.md) in Kotlin Notebook with basic Extension Properties API examples.

</tab>
<tab title="Compiler Plugin">

For now, if you read [`DataFrame`](DataFrame.md) from a file or URL, you need to define its schema manually. 
You can do it quickly with [`generate..()` methods](DataSchema-Data-Classes-Generation.md).

Define schemas:
```kotlin
@DataSchema
data class PersonInfo(
    val age: Int,
    val height: Float
)

@DataSchema
data class Person(
    val info: PersonInfo,
    val name: String
)
```

Read the `DataFrame` from the CSV file and specify the schema with 
[`.convertTo()`](convertTo.md) or [`cast()`](cast.md):

```kotlin
val df = DataFrame.readCsv("example.csv").convertTo<Person>()
```

Extensions for this `DataFrame` will be generated automatically by the plugin, 
so you can use extensions for accessing columns, 
using it in operations inside the [Column Selector DSL](ColumnSelectors.md)
and [DataRow API](DataRow.md).


```kotlin
// Get nested column
df.info.age
// Sort by multiple columns
df.sortBy { name and info.height }
// Filter rows using a row condition. 
// These extensions express the exact value in the row 
// with the corresponding type:
df.filter { name.startsWith("A") && info.age >= 16 }
```

Moreover, new extensions will be generated on-the-fly after each schema change: 
by changing any column [name](rename.md),
or [type](convert.md) or [add](add.md) a new one.
For example, rename the "name" column into "firstName" and then we can use `firstName` extensions
in the following operations:

```kotlin
// Rename "name" column into "firstName"
df.rename { name }.into("firstName")
    // Can use `firstName` extension in the row condition 
    // right after renaming
    .filter { firstName == "Nikita" }
```

See [Compiler Plugin Example](https://github.com/Kotlin/dataframe/tree/plugin_example/examples/kotlin-dataframe-plugin-example) 
IDEA project with basic Extension Properties API examples.
</tab>
</tabs>
