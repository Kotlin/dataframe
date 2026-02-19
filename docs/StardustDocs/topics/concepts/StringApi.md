# String API

<web-summary>
Work with columns in Kotlin DataFrame using simple string-based selectors.
</web-summary>

<card-summary>
Use the String API in Kotlin DataFrame to select columns directly by name and build expressions with minimal setup.
</card-summary>

<link-summary>
An introduction to the Kotlin DataFrame String API for column selection.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.concepts.StringApi-->

The String API is the most basic and straightforward way to select columns
in Kotlin DataFrame [operations](operations.md).

In String API operation overloads, selected column names are provided directly as `String` values
in function arguments:

<!---FUN simpleSelect-->

```kotlin
// Select a sub-dataframe with the "name" and "info" columns
df.select("name", "info")
```

<!---END-->

## String Column Accessors

The String API can also be used inside the
[Columns Selection DSL](ColumnSelectors.md) and
[row expressions](DataRow.md#row-expressions)
via *`String` column accessors*.

`String` column accessors allow you to access nested columns and combine them with
[the extensions properties](extensionPropertiesApi.md) 
or with any other [CS DSL methods](ColumnSelectors.md#functions-overview).

String column accessors are created using special functions.
In the Columns Selection DSL, they have the special type `ColumnAccessor`,
while in row expressions they resolve to concrete value types.

You can optionally specify the column type as a type argument of the
`String` column accessor creation function.
This is required for row expressions and for some operations with a column selection.
If the specified type does not match the actual column type,
a runtime exception may be thrown.

| Columns Seletcion DSL                      | Row Expressions          |                                                                                                                                            |
|--------------------------------------------|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| `col("name")` / `col<T>("name")`           | `getValue<T>("name")`    | Resolves into general [`DataColumn`](DataColumn.md) / row value with the provided `"name"` and type `T`.                                   |
| `colGroup("name")` / `colGroup<T>("name")` | `getColumnGroup("name")` | Resolves into [`ColumnGroup`](DataColumn.md#columngroup) with the provided `"name"` and type `T`. Can be used for accessing nested columns |
| `valueCol("name")` / `valueCol<T>("name")` | `getValue<T>("name")`    | Resolves into [`ValueColumn`](DataColumn.md#valuecolumn) / row value with the provided `"name"` and type `T`.                              |
| `frameCol("name")` / `frameCol<T>("name")` | `getFrameColumn("name")` | Resolves into [`FrameColumn`](DataColumn.md#framecolumn) / `DataFrame` with the provided `"name"` and type `T`.                            |

> Row Expressions methods may be changed in the future.
> {style = "warning"}

### Example

Consider a simple hierarchical dataframe from
<resource src="example.csv"></resource>.

This table consists of two columns: `name`, which is a `String` column, and `info`,
which is a [**column group**](DataColumn.md#columngroup) containing two nested
[value columns](DataColumn.md#valuecolumn) —
`age` of type `Int`, and `height` of type `Double`.

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

#### Columns Selection DSL

Get a single "height" subcolumn from the "info" column group

<!---FUN getColumn-->

```kotlin
df.getColumn { colGroup("info").col("height") }
```

<!---END-->

Select the "age" subcolumn of the "info" column group and the "name" column

<!---FUN selectSubcolumnAndColumn-->

```kotlin
df.select { colGroup("info").col("age") and col("name") }
```

<!---END-->

Calculate the mean value of the ("info"->"age") column; specify the column type as a `col` type argument

<!---FUN meanValueBySubcolumn-->

```kotlin
df.mean { colGroup("info").col<Int>("age") }
```

<!---END-->

Combine Extensions Properties and String Column Accessors.
Select "height" and "name" columns, assuming we have extensions properties
for "info" and "name" columns but not for the ("info"->"height") column

<!---FUN combineExtensionsAndStrings-->

```kotlin
df.select { "info".col("height") and name }
```

<!---END-->

Combine Columns Selection DSL and String Column Accessors.
Remove all `Number` columns from the dataframe except ("info"->"age")

<!---FUN removeWithExcept-->

```kotlin
df.remove {
    colsAtAnyDepth().colsOf<Number>() except
        colGroup("info").col("age")
}
```

<!---END-->

Select all subcolumns from the "info" column group

<!---FUN selectSubcolumns-->

```kotlin
df.select { colGroup("info").select { col("age") and col("height") } }
// or
df.select { colGroup("info").allCols() }
```

<!---END-->


#### Row Expressions

Add a new "heightInt" column by casting the "height" column values to `Int`

<!---FUN addColumnFromSubcolumn-->

```kotlin
df.add("heightInt") {
    "info"["height"]<Double>().toInt()
}
```

<!---END-->

Filter rows where the ("info"->"age") column value is greater than or equal to 18

<!---FUN filterBySubcolumn-->

```kotlin
df.filter { "info"["age"]<Int>() >= 18 }
```

<!---END-->


### Invoked String API

> This API is outdated and may be hard to read and refactor;
> it may be changed in the future.
> 
> Please don't mix it with the `col`/`colGroup` methods.
> 
> We don't recommend using it in production code.
> {style = "warning"}

Alternatively, you can use the `String` invocation (optional typed argument) for column accessor creation.
It will create the same column accessors as in the Columns Selection DSL.
You can't specify the column kind in this case, but you can access nested columns using the 
`String.get` or `String.invoke` operators or using the ` String.select {} ` function, 
where the receiver is the column group name.

<!---FUN invocatedStringsApi-->

```kotlin
// Columns Selection DSL

// Get a single "height" subcolumn from the "info" column group
df.getColumn { "info"["height"]<Double>() }

// Select the "age" subcolumn of the "info" column group
// and the "name" column
df.select { "info"["age"] and "name"() }

// Calculate the mean value of the ("info"->"age") column;
// specify the column type as an invocation type argument
df.mean { "info" { "age"<Int>() } }

// Select all subcolumns from the "info" column group
df.select { "info" { "age"() and "height"() } }
// or
df.select { "info".allCols() }

// Row Expressions

// Add a new "heightInt" column by
// casting the "height" column values to `Int`
df.add("heightInt") {
    "info"["height"]<Double>().toInt()
}

// Filter rows where the ("info"->"age") column value
// is greater than or equal to 18
df.filter { "info"["age"]<Int>() >= 18 }
```

<!---END-->

## When should I use the String API?

The String API is a good starting point for learning the library
and understanding how column selection works.

For production code we strongly recommend using the
[**Extension Properties API**](extensionPropertiesApi.md) instead.
It is more concise, fully type-safe, and provides better IDE support.

However, note that sometimes the usage of Extension Properties API is not possible
or may require too many excess actions. 
In such cases, use [](#string-column-accessors).
