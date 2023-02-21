[//]: # (title: groupBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Splits the rows of [`DataFrame`](DataFrame.md) into groups using one or several columns as grouping keys.

```text
groupBy { columns }
      [ transformations ]
      reducer | aggregator | pivot

transformations = [ .sortByCount() | .sortByCountAsc() | .sortBy { columns } | .sortByDesc { columns } ]
                  [ .updateGroups { frameExpression } ]
                  [ .add(column) { rowExpression } ]
    
reducer = .minBy { column } | .maxBy { column } | .first [ { rowCondition } ] | .last [ { rowCondition } ] 
          .concat() | .into([column]) [{ rowExpression }] | .values { valueColumns } 

aggregator = .count() | .concat() | .into([column]) [{ rowExpression }] | .values { valueColumns } | .aggregate { aggregations } | .<stat> [ { columns } ]

pivot = .pivot { columns }
      [ .default(defaultValue) ]
         pivotReducer | pivotAggregator  
```

See [column selectors](ColumnSelectors.md), [groupBy transformations](#transformation), [groupBy aggregations](#aggregation), [pivot+groupBy](pivot.md#pivot-groupby)

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
df.groupBy { "age"<Int>() / 10 named "ageDecade" }
df.groupBy { expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named "nameLength" }
```

</tab></tabs>
<!---END-->

Returns `GroupBy` object.

## Transformation

`GroupBy DataFrame` is a [`DataFrame`](DataFrame.md) with one chosen [`FrameColumn`](DataColumn.md#framecolumn) containing data groups.

It supports the following operations:
* [`add`](add.md)
* [`sortBy`](sortBy.md)
* [`map`](map.md)
* [`pivot`](pivot.md#pivot-groupby)
* [`concat`](concat.md)

Any [`DataFrame`](DataFrame.md) with `FrameColumn` can be reinterpreted as `GroupBy DataFrame`:

<!---FUN dataFrameToGroupBy-->

```kotlin
val key by columnOf(1, 2) // create int column with name "key"
val data by columnOf(df[0..3], df[4..6]) // create frame column with name "data"
val df = dataFrameOf(key, data) // create dataframe with two columns

df.asGroupBy { data } // convert dataframe to GroupBy by interpreting 'data' column as groups
```

<!---END-->

And any [`GroupBy DataFrame`](groupBy.md#transformation) can be reinterpreted as [`DataFrame`](DataFrame.md) with `FrameColumn`:

<!---FUN groupByToFrame-->

```kotlin
df.groupBy { city }.toDataFrame()
```

<!---END-->

Use [`concat`](concat.md) to union all data groups of `GroupBy` into original [`DataFrame`](DataFrame.md) preserving new order of rows produced by grouping:

<!---FUN concatGroupBy-->

```kotlin
df.groupBy { name }.concat()
```

<!---END-->

## Aggregation

To compute one or several [statistics](summaryStatistics.md) per every group of `GroupBy` use `aggregate` function. 
Its body will be executed for every data group and has a receiver of type [`DataFrame`](DataFrame.md) that represents current data group being aggregated.
To add a new column to the resulting [`DataFrame`](DataFrame.md), pass the name of new column to infix function `into`:

<!---FUN groupByAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate {
    count() into "total"
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
    count() into "total"
    count { age() > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age() }[name] into "name of oldest"
}
// or
df.groupBy(city).aggregate {
    count() into "total"
    count { age > 18 } into "adults"
    median(age) into "median age"
    min(age) into "min age"
    maxBy(age)[name] into "name of oldest"
}
// or
df.groupBy(city).aggregate {
    count() into "total"
    age().count { it > 18 } into "adults"
    age().median() into "median age"
    age().min() into "min age"
    maxBy(age)[name] into "name of oldest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate {
    count() into "total"
    count { "age"<Int>() > 18 } into "adults"
    median("age") into "median age"
    min("age") into "min age"
    maxBy("age")["name"] into "oldest"
}
// or
df.groupBy("city").aggregate {
    count() into "total"
    count { "age"<Int>() > 18 } into "adults"
    "age"<Int>().median() into "median age"
    "age"<Int>().min() into "min age"
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

Most common aggregation functions can be computed directly at [`GroupBy DataFrame`](groupBy.md#transformation) :

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
    "name"["firstName"]<String>().length() and "name"["lastName"]<String>().length()
} // maximum length of firstName or lastName into column "max"
df.groupBy("city")
    .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
df.groupBy("city")
    .minFor { ("age"<Int>() into "min age") and ("weight"<Int?>() into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy("city").meanOf("mean ratio") {
    "weight"<Int?>()?.div("age"<Int>())
} // mean of weight/age into column "mean ratio"
```

</tab></tabs>
<!---END-->

To get all column values for every group without aggregation use `values` function:
* for [ValueColumn](DataColumn.md#valuecolumn) of type `T` it will gather group values into lists of type `List<T>`
* for [ColumnGroup](DataColumn.md#columngroup) it will gather group values into [`DataFrame`](DataFrame.md) and convert [ColumnGroup](DataColumn.md#columngroup) into [FrameColumn](DataColumn.md#framecolumn)

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
df.groupBy("city").values { "weight" into "weights" }
```

</tab></tabs>
<!---END-->
