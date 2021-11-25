[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` with the union of rows from several given `DataFrames`.

`concat` is available for:

`DataFrame`:

<!---FUN concatDfs-->

```kotlin
df.concat(df1, df2)
```

<!---END-->

`Iterable<DataFrame>`:

<!---FUN concatIterable-->

```kotlin
listOf(df1, df2).concat()
```

<!---END-->

`Iterable<DataRow>`:

<!---FUN concatRows-->

```kotlin
val rows = listOf(df[2], df[4], df[5])
rows.concat()
```

<!---END-->

[`groupBy`](groupBy.md#transformation):

<!---FUN concatGroupBy-->

```kotlin
df.groupBy { name }.concat()
```

<!---END-->

[`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN concatFrameColumn-->

```kotlin
val frameColumn by columnOf(df[0..1], df[4..5])
frameColumn.concat()
```

<!---END-->

If you want to union columns (not rows) from several `DataFrames`, see [`add`](add.md).

## Schema unification

If input `DataFrames` have different schemas, every column in resulting `DataFrame` will have the most common type of the original columns with the same name. 

For example, if one `DataFrame` has column `A: Int` and other `DataFrame` has column `A: Double`, resulting `DataFrame` will have column `A: Number`.

Missing columns in dataframes will be filled with `null`.
