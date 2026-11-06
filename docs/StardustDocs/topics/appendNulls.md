[//]: # (title: appendNulls)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.AppendNullsSamples-->

<web-summary>
Discover the `appendNulls` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `appendNulls` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `appendNulls` operation for Kotlin DataFrame.
</link-summary>

Adds one or more rows filled with `null` values to a [`DataFrame`](DataFrame.md).
Returns a new `DataFrame` containing the existing rows followed by the requested number of rows filled with `null`
values. 

`appendNulls` does not modify the original `DataFrame`. Calling `appendNulls(0)` returns the original
`DataFrame` instance.

**API reference**: [`appendNulls`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/append-nulls.html)

```text
df.appendNulls(numberOfRows: Int = 1): DataFrame
```

Passing a negative `numberOfRows` results in an `IllegalArgumentException`.

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

* [`append`](append.md) — appends rows containing specified values.
* [`concat`](concat.md) — vertically combines dataframes or rows.
* [`fillNulls`](fill.md#fillnulls) — replaces `null` values in existing rows.
* [`duplicateRows`](duplicate.md) — repeats existing rows.

### Parameters

* `numberOfRows: Int = 1` — the number of null-filled rows to append. It must not be negative. The default is `1`.

### Examples

The examples in this section use the following dataframe. Appended rows are highlighted in the outputs:

<!---FUN appendNullsDf-->

```kotlin
df
```

<inline-frame src="./resources/appendNullsDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Omit `numberOfRows` to append one row containing a null value in every column:

<!---FUN appendNullsOneRow-->

```kotlin
df.appendNulls()
```

<inline-frame src="./resources/appendNullsOneRow.html" width="100%" height="500px"></inline-frame>

<!---END-->

Set `numberOfRows` to append several null-filled rows at once:

<!---FUN appendNullsSeveralRows-->

```kotlin
df.appendNulls(numberOfRows = 3)
```

<inline-frame src="./resources/appendNullsSeveralRows.html" width="100%" height="500px"></inline-frame>

<!---END-->

Pass `0` to append no rows:

<!---FUN appendNullsZeroRows-->

```kotlin
df.appendNulls(numberOfRows = 0)
```

<inline-frame src="./resources/appendNullsZeroRows.html" width="100%" height="500px"></inline-frame>

<!---END-->

### `appendNulls` with a [`ColumnGroup`](DataColumn.md#columngroup)

For a `ColumnGroup`, the appended `null` is passed to its nested columns.

The following dataframe contains a `name` column group with `firstName` and `lastName` columns:

<!---FUN appendNullsColumnGroupDf-->

```kotlin
columnGroupDf
```

<inline-frame src="./resources/appendNullsColumnGroupDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

`appendNulls()` adds a row in which nested and top-level values are `null`:

<!---FUN appendNullsColumnGroup-->

```kotlin
columnGroupDf.appendNulls()
```

<inline-frame src="./resources/appendNullsColumnGroup.html" width="100%" height="500px"></inline-frame>

<!---END-->

### `appendNulls` with a [`FrameColumn`](DataColumn.md#framecolumn)

For a `FrameColumn`, an appended `null` is represented by an empty `DataFrame` created using the schema available
from that `FrameColumn` at the time of the call.

The following dataframe contains a `people` `FrameColumn`. Each nested dataframe contains one person:

<!---FUN appendNullsFrameColumnDf-->

```kotlin
frameColumnDf
```

<inline-frame src="./resources/appendNullsFrameColumnDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

The new `FrameColumn` cell contains an empty `DataFrame` with the schema available from the `FrameColumn`:

<!---FUN appendNullsFrameColumn-->

```kotlin
frameColumnDf.appendNulls()
```

<inline-frame src="./resources/appendNullsFrameColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->
