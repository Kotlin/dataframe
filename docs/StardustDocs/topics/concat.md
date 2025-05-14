[//]: # (title: concat)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns a [`DataFrame`](DataFrame.md) with the union of rows from several given [`DataFrame`](DataFrame.md) objects.

`concat` is available for:

[`DataFrame`](DataFrame.md):

<!---FUN concatDataFrames-->

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

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.concatGroupBy.html" width="100%"/>
<!---END-->

[`FrameColumn`](DataColumn.md#framecolumn):

<!---FUN concatFrameColumn-->

```kotlin
val x = dataFrameOf("a", "b")(
    1, 2,
    3, 4,
)
val y = dataFrameOf("b", "c")(
    5, 6,
    7, 8,
)
val frameColumn by columnOf(x, y)
frameColumn.concat()
```

<!---END-->

If you want to take the union of columns (not rows) from several [`DataFrame`](DataFrame.md) objects, see [`add`](add.md).

## Schema unification

If input [`DataFrame`](DataFrame.md) objects have different schemas, every column in the resulting [`DataFrame`](DataFrame.md) 
will get the lowest common type of the original columns with the same name. 

For example, if one [`DataFrame`](DataFrame.md) has a column `A: Int` and another [`DataFrame`](DataFrame.md) has a column `A: Double`, 
the resulting [`DataFrame`](DataFrame.md) will have a column `A: Number`.

Missing columns in [`DataFrame`](DataFrame.md) objects will be filled with `null`.
