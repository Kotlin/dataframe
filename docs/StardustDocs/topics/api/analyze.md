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

A `groupBy` operation is used to split rows of `DataFrame` and group them vertically using one or several columns as grouping keys.

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

`groupBy` returns `GroupedDataFrame` which is `DataFrame` with one chosen [`FrameColumn`](columns.md#framecolumn) containing data groups.

### Aggregations

To compute one or several [statistics](#basic-statistics) per every group of `GroupedDataFrame` use `aggregate` function. Its body will be executed for every data group and has a receiver of type `DataFrame` that represents current data group being aggregated.
To add new column to the resulting `DataFrame`, pass the name of new column to infix function `into`:

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

If only one aggregation function is used, column name can be omitted:

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
df.groupBy { city }
    .max { name.firstName.length() and name.lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }
    .medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }
    .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
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
df.groupBy { city }
    .max { firstName.length() and lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }
    .medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }
    .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
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
df.groupBy("city").max {
    "name"["firstName"].strings().length() and "name"["lastName"].strings().length()
} // maximum length of firstName or lastName into column "max"
df.groupBy("city")
    .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
df.groupBy("city")
    .minFor { ("age".ints() into "min age") and ("weight".intOrNulls() into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy("city").meanOf("mean ratio") {
    "weight".intOrNull()?.div("age".int())
} // mean of weight/age into column "mean ratio"
```

</tab></tabs>
<!---END-->

To get all column values for every group without aggregation use `values` function: 
* for [ValueColumn](columns.md#valuecolumn) of type `T` it will gather group values into lists of type `Many<T>`
* for [ColumnGroup](columns.md#columngroup) it will gather group values into `DataFrame` and convert [ColumnGroup](columns.md#columngroup) into [FrameColumn](columns.md#framecolumn)

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

### Conversions

Any `DataFrame` with `FrameColumn` can be reinterpreted as `GroupedDataFrame`:

<!---FUN dataFrameToGrouped-->

```kotlin
val key by columnOf(1, 2) // create int column with name "key"
val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
val df = dataFrameOf(key, data) // create dataframe with two columns

df.asGrouped { data } // convert dataframe to GroupedDataFrame by interpreting 'data' column as groups
```

<!---END-->

And any `GroupedDataFrame` can be reinterpreted as `DataFrame` with `FrameColumn`:

<!---FUN groupedDataFrameToFrame-->

```kotlin
df.groupBy { city }.asDataFrame()
```

<!---END-->

To ungroup `GroupedDataFrame` back to original `DataFrame` use `union`:

<!---FUN groupByUnion-->

```kotlin
df.groupBy { city }.union()
```

<!---END-->

This operation [unions](mix.md#union) all groups of `GroupedDataFrame` into single `DataFrame`. It will result in original `DataFrame`, but with reordered rows according to grouping keys.

## Pivot

`pivot` operation is used to split rows of `DataFrame` and group them horizontally into new columns based on values from one or several columns of original `DataFrame`. 

Pass a column to `pivot` function to use its values as grouping keys and names for new columns. To create multi-level column hierarchy, pass several columns to `pivot`:

<!---FUN pivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }
df.pivot { city and name.firstName }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()

df.pivot { city }
df.pivot { city and firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city")
df.pivot { "city"() and "name"["firstName"] }
```

</tab></tabs>
<!---END-->

`pivot` returns `PivotedDataFrame` which is an intermediate object that can be configured for further transformation and aggregation of data

To create matrix table that is expanded both horizontally and vertically, apply `groupBy` function at `PivotedDataFrame` passing the columns to be used for vertical grouping. Reversed order of `pivot` and `groupBy` operations will produce the same result. 

<!---FUN pivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name }
// same as
df.groupBy { name }.pivot { city }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()

df.pivot { city }.groupBy { name }
// same as
df.groupBy { name }.pivot { city }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name")
// same as
df.groupBy("name").pivot("city")
```

</tab></tabs>
<!---END-->

Combination of `pivot` and `groupBy` operations return `GroupedPivot` that can be used for further aggregation of data groups within matrix cells. 

### Aggregations

To aggregate data groups in `PivotedDataFrame` or `GroupedPivot` with one or several aggregation functions use `aggregate`:

<!---FUN pivotAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.aggregate { minBy { age }.name }
df.pivot { city }.groupBy { name.firstName }.aggregate {
    meanFor { age and weight } into "means"
    stdFor { age and weight } into "stds"
    maxByOrNull { weight }?.name?.lastName into "biggest"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.aggregate { minBy(age).name }

df.pivot { city }.groupBy { firstName }.aggregate {
    meanFor { age and weight } into "means"
    stdFor { age and weight } into "stds"
    maxByOrNull(weight)?.name?.lastName into "biggest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").aggregate { minBy("age")["name"] }

df.pivot("city").groupBy { "name"["firstName"] }.aggregate {
    meanFor("age", "weight") into "means"
    stdFor("age", "weight") into "stds"
    maxByOrNull("weight")?.get("name")?.get("lastName") into "biggest"
}
```

</tab></tabs>
<!---END-->

Shortcuts for common aggregation functions are also available:

<!---FUN pivotCommonAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.maxFor { age and weight }
df.groupBy { name }.pivot { city }.median { age }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.maxFor { age and weight }
df.groupBy { name }.pivot { city }.median { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").maxFor("age", "weight")
df.groupBy("name").pivot("city").median("age")
```

</tab></tabs>
<!---END-->

By default, when aggregation function produces several values for data group, column hierarchy in resulting `DataFrame` will be indexed first by pivot keys and then by the names of aggregated values.
To reverse this order so that resulting columns will be indexed first by names of aggregated values and then by pivot keys, use `separate=true` flag that is available in multi-result aggregation operations, such as `aggregate` or `<stat>For`:

<!---FUN pivotSeparate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.maxFor(separate = true) { age and weight }
df.pivot { city }.aggregate(separate = true) {
    min { age } into "min age"
    maxOrNull { weight } into "max weight"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.maxFor(separate = true) { age and weight }
df.pivot { city }.aggregate(separate = true) {
    min { age } into "min age"
    maxOrNull { weight } into "max weight"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").maxFor("age", "weight", separate = true)
df.pivot("city").aggregate(separate = true) {
    min("age") into "min age"
    maxOrNull("weight") into "max weight"
}
```

</tab></tabs>
<!---END-->

By default, any aggregation function will result in `null` value for those matrix cells, where intersection of column and row keys produced an empty data group. 
You can specify default value for any aggregation by `default` infix function. This value will replace all `null` results of aggregation function over non-empty data groups as well.
To use one default value for all aggregation functions, use `default()` before aggregation.  

<!---FUN pivotDefault-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
df.pivot { city }.groupBy { name }.aggregate {
    median { age } into "median age" default 0
    minOrNull { weight } into "min weight" default 100
}
df.pivot { city }.groupBy { name }.default(0).min()
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()

df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
df.pivot { city }.groupBy { name }.aggregate {
    median { age } into "median age" default 0
    minOrNull { weight } into "min weight" default 100
}
df.pivot { city }.groupBy { name }.default(0).min()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name").aggregate { min("age") default 0 }
df.pivot("city").groupBy("name").aggregate {
    median("age") into "median age" default 0
    minOrNull("weight") into "min weight" default 100
}
df.pivot("city").groupBy("name").default(0).min()
```

</tab></tabs>
<!---END-->

[pivot](#pivot) operation can also be used inside `aggregate` body of `GroupedDataFrame`. This allows to combine column pivoting with other aggregation functions:

<!---FUN pivotInAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name.firstName }.aggregate {
    pivot { city }.aggregate(separate = true) {
        mean { age } into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()
val age by column<Int>()

df.groupBy { firstName }.aggregate {
    pivot { city }.aggregate(separate = true) {
        mean { age } into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { "name"["firstName"] }.aggregate {
    pivot("city").aggregate(separate = true) {
        mean("age") into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab></tabs>
<!---END-->

### Conversions

`PivotedDataFrame` can be converted to `DataRow` and `GroupedPivot` can be converted to `DataFrame` without any additional transformations. Generated columns will have type `FrameColumn` and will contain data groups (similar to `GroupedDataFrame`)

<!---FUN pivotAsDataRowOrFrame-->

```kotlin
df.pivot { city }.asDataRow()
df.pivot { city }.groupBy { name }.asDataFrame()
```

<!---END-->
