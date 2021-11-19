[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` with the union of rows from several given DataFrames.

To union columns instead of rows, see [`add`](add.md)

<!---FUN concat-->

```kotlin
df.concat(otherDf)
```

<!---END-->

### Concat several DataFrames

`concat` is available for:

* `Iterable<DataFrame>`:

<!---FUN concatIterable-->

```kotlin
listOf(df[0..1], df[4..5]).concat()
```

<!---END-->

* `Iterable<DataRow>`:

<!---FUN concatRows-->

```kotlin
val rows = listOf(df[2], df[4], df[5])
rows.concat()
```

<!---END-->

* `GroupBy`:

<!---FUN concatGroupBy-->

```kotlin
df.groupBy { name }.concat()
```

<!---END-->

* [`FrameColumn`](DataColumn.md#framecolumn):z

<!---FUN concatFrameColumn-->

```kotlin
val frameColumn by columnOf(df[0..1], df[4..5])
frameColumn.concat()
```

<!---END-->

## Schema unification

If input DataFrames have different schemas, every column in resulting `DataFrame` will have the most common type of the original columns with the same name. 

For example, if one `DataFrame` has column `A: Int` and other `DataFrame` has column `A: Double`, resulting `DataFrame` will have column `A: Number`.

Missing columns in DataFrames will be filled with `null`.
