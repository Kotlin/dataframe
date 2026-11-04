[//]: # (title: append)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.AppendSamples-->

<web-summary>
Discover the `append` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `append` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `append` operation for Kotlin DataFrame.
</link-summary>

Adds one or more rows to a [`DataFrame`](DataFrame.md). Returns a new `DataFrame` containing the existing rows followed by rows constructed from the
supplied values or objects.

**API reference**: [`append`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/append.html)

```text
df.append(vararg values: Any?): DataFrame
df.append(vararg rows: T): DataFrame<T>

T: DataRowSchema
```

Every appended value must be compatible with the corresponding column:

* A [`ValueColumn`](DataColumn.md#valuecolumn) accepts `null` or a value whose runtime type is the same as or a subtype
  of the column type.
* A [`ColumnGroup`](DataColumn.md#columngroup) accepts `null`, a [`DataRow`](DataRow.md), or a `List`. Values from a
  `DataRow` are matched to the nested columns by name: a nested column whose name is absent from the row receives
  `null`, while columns present only in the row are ignored. Values from a `List` are read in the nested column order,
  one value per nested column. A shorter list throws `IndexOutOfBoundsException`, while additional values in a longer
  list are ignored.
* A [`FrameColumn`](DataColumn.md#framecolumn) accepts a `DataFrame` or `null`, which is
  [represented by an empty dataframe](#null-with-framecolumn).

`append` does not modify the original `DataFrame`.
When `values` or `rows` is empty, it returns the original `DataFrame` instance.

The overload that accepts `rows` appends one row for each object in `rows`. The `DataFrame` schema type `T` must
implement `DataRowSchema`, and every object in `rows` must be an instance of `T`. With the [compiler plugin](Compiler-Plugin.md), a data class annotated with
`@DataSchema` implements `DataRowSchema` automatically. The plugin is not required when a class or interface
implements `DataRowSchema` explicitly.

Passing values to a `DataFrame` without columns, appending an incomplete top-level row, or passing a value whose type
is incompatible with its column results in an `IllegalArgumentException`. A list supplied for a `ColumnGroup` that
contains fewer values than the group has columns results in an `IndexOutOfBoundsException`.

<warning>

Appending `null` widens the runtime type of each affected value column to nullable, while the compile-time schema
of the returned `DataFrame` is not refined. Use [`cast`](cast.md) or [`convertTo`](convertTo.md) with a nullable
schema before accessing these values through typed accessors.

</warning>

<tip>

Adding rows creates a new dataframe and rebuilds its columns using the existing and appended values.
Repeatedly appending rows one at a time in a loop is a performance antipattern.
Prefer building a dataframe at once or appending/concatenating rows in batches.

</tip>

See also:

* [`appendNulls`](appendNulls.md) — appends rows filled with `null` values.
* [`concat`](concat.md) — vertically combines dataframes or rows.
* [`duplicateRows`](duplicate.md) — repeats existing rows.
* [`add`](add.md) — adds columns rather than rows.

### Parameters

* `values: vararg Any?` — a flat sequence of values to construct new rows from.
  Each consecutive group containing one value for every top-level column forms one new
  row, and the values within each group must follow the dataframe's column order. The number of values
  must be a multiple of the number of columns.
* `rows: vararg T` — objects representing complete rows to append, in argument order. `T` must implement
  `DataRowSchema` and must be the schema type of `typedDf`.

### Examples

The examples in this section use the following dataframe. Appended rows are highlighted in the outputs:

<!---FUN appendDf-->

```kotlin
df
```

<inline-frame src="./resources/appendDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

#### Append values

Pass one value for each column to append a single row:

<!---FUN appendOneRow-->

```kotlin
df.append("Bob", 30)
```

<inline-frame src="./resources/appendOneRow.html" width="100%" height="500px"></inline-frame>

<!---END-->

Pass several complete groups of values to append several rows in one call:

<!---FUN appendSeveralRows-->

```kotlin
df.append(
    // the first new row
    "Bob",
    30,
    // the second new row
    "Charlie",
    25,
)
```

<inline-frame src="./resources/appendSeveralRows.html" width="100%" height="500px"></inline-frame>

<!---END-->

`null` can be supplied for an individual value. Only that cell in the new row is null:

<!---FUN appendNullValue-->

```kotlin
df.append("Bob", null)
```

<inline-frame src="./resources/appendNullValue.html" width="100%" height="500px"></inline-frame>

<!---END-->

Calling `append()` without values returns the original `DataFrame` instance:

<!---FUN appendNoValues-->

```kotlin
df.append()
```

<inline-frame src="./resources/appendNoValues.html" width="100%" height="500px"></inline-frame>

<!---END-->

#### Append objects as rows

When the compiler plugin is enabled, it makes a data class annotated with `@DataSchema` implement `DataRowSchema`.
When the annotated class is used as the `DataFrame` schema type, its instances can be passed directly to the
type-safe `append` overload. In the following example, `Person` is such a class and `df` has the type
`DataFrame<Person>`:

<!---FUN appendDataSchema-->

```kotlin
df.append(Person("Bob", 30))
```

<inline-frame src="./resources/appendDataSchema.html" width="100%" height="500px"></inline-frame>

<!---END-->

Without the compiler plugin, a class can implement `DataRowSchema` explicitly:

<!---FUN appendExplicitDataRowSchema-->

```kotlin
data class Person(val name: String, val age: Int) : DataRowSchema

val df = dataFrameOf(Person("Alice", 20))

df.append(Person("Bob", 30))
```

<inline-frame src="./resources/appendExplicitDataRowSchema.html" width="100%" height="500px"></inline-frame>

<!---END-->

An interface can also extend `DataRowSchema` and serve as the schema. In that case, instances of a class
implementing that interface can be appended.

### Append values to a [`ColumnGroup`](DataColumn.md#columngroup)

The following dataframe contains a `name` column group with `firstName` and `lastName` columns:

<!---FUN appendColumnGroupDf-->

```kotlin
columnGroupDf
```

<inline-frame src="./resources/appendColumnGroupDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

#### `List`

Values can be supplied to a `ColumnGroup` as a list:

<!---FUN appendColumnGroupList-->

```kotlin
columnGroupDf.append(listOf("Bob", "Dylan"), 30)
```

<inline-frame src="./resources/appendColumnGroupList.html" width="100%" height="500px"></inline-frame>

<!---END-->

The list is read positionally, using one value for each nested column in the order of the columns in the
`ColumnGroup`. It must contain at least as many values as the group has columns. A shorter list throws
`IndexOutOfBoundsException`; values beyond the number of columns in the group are ignored.

#### [`DataRow`](DataRow.md)

A `DataRow` can be used to supply values to a `ColumnGroup`. Its values are matched to the nested columns by name,
not by position, and must be compatible with the corresponding nested columns:

<!---FUN appendColumnGroupRow-->

```kotlin
val bobRow = dataFrameOf("lastName", "firstName")("Dylan", "Bob")[0]
columnGroupDf.append(bobRow, 30)
```

<inline-frame src="./resources/appendColumnGroupRow.html" width="100%" height="500px"></inline-frame>

<!---END-->

If a nested column's name is absent from the row, `null` is appended to that column:

<!---FUN appendColumnGroupRowWithMissingColumn-->

```kotlin
val bobRow = dataFrameOf("firstName")("Bob")[0]
columnGroupDf.append(bobRow, 30)
```

<inline-frame src="./resources/appendColumnGroupRowWithMissingColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

Columns present in the `DataRow` but not in the `ColumnGroup` are ignored.

#### `null`

When `null` is passed for a `ColumnGroup`, all the nested values become null in the new row. Values for the other
top-level columns are supplied as usual:

<!---FUN appendNullToColumnGroup-->

```kotlin
columnGroupDf.append(null, 30)
```

<inline-frame src="./resources/appendNullToColumnGroup.html" width="100%" height="500px"></inline-frame>

<!---END-->

### Append values to a [`FrameColumn`](DataColumn.md#framecolumn)

The following dataframe contains a `people` frame column. Each nested dataframe contains one person:

<!---FUN appendFrameColumnDf-->

```kotlin
frameColumnDf
```

<inline-frame src="./resources/appendFrameColumnDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

#### [`DataFrame`](DataFrame.md)

Pass a dataframe to place it in the new cell of the `FrameColumn`:

<!---FUN appendFrameColumn-->

```kotlin
val bobDf = dataFrameOf(
    "name" to columnOf("Bob"),
    "age" to columnOf(30),
)
frameColumnDf.append(bobDf)
```

<inline-frame src="./resources/appendFrameColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

#### `null` {id="null-with-framecolumn"}

A `FrameColumn` cannot contain `null` values. If `null` is appended to it, this `null` is represented in the
`FrameColumn` by an empty `DataFrame`. This empty `DataFrame` is created using the schema available from that
`FrameColumn` at the time of the call:

<!---FUN appendNullToFrameColumn-->

```kotlin
frameColumnDf.append(null)
```

<inline-frame src="./resources/appendNullToFrameColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->
