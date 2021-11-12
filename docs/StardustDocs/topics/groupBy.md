[//]: # (title: groupBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits the rows of `DataFrame` into groups using one or several columns as grouping keys.

```kotlin
groupBy { columns }
    [transformations]
    [aggregations]
```

See [column selectors](ColumnSelectors.md), [groupBy transformations](#groupeddataframe) and [groupBy aggregations](aggregateGroupBy.md)

<!---FUN groupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name }
df.groupBy { city and name.lastName }
df.groupBy { age / 10 named "ageDecade" }
df.groupBy { expr { name.firstName.length + name.lastName.length } named "nameLength" }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val lastName by name.column<String>()
val firstName by name.column<String>()
val age by column<Int>()
val city by column<String?>()

df.groupBy { name }
// or
df.groupBy(name)

df.groupBy { city and lastName }
// or
df.groupBy(city, lastName)

df.groupBy { age / 10 named "ageDecade" }

df.groupBy { expr { firstName().length + lastName().length } named "nameLength" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("name")
df.groupBy { "city" and "name"["lastName"] }
df.groupBy { "age".ints() / 10 named "ageDecade" }
df.groupBy { expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named "nameLength" }
```

</tab></tabs>
<!---END-->

Returns `GroupedDataFrame` object.

### GroupedDataFrame

`GroupedDataFrame` is a `DataFrame` with one chosen [`FrameColumn`](DataColumn.md#framecolumn) containing data groups.

It supports the following operations:
* [`add`](add.md)
* [`sortBy`](sortBy.md)
* [`map`](map.md)

Any `DataFrame` with `FrameColumn` can be reinterpreted as `GroupedDataFrame`:

<!---FUN dataFrameToGrouped-->

```kotlin
val key by columnOf(1, 2) // create int column with name "key"
val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
val df = dataFrameOf(key, data) // create dataframe with two columns

df.asGroupedDataFrame { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
```

<!---END-->

And any `GroupedDataFrame` can be reinterpreted as `DataFrame` with `FrameColumn`:

<!---FUN groupedDataFrameToFrame-->

```kotlin
df.groupBy { city }.toDataFrame()
```

<!---END-->

Use [`concat`](concat.md) to union all data groups of `GroupedDataFrame` into original `DataFrame` preserving new order of rows produced by grouping:

<!---FUN concatGroupedDataFrame-->

```kotlin
df.groupBy { name }.concat()
```

<!---END-->

To compute one or several aggregation statistics over `GroupedDataFrame` see [GroupBy aggregation](aggregateGroupBy.md)
