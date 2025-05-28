[//]: # (title: pivot)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Splits the rows of a [`DataFrame`](DataFrame.md) and groups them horizontally into new columns based on values from one or several columns of the original [`DataFrame`](DataFrame.md).

```text
pivot (inward = true) { pivotColumns }
  [ .groupBy { indexColumns } | .groupByOther() ]
  [ .default(defaultValue) ]
     reducer | aggregator
    
reducer = .minBy { column } | .maxBy { column } | .first [ { rowCondition } ] | .last [ { rowCondition } ] 
          .with { rowExpression } | .values { valueColumns }

aggregator = .count() | .matches() | .frames() | .with { rowExpression } | .values { valueColumns } | .aggregate { aggregations } | .<stat> [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `inward` — if `true` generated columns are nested inside the original column, otherwise they will be top-level
* `pivotColumns` — columns with values for horizontal data grouping and generation of new columns
* `indexColumns` — columns with values for vertical data grouping
* `defaultValue` — value to fill mismatched pivot-index column pairs
* `valueColumns` — columns with output values

<!---FUN pivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivot.html" width="100%"/>
<!---END-->

To pivot several columns at once, you can combine them using `and` or `then` infix function:
* `and` will pivot columns independently
* `then` will create column hierarchy from combinations of values from pivoted columns

<!---FUN pivot2-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city and name.firstName }
df.pivot { city then name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot { "city" and "name"["firstName"] }
df.pivot { "city" then "name"["firstName"] }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivot2.html" width="100%"/>
<!---END-->

## pivot + groupBy

To create a matrix table that is expanded both horizontally and vertically,
apply [`groupBy`](groupBy.md) transformation passing the columns for vertical grouping. 
Reversed order of `pivot` and [`groupBy`](groupBy.md) will produce the same result.

<!---FUN pivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
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
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotGroupBy.html" width="100%"/>
<!---END-->

To group by all columns except pivoted use `groupByOther`:

<!---FUN pivotGroupByOther-->

```kotlin
df.pivot { city }.groupByOther()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotGroupByOther.html" width="100%"/>
<!---END-->

## Aggregation

To aggregate data groups with one or several statistics use `aggregate`:

<!---FUN pivotAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.aggregate { minBy { age }.name }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").aggregate { minBy("age")["name"] }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotAggregate.html" width="100%"/>
<!---END-->

<!---FUN pivotAggregate1-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name.firstName }.aggregate {
    meanFor { age and weight } into "means"
    stdFor { age and weight } into "stds"
    maxByOrNull { weight }?.name?.lastName into "biggest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy { "name"["firstName"] }.aggregate {
    meanFor("age", "weight") into "means"
    stdFor("age", "weight") into "stds"
    maxByOrNull("weight")?.getColumnGroup("name")?.get("lastName") into "biggest"
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotAggregate1.html" width="100%"/>
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
<tab title="Strings">

```kotlin
df.pivot("city").maxFor("age", "weight")
df.groupBy("name").pivot("city").median("age")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotCommonAggregations.html" width="100%"/>
<!---END-->

By default, when aggregation function produces several values for single data group, 
column hierarchy in resulting [`DataFrame`](DataFrame.md) 
will be indexed first by pivot keys and then by the names of aggregated values.
To reverse this order so that resulting columns will be indexed first by names of aggregated values and then by pivot keys, 
use `separate=true` flag that is available in multi-result aggregation operations, such as `aggregate` or `<stat>For`:

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
df.pivot { city }.groupBy { name }.default(0).min()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name").aggregate { min("age") default 0 }
df.pivot("city").groupBy("name").default(0).min()
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotDefault.html" width="100%"/>
<!---END-->

<!---FUN pivotDefault1-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name }.aggregate {
    median { age } into "median age" default 0
    minOrNull { weight } into "min weight" default 100
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name").aggregate {
    median("age") into "median age" default 0
    minOrNull("weight") into "min weight" default 100
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotDefault1.html" width="100%"/>
<!---END-->

### Pivot inside aggregate

pivot transformation can be used inside [`aggregate`](groupBy.md#aggregation) function of [`groupBy`](groupBy.md). 
This allows combining column pivoting with other [`groupBy`](groupBy.md) aggregations:

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
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotInAggregate.html" width="100%"/>
<!---END-->

### pivotCounts

Pivots with [`count`](count.md) statistics one or several columns preserving all other columns of [`DataFrame`](DataFrame.md) 
or [`GroupBy DataFrame`](groupBy.md#transformation).

<!---FUN pivotCounts-->

```kotlin
df.pivotCounts { city }
// same as
df.pivot { city }.groupByOther().count()

df.groupBy { name }.pivotCounts { city }
// same as
df.groupBy { name }.pivot { city }.count()
// same as
df.groupBy { name }.aggregate {
    pivotCounts { city }
}
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotCounts.html" width="100%"/>
<!---END-->

### pivotMatches

Pivots with `Boolean` statistics one or several columns preserving all other columns of [`DataFrame`](DataFrame.md).

<!---FUN pivotMatches-->

```kotlin
df.pivotMatches { city }
// same as
df.pivot { city }.groupByOther().matches()

df.groupBy { name }.pivotMatches { city }
// same as
df.groupBy { name }.pivot { city }.matches()
// same as
df.groupBy { name }.aggregate {
    pivotMatches { city }
}
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotMatches.html" width="100%"/>
<!---END-->
