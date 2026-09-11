[//]: # (title: appendNulls)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Append-->

Returns a new [`DataFrame`](DataFrame.md) containing the existing rows followed by the requested number of rows
filled with `null` values.

```kotlin
fun <T> DataFrame<T>.appendNulls(numberOfRows: Int = 1): DataFrame<T>
```

**Parameters:**

`numberOfRows` defaults to `1` and must not be negative.
When `numberOfRows` is `0`, it returns the original dataframe instance.

`appendNulls` does not modify the original dataframe.

**Related operations**
* [`append`](append.md) — appends rows containing specified values;
* [`concat`](concat.md) — vertically combines dataframes or rows;
* [`fillNulls`](fill.md#fillnulls) — replaces `null` values in existing rows;
* [`duplicate`](duplicate.md) — repeats existing rows.

## Basic usage

The examples in this section use the following dataframe:

<!---FUN appendDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/appendDf.html" width="100%" height="500px"></inline-frame>

Omit `numberOfRows` to append one row containing a null value in every column:

<!---FUN appendOneNullRow-->

```kotlin
df.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendOneNullRow.html" width="100%" height="500px"></inline-frame>

Set `numberOfRows` to append several null-filled rows at once:

<!---FUN appendSeveralNullRows-->

```kotlin
df.appendNulls(numberOfRows = 3)
```

<!---END-->
<inline-frame src="./resources/appendSeveralNullRows.html" width="100%" height="500px"></inline-frame>

Passing `0` is a no-op and returns the original dataframe instance:

<!---FUN appendZeroNullRows-->

```kotlin
df.appendNulls(numberOfRows = 0)
```

<!---END-->
<inline-frame src="./resources/appendZeroNullRows.html" width="100%" height="500px"></inline-frame>

The returned dataframe contains the additional row, while the original dataframe remains unchanged:

<!---FUN appendNullsDoesNotModifyOriginal-->

```kotlin
val withNullRow = df.appendNulls()

df // the original dataframe still contains only Alice
```

<!---END-->
<inline-frame src="./resources/appendNullsDoesNotModifyOriginal.html" width="100%" height="500px"></inline-frame>

## `appendNulls` with a [`ColumnGroup`](DataColumn.md#columngroup)

For a [`ColumnGroup`](DataColumn.md#columngroup), the appended `null` is passed to its nested columns.

Consider the following example:

<!---FUN columnGroupDf-->

```kotlin
columnGroupDf
```

<!---END-->
<inline-frame src="./resources/columnGroupDf.html" width="100%" height="500px"></inline-frame>

`appendNulls()` adds a row in which nested and top-level values are `null`:

<!---FUN appendNullsColumnGroup-->

```kotlin
columnGroupDf.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendNullsColumnGroup.html" width="100%" height="500px"></inline-frame>

## `appendNulls` with a [`FrameColumn`](DataColumn.md#framecolumn)

For a [`FrameColumn`](DataColumn.md#framecolumn), an appended `null` is represented
by an empty [`DataFrame`](DataFrame.md) created using the schema available
from that [`FrameColumn`](DataColumn.md#framecolumn) at the time of the call.

Consider the following example:

<!---FUN frameColumnDf-->

```kotlin
frameColumnDf
```

<!---END-->
<inline-frame src="./resources/frameColumnDf.html" width="100%" height="500px"></inline-frame>

The new [`FrameColumn`](DataColumn.md#framecolumn) cell contains an empty [`DataFrame`](DataFrame.md) with the schema
taken from the [`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN appendNullsFrameColumn-->

```kotlin
frameColumnDf.appendNulls()
```

<!---END-->
<inline-frame src="./resources/appendNullsFrameColumn.html" width="100%" height="500px"></inline-frame>
