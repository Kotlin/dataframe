[//]: # (title: duplicate)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DuplicateSamples-->

Repeats rows or whole dataframes:
[`duplicateRows`](#duplicaterows) repeats rows inside a [`DataFrame`](DataFrame.md),
[`duplicate`](#duplicate-on-a-datarow) on a [`DataRow`](DataRow.md) makes a [`DataFrame`](DataFrame.md) out of copies
of the row, and [`duplicate`](#duplicate-on-a-dataframe) on a [`DataFrame`](DataFrame.md) makes a
[`FrameColumn`](DataColumn.md#framecolumn) out of copies of the whole dataframe.

**Related operations**: [](appendDuplicate.md)

The number of copies `n` counts the original too, so `n = 1` gives one copy.
`n` must be greater than 0.
For `n = 0` or a negative `n`, every `duplicate` and `duplicateRows` call throws an `IllegalArgumentException`.

Every example on this page uses the same [`DataFrame`](DataFrame.md):

<!---FUN duplicateDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/duplicateDf.html" width="100%" height="500px"></inline-frame>

## duplicateRows

Returns a [`DataFrame`](DataFrame.md) where every row is repeated `n` times.
The copies of a row come right after it, so the order of the rows stays the same.
The examples use the `df` shown at the top of this page.

```text
duplicateRows(n): DataFrame
duplicateRows(n) { rowCondition }: DataFrame
```

<!---FUN duplicateRows-->

```kotlin
df.duplicateRows(3)
```

<!---END-->
<inline-frame src="./resources/duplicateRows.html" width="100%" height="500px"></inline-frame>

With a [row condition](DataRow.md#row-conditions), only the rows that match it are repeated.
The other rows appear once, as before:

<!---FUN duplicateRowsWhere-->
<tabs>
<tab title="Properties">

```kotlin
df.duplicateRows(3) { age > 18 }
```

</tab>
<tab title="Strings">

```kotlin
df.duplicateRows(3) { "age"<Int>() > 18 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/duplicateRowsWhere_properties.html" width="100%" height="500px"></inline-frame>

Values in column groups and frame columns are repeated together with their rows.
The column names and types stay the same.

Don't confuse `duplicateRows` with [`distinct`](distinct.md), which removes repeated rows.

## duplicate on a DataRow

Returns a [`DataFrame`](DataFrame.md) with `n` rows, each of them a copy of the row.
The example uses the `df` shown at the top of this page.

```text
DataRow.duplicate(n): DataFrame
```

<!---FUN duplicateRow-->

```kotlin
df[1].duplicate(3)
```

<!---END-->
<inline-frame src="./resources/duplicateRow.html" width="100%" height="500px"></inline-frame>

The result has the same columns as the dataframe the row comes from,
column groups and frame columns included.
The type of each column follows the value in the row:
a nullable column whose value in the row is not `null` becomes non-nullable in the result.
Here `name` is `String?` in `people`, and `String` in the copies of the first row:

<!---FUN duplicateRowNullable-->

```kotlin
val people = dataFrameOf(
    "name" to listOf("Alice", null),
    "age" to listOf(15, 20),
)
people[0].duplicate(2).schema()
```

Output:

```text
name: String
age: Int
```

<!---END-->

## duplicate on a DataFrame

Returns a [`FrameColumn`](DataColumn.md#framecolumn) with `n` cells, each holding the whole dataframe.
The column has an empty name.
The example uses the `df` shown at the top of this page.

```text
DataFrame.duplicate(n): FrameColumn
```

<!---FUN duplicateDataFrame-->

```kotlin
df.duplicate(3)
```

<!---END-->
<inline-frame src="./resources/duplicateDataFrame.html" width="100%" height="500px"></inline-frame>

A dataframe gives a column without a name the name `untitled`, so the table above shows it this way.

To get one [`DataFrame`](DataFrame.md) with all rows of `df`, followed by all rows of `df` again,
[`concat`](concat.md) the copies:

<!---FUN duplicateDataFrameConcat-->

```kotlin
df.duplicate(2).concat()
```

<!---END-->
<inline-frame src="./resources/duplicateDataFrameConcat.html" width="100%" height="500px"></inline-frame>

**See also**: [`append`](append.md), [`concat`](concat.md), [`distinct`](distinct.md).
