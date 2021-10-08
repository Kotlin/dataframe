[//]: # (title: Analyze)

<!---IMPORT docs.api.Analyze-->

## Basic statistics

<!---FUN basicInfo-->

```kotlin
df.nrow() // number of rows
df.ncol() // number of columns
df.schema() // schema of columns
```

<!---END-->

To count number of rows that satisfy to [condition](rows.md#row-conditions) use `count`:

<!---FUN count-->

```kotlin
df.count { age > 15 }
```

<!---END-->

### Single column statistics

* `sum` and `mean` are available for numeric columns
* `min`, `max` and `median` are available for comparable columns

<!---FUN columnStats-->
<tabs>
<tab title="Properties">

```kotlin
df.sum { weight }
df.min { age }
df.mean { age }
df.median { age }

df.weight.sum()
df.age.max()
df.age.mean()
df.age.median()
```

</tab>
<tab title="Accessors">

```kotlin
val weight by column<Int?>()
val age by column<Int>()

df.sum { weight }
df.min { age }
df.mean { age }
df.median { age }

df.sum(weight)
df.min(age)
df.mean(age)
df.median(age)

df[weight].sum()
df[age].mean()
df[age].min()
df[age].median()
```

</tab>
<tab title="Strings">

```kotlin
df.sum("weight")
df.min("age")
df.mean("age")
df.median("age")
```

</tab></tabs>
<!---END-->

### Multiple columns statistics

When several columns are specified, statistical operations compute a single value across all given columns  

<!---FUN multipleColumnsStat-->
<tabs>
<tab title="Properties">

```kotlin
df.min { intCols() }
df.max { name.firstName and name.lastName }
df.sum { age and weight }
df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()

df.min { intCols() }

df.max { firstName and lastName }
// or
df.max(firstName, lastName)

df.sum { age and weight }
// or
df.sum(age, weight)

df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab>
<tab title="Strings">

```kotlin
df.min { intCols() }

df.max { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

df.sum("age", "weight")
// or
df.sum { "age"().asNumbers() and "weight"().asNumbers() }

df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab></tabs>
<!---END-->

To compute statistics separately for every column, use operations with `-for` suffix:

<!---FUN columnsFor-->
<tabs>
<tab title="Properties">

```kotlin
df.minFor { intCols() }
df.maxFor { name.firstName and name.lastName }
df.sumFor { age and weight }
df.meanFor { cols(1, 3).asNumbers() }
df.medianFor { name.cols().asComparable() }
```

</tab>
<tab title="Strings">

```kotlin
df.minFor { intCols() }
df.maxFor { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

df.sumFor("age", "weight")
// or
df.sumFor { "age"().asNumbers() and "weight"().asNumbers() }

df.meanFor { cols(1, 3).asNumbers() }
df.medianFor { name.cols().asComparable() }
```

</tab></tabs>
<!---END-->

### Row expression statistics

To compute statistics for some expression evaluated for every row, you should use operations with `-of` suffix:

<!---FUN ofExpressions-->
<tabs>
<tab title="Properties">

```kotlin
df.minOf { 2021 - age }
df.maxOf { name.firstName.length + name.lastName.length }
df.sumOf { weight?.let { it - 50 } }
df.meanOf { Math.log(age.toDouble()) }
df.medianOf { city?.length }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()
val city by column<String?>()

df.minOf { 2021 - age() }
df.maxOf { firstName().length + lastName().length }
df.sumOf { weight()?.let { it - 50 } }
df.meanOf { Math.log(age().toDouble()) }
df.medianOf { city()?.length }
```

</tab>
<tab title="Strings">

```kotlin
df.minOf { 2021 - "age"<Int>() }
df.maxOf { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length }
df.sumOf { "weight"<Int?>()?.let { it - 50 } }
df.meanOf { Math.log("age"<Int>().toDouble()) }
df.medianOf { "city"<String?>()?.length }
```

</tab></tabs>
<!---END-->

## GroupBy

A `groupBy` operation is used to split rows of `DataFrame` into groups using one or several columns as grouping keys.

<!---FUN groupBy-->

### Aggregations

`groupBy` returns `GroupedDataFrame`, that can be aggregated into `DataFrame` with one or several [statistics](#basic-statistics). Every data group will be passed to the body of `aggregate` function as a receiver of type `DataFrame`

<!---FUN groupByAggregations-->

If only one aggregation function is used, column name for aggregation result can be omitted. In this case default column name `aggregated` will be used for aggregation result.

<!---FUN groupByAggregateWithoutInto-->

Most common aggregation functions can be computed directly at `GroupedDataFrame`:

<!---FUN groupByDirectAggregations-->

To get all column values for every group without aggregation use `values` function. 
For [ValueColumn](columns.md#valuecolumn) of type `T` it will gather group values into lists of type `Many<T>`
For [ColumnGroup](columns.md#columngroup) it will gather group values into `DataFrame` and convert [ColumnGroup](columns.md#columngroup) into [FrameColumn](columns.md#framecolumn)

<!---FUN groupByWithoutAggregation-->

### GroupedDataFrame/DataFrame conversions

Under the hood `GroupedDataFrame` is just a `DataFrame` with one chosen [`FrameColumn`](columns.md#framecolumn) containing data groups.
Therefore any `DataFrame` with `FrameColumn` can be interpreted as `GroupedDataFrame`:

<!---FUN dataFrameToGrouped-->

```kotlin
val key by columnOf(1, 2) // create int column with name "key"
val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
val df = key + data // create dataframe with two columns
df.toGrouped { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
```

<!---END-->

Any `GroupedDataFrame` can also be interpreted as `DataFrame`:

<!---FUN groupedDataFrameToFrame-->

[Union](mix.md#union) operation unions all groups of `GroupedDataFrame` into single `DataFrame`. This operation is reverse to `groupBy`: it will return original `DataFrame` with reordered rows according to grouping keys

<!---FUN groupByUnion-->

## Pivot

`pivot` operation reshapes `DataFrame` by grouping data into new columns based on key values:

<!---FUN pivot-->

### Aggregation



### `pivot` inside `aggregate`
[pivot](#pivot) operation can also be used within [aggregate](#aggregate) with a slightly different syntax

## Working with `GroupedDataFrame`

**Input**

name|city|date
---|---|---
Alice|London|2020-10-01
Bob|Paris|2020-10-02
Alice|Paris|2020-10-03
Alice|London|2020-10-04
Bob|Milan|2020-10-05

```kotlin
df.groupBy { name }.aggregate {
    pivot { city }.max { date }
}
```
or
```kotlin
df.groupBy { name }.pivot { city }.max { date }
```
**Output**

name|London last visit|Paris last visit|Milan last visit
---|---|---|---
Alice|2020-10-04|2020-10-03|null
Bob|null|2020-10-02|2020-10-05

**Input**

name|city|date
---|---|---
Alice|London|2020-10-01
Bob|Paris|2020-10-02
Alice|Paris|2020-10-03
Alice|London|2020-10-04
Bob|Milan|2020-10-05

```kotlin
df.groupBy { name }.aggregate {
    countBy { city } into { it }
}
```
or
```kotlin
df.groupBy { name }.countBy { city }
```

**Output**

name|London|Paris|Milan
---|---|---|---
Alice|2|1|0
Bob|0|1|1
