[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with the union of rows from several given [`DataFrames`](DataFrame.md).

`concat` is available for:

[`DataFrame`](DataFrame.md):

<!---FUN concatDfs-->

```kotlin
df.concat(df1, df2)
```

<!---END-->

[`DataColumn`](DataColumn.md):

<!---FUN concatColumns-->

```kotlin
val a by columnOf(1, 2)
val b by columnOf(3, 4)
a.concat(b)
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

`Iterable<DataColumn>`:

<!---FUN concatColumnsIterable-->

```kotlin
val a by columnOf(1, 2)
val b by columnOf(3, 4)
listOf(a, b).concat()
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
val x = dataFrameOf("a", "b")(
    1, 2,
    3, 4
)
val y = dataFrameOf("b", "c")(
    5, 6,
    7, 8
)
val frameColumn by columnOf(x, y)
frameColumn.concat()
```

<!---END-->

If you want to union columns (not rows) from several [`DataFrames`](DataFrame.md), see [`add`](add.md).

## Schema unification

If input [`DataFrames`](DataFrame.md) have different schemas, every column in resulting [`DataFrames`](DataFrame.md) will have the most common type of the original columns with the same name. 

For example, if one [`DataFrame`](DataFrame.md) has column `A: Int` and other [`DataFrame`](DataFrame.md) has column `A: Double`, resulting `DataFrame` will have column `A: Number`.

Missing columns in dataframes will be filled with `null`.
