[//]: # (title: map)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Computes a new value for every value, row, or key–group pair of the receiver, and collects the results
into a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/),
a [`DataFrame`](DataFrame.md), a [`DataColumn`](DataColumn.md),
or a [`FrameColumn`](DataColumn.md#framecolumn).

**Related operations**: [](addRemove.md)

All `map` operations share the name but differ in what they go over and what they give back:

| Operation | Goes over | Returns |
|-----------|-----------|---------|
| [`map`](#map) | rows of a [`DataFrame`](DataFrame.md) | `List` |
| [`mapToColumn`](#maptocolumn) | rows of a [`DataFrame`](DataFrame.md) | a single [`DataColumn`](DataColumn.md) |
| [`mapToFrame`](#maptoframe) | rows of a [`DataFrame`](DataFrame.md) | a new [`DataFrame`](DataFrame.md) |
| [`map`](#map-on-datacolumn) / [`mapIndexed`](#map-on-datacolumn) | values of a [`DataColumn`](DataColumn.md) | a [`DataColumn`](DataColumn.md) of the same size |
| [`map`](#map-on-groupby) | key–group pairs of a [`GroupBy`](groupBy.md) | `List` |
| [`mapToRows`](#map-on-groupby) | key–group pairs of a [`GroupBy`](groupBy.md) | a [`DataFrame`](DataFrame.md) |
| [`mapToFrames`](#map-on-groupby) | key–group pairs of a [`GroupBy`](groupBy.md) | a [`FrameColumn`](DataColumn.md#framecolumn) |

Each result keeps the order of the values, rows, or key–group pairs it was computed from.

## map

Maps the rows of a [`DataFrame`](DataFrame.md) into a `List` with one element per row.

```text
map { rowExpression }: List<T>

rowExpression: DataRow.(DataRow) -> Value
```

<!---FUN map-->

```kotlin
df.map { 2021 - it.age }
```

<!---END-->

See [row expressions](DataRow.md#row-expressions)

A [`ColumnGroup`](DataColumn.md#columngroup) is also a [`DataFrame`](DataFrame.md), so `map` on a column group is
this operation: it goes over the rows of the group and returns a `List`.
To get a [`DataColumn`](DataColumn.md) of the same size instead — a column of the rows of the group —
call `asDataColumn()` on the group first, and then [`map`](#map-on-datacolumn).

## mapToColumn

Maps the rows of a [`DataFrame`](DataFrame.md) into a single new [`DataColumn`](DataColumn.md) with one value per row.

```text
mapToColumn(columnName) { rowExpression }: DataColumn

rowExpression: DataRow.(DataRow) -> Value
```

<!---FUN mapToColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.mapToColumn("year of birth") { 2021 - age }
```

</tab>
<tab title="Strings">

```kotlin
df.mapToColumn("year of birth") { 2021 - "age"<Int>() }
```

</tab></tabs>
<!---END-->

See [row expressions](DataRow.md#row-expressions)

The new column is standalone: the original [`DataFrame`](DataFrame.md) is not changed and does not contain it.
Use [`add`](add.md) to get a [`DataFrame`](DataFrame.md) with the new column in it.

Inside the row expression, `prev()?.newValue()` gives the value already computed for the preceding row —
`null` for the first row, where there is no preceding one. This is how running totals and other
recurrences are expressed; see [`add`](add.md).

## mapToFrame

Maps the rows of a [`DataFrame`](DataFrame.md) into a new [`DataFrame`](DataFrame.md) made of the described columns.

```text
mapToFrame { 
    columnMapping
    columnMapping
    ...
} : DataFrame

columnMapping = column into columnName | columnName from column | columnName from { rowExpression } | +column  
```

<!---FUN mapMany-->
<tabs>
<tab title="Properties">

```kotlin
df.mapToFrame {
    "year of birth" from { 2021 - age }
    expr { age > 18 } into "is adult"
    name.lastName.map { it.length } into "last name length"
    "full name" from { name.firstName + " " + name.lastName }
    +city
}
```

</tab>
<tab title="Strings">

```kotlin
df.mapToFrame {
    "year of birth" from { 2021 - "age"<Int>() }
    expr { "age"<Int>() > 18 } into "is adult"
    "name"["lastName"]<String>().map { it.length } into "last name length"
    "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    +"city"
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mapMany.html" width="100%"/>
<!---END-->

The result holds **only** the described columns, in the order in which they are described.
This is what makes `mapToFrame` different from [`add`](add.md), where the columns of the original
[`DataFrame`](DataFrame.md) are part of the result as well.
In the example above, `city` is in the result only because of the `+city` line,
and `name` and `age` are not in it at all.

## map on DataColumn

Maps the values of a [`DataColumn`](DataColumn.md) into a new [`DataColumn`](DataColumn.md) of the same size.
`mapIndexed` gives the position of the value as well, starting at `0`.

```text
map { value -> newValue }: DataColumn
map(type) { value -> newValue }: DataColumn

mapIndexed { index, value -> newValue }: DataColumn
mapIndexed(type) { index, value -> newValue }: DataColumn
```

<!---FUN mapOnColumn-->

```kotlin
// A column of last name lengths; it keeps the name of the original column,
// so it is renamed here
df.name.lastName.map { it.length }.rename("lastNameLength")
```

<!---END-->

The new column has the same name as the original one, so it is usually renamed on the spot
or given a name by the operation it is passed to.

`mapIndexed` also gives the position of the value:

<!---FUN mapIndexedOnColumn-->

```kotlin
// "1. Alice", "2. Bob", ...
df.name.firstName.mapIndexed { i, firstName -> "${i + 1}. $firstName" }
```

<!---END-->

Which kind of column you get follows the type of the new column — the type argument, or the `type` given
explicitly — and not the computed values:
a [`DataFrame`](DataFrame.md) type gives a [`FrameColumn`](DataColumn.md#framecolumn),
a [`DataRow`](DataRow.md) type gives a [`ColumnGroup`](DataColumn.md#columngroup),
and any other type gives a [`ValueColumn`](DataColumn.md#valuecolumn).
A nullable [`DataFrame`](DataFrame.md) type belongs to the last group, because a
[`FrameColumn`](DataColumn.md#framecolumn) cannot hold `null`.

`infer` only concerns a [`ValueColumn`](DataColumn.md#valuecolumn): it decides whether the type of that
column is the given one as it is, or the type of the computed values. For a
[`ColumnGroup`](DataColumn.md#columngroup) and a [`FrameColumn`](DataColumn.md#framecolumn) it changes nothing.

The overloads with an explicit `type` are for the cases where the type of the new column is only known
at runtime. The computed values are put into the column as they are, without any conversion, so the type
has to fit them: a [`ValueColumn`](DataColumn.md#valuecolumn) can never have a
[`DataFrame`](DataFrame.md) type, and a call that would give it one fails with an `IllegalArgumentException`.

## map on GroupBy

Maps the key–group pairs of a [`GroupBy`](groupBy.md): every row of a `GroupBy` is one key–group pair —
the key values, and the group of rows that belongs to them (see [`groupBy`](groupBy.md)).

```text
map { GroupWithKey -> value }: List<Value>
mapToRows { GroupWithKey -> DataRow? }: DataFrame
mapToFrames { GroupWithKey -> DataFrame }: FrameColumn
```

The lambda receives the pair as a `GroupWithKey`, both as the receiver and as the argument, so the key values
are available as `key` (a [`DataRow`](DataRow.md)) and the rows of the group as `group`
(a [`DataFrame`](DataFrame.md)).

<!---FUN mapOnGroupBy-->

```kotlin
// The number of people per city, as a list, in the order of the groups: [1, 1, 2, 1, 1, 1]
df.groupBy { city }.map { group.rowsCount() }
```

<!---END-->

<!---FUN mapToRowsOnGroupBy-->

```kotlin
// The oldest person of every city, one row per city
df.groupBy { city }.mapToRows { group.sortByDesc { age }.firstOrNull() }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mapToRowsOnGroupBy.html" width="100%"/>
<!---END-->

<!---FUN mapToFramesOnGroupBy-->

```kotlin
// The two oldest people with each first name, as a frame column:
// only the group of "Charlie" has a third person to leave out
df.groupBy { name.firstName }.mapToFrames { group.sortByDesc { age }.take(2) }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mapToFramesOnGroupBy.html" width="100%"/>
<!---END-->

`mapToFrames` names the new column after [`GroupBy.groups`](groupBy.md) (`"group"` by default);
call `concat()` on it to get all of those dataframes back as one [`DataFrame`](DataFrame.md).
[`concatWithKeys`](groupBy.md#concatwithkeys-on-groupby) is built exactly this way.

> `map` and `mapToRows` leave out the pairs for which the lambda returns `null`,
> so their results can be shorter than the number of key–group pairs.
> {style="note"}
