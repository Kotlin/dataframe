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
df.groupBy { "city"() and "name"["lastName"] }
df.groupBy { "age".ints() / 10 named "ageDecade" }
df.groupBy { expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named "nameLength" }
```

</tab></tabs>
<!---END-->

### Aggregations

`groupBy` returns `GroupedDataFrame`, that can be aggregated into `DataFrame` with one or several [statistics](#basic-statistics). Every data group will be passed to the body of `aggregate` function as a receiver of type `DataFrame`

<!---FUN groupByAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate {
    nrow() into "total"
    count { age > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age }.name into "oldest"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val name by columnGroup()

df.groupBy { city }.aggregate {
    nrow() into "total"
    count { age() > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age() }[name] into "name of oldest"
}
// or
df.groupBy(city).aggregate {
    nrow() into "total"
    count { age > 18 } into "adults"
    median(age) into "median age"
    min(age) into "min age"
    maxBy(age)[name] into "name of oldest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate {
    nrow() into "total"
    count { "age"<Int>() > 18 } into "adults"
    median("age") into "median age"
    min("age") into "min age"
    maxBy("age")["name"] into "oldest"
}
```

</tab></tabs>
<!---END-->

If only one aggregation function is used, column name for aggregation result can be omitted. In this case default column name `aggregated` will be used for aggregation result.

<!---FUN groupByAggregateWithoutInto-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate { maxBy { age }.name }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val name by columnGroup()

df.groupBy { city }.aggregate { maxBy { age() }[name] }
// or
df.groupBy(city).aggregate { maxBy(age)[name] }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate { maxBy("age")["name"] }
```

</tab></tabs>
<!---END-->

Most common aggregation functions can be computed directly at `GroupedDataFrame`:

<!---FUN groupByDirectAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.max() // max for every comparable column
df.groupBy { city }.mean() // mean for every numeric column
df.groupBy { city }.max { age } // max age into column "age"
df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
df.groupBy { city }.count() // number of rows into column "count"
df.groupBy { city }.max { name.firstName.length() and name.lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }.medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }.minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy { city }.meanOf("mean ratio") { weight?.div(age) } // mean of weight/age into column "mean ratio"
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

df.groupBy { city }.max() // max for every comparable column
df.groupBy { city }.mean() // mean for every numeric column
df.groupBy { city }.max { age } // max age into column "age"
df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
df.groupBy { city }.count() // number of rows into column "count"
df.groupBy { city }.max { firstName.length() and lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }.medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }.minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy { city }.meanOf("mean ratio") { weight()?.div(age()) } // mean of weight/age into column "mean ratio"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").max() // max for every comparable column
df.groupBy("city").mean() // mean for every numeric column
df.groupBy("city").max("age") // max age into column "age"
df.groupBy("city").sum("weight", name = "total weight") // sum of weights into column "total weight"
df.groupBy("city").count() // number of rows into column "count"
df.groupBy("city").max { "name"["firstName"].strings().length() and "name"["lastName"].strings().length() } // maximum length of firstName or lastName into column "max"
df.groupBy("city").medianFor("age", "weight") // median age into column "age", median weight into column "weight"
df.groupBy("city").minFor { ("age".ints() into "min age") and ("weight".intOrNulls() into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy("city").meanOf("mean ratio") { "weight".intOrNull()?.div("age".int()) } // mean of weight/age into column "mean ratio"
```

</tab></tabs>
<!---END-->

To get all column values for every group without aggregation use `values` function. 
For [ValueColumn](columns.md#valuecolumn) of type `T` it will gather group values into lists of type `Many<T>`
For [ColumnGroup](columns.md#columngroup) it will gather group values into `DataFrame` and convert [ColumnGroup](columns.md#columngroup) into [FrameColumn](columns.md#framecolumn)

<!---FUN groupByWithoutAggregation-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.values()
df.groupBy { city }.values { name and age }
df.groupBy { city }.values { weight into "weights" }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()

df.groupBy(city).values()
df.groupBy(city).values(name, age)
df.groupBy(city).values { weight into "weights" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").values()
df.groupBy("city").values("name", "age")
df.groupBy("city").values { "weight"() into "weights" }
```

</tab></tabs>
<!---END-->

### GroupedDataFrame/DataFrame conversions

Under the hood `GroupedDataFrame` is just a `DataFrame` with one chosen [`FrameColumn`](columns.md#framecolumn) containing data groups.
Therefore any `DataFrame` with `FrameColumn` can be interpreted as `GroupedDataFrame`:

<!---FUN dataFrameToGrouped-->

```kotlin
val key by columnOf(1, 2) // create int column with name "key"
val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
val df = dataFrameOf(key, data) // create dataframe with two columns
df.asGrouped { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
```

<!---END-->

Any `GroupedDataFrame` can also be interpreted as `DataFrame`:

<!---FUN groupedDataFrameToFrame-->

```kotlin
val grouped = df.groupBy { city } // create GroupedDataFrame
grouped.asDataFrame() // convert GroupedDataFrame to DataFrame with string column "city" and frame column "group"
```

<!---END-->

[Union](mix.md#union) operation unions all groups of `GroupedDataFrame` into single `DataFrame`. This operation is reverse to `groupBy`: it will return original `DataFrame` with reordered rows according to grouping keys

<!---FUN groupByUnion-->

```kotlin
df.groupBy { city }.union()
```

<!---END-->

## Pivot

`pivot` operation reshapes `DataFrame` by grouping data into new columns based on key values:

<!---FUN pivot-->

```kotlin
df.pivot { city }
```

<!---END-->

### Aggregation

### `pivot` inside `aggregate`
[pivot](#pivot) operation can also be used within `aggregate` with a slightly different syntax

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
