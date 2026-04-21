[//]: # (title: pivot)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.PivotSamples-->

Splits the rows of a [`DataFrame`](DataFrame.md) and groups them horizontally into new columns based on values from one or several columns of the original [`DataFrame`](DataFrame.md).

```text
pivot (inward = true) { pivotColumns }
  [ .groupBy { indexColumns } | .groupByOther() ]
  [ .default(defaultValue) ]
     reducer | aggregator
    
reducer = .minBy { column } | .maxBy { column } | .first [ { rowCondition } ] | .last [ { rowCondition } ] 
          .medianBy { column } | .percentileBy(percentile) { column } |.with { rowExpression } | .values { valueColumns }

aggregator = .count() | .matches() | .frames() | .with { rowExpression } | .values { valueColumns } | .aggregate { aggregations } | .<stat> [ { columns } ]
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation, 
[`pivot + groupBy`](pivot.md#pivot-groupby), [`pivot reducing`](pivot.md#reducing), [`pivot aggregation`](pivot.md#aggregation).

**Parameters:**
* `inward` — if `true` generated columns are nested inside the original column, otherwise they will be top-level
* `pivotColumns` — columns with values for horizontal data grouping and generation of new columns
* `indexColumns` — columns with values for vertical data grouping
* `defaultValue` — value to fill mismatched pivot-index column pairs
* `valueColumns` — columns with output values

<!---FUN df-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/df.html" width="100%" height="500px"></inline-frame>

<!---FUN pivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivot_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotInward-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot(inward = true) { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy", inward = true)
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotInward_properties.html" width="100%" height="500px"></inline-frame>


To pivot several columns at once, you can combine them using `and` or `then` infix function:
* `and` will pivot columns independently
* `then` will create column hierarchy from combinations of values from pivoted columns

<!---FUN pivotAnd-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy and name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot { "isHappy" and "name"["firstName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotAnd_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotThen-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy then name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot { "isHappy" then "name"["firstName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotThen_properties.html" width="100%" height="500px"></inline-frame>

## pivot + groupBy

To create a matrix table that is expanded both horizontally and vertically,
apply [`groupBy`](groupBy.md) transformation passing the columns for vertical grouping. 
Reversed order of `pivot` and [`groupBy`](groupBy.md) will produce the same result.

<!---FUN pivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupBy { name.firstName }
// same as
df.groupBy { name.firstName }.pivot { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupBy { "name"["firstName"] }
// same as
df.groupBy { "name"["firstName"] }.pivot("isHappy")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotGroupBy_properties.html" width="100%" height="500px"></inline-frame>

To group by all columns except pivoted, use `groupByOther`:

<!---FUN pivotGroupByOther-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupByOther()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupByOther()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotGroupByOther_properties.html" width="100%" height="500px"></inline-frame>

`PivotGroupBy` supports the same reducing and aggregation operations as `Pivot`.
In addition, `PivotGroupBy` supports the [`default`](pivot.md#default-values-for-aggregation) 
and [`matches`](pivot.md#pivotmatches) functions.

<!---FUN pivotGroupByAggregation-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name.firstName }.pivot { isHappy }.max { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { "name"["firstName"] }.pivot("isHappy").max("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotGroupByAggregation_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotGroupByFrames-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupBy { name.firstName }.frames()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupBy { "name"["firstName"] }.frames()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotGroupByFrames_properties.html" width="100%" height="500px"></inline-frame>

## Reducing
A [`Pivot`](pivot.md) can be reduced to a [`DataRow`](DataRow.md). 
It means that each `Pivot` group is collapsed into a single row, 
and then these rows are concatenated into a single [`DataRow`](DataRow.md) 
with `Pivot` keys as top-level columns or as [`column groups`](DataColumn.md#columngroup).

Reducing is a specific case of [`aggregation`](pivot.md#aggregation).

### Step 1: use a reducing method
Use the following functions to collapse each group in a [`Pivot`](pivot.md) into a single row:
* [`first`](first.md) / [`last`](last.md) — take the first or last row (optionally, the first or last one that satisfies a predicate) of each group;
* [`minBy`](minBy.md) / [`maxBy`](maxBy.md) — take the row with the minimum or maximum value of the given `RowExpression` evaluated on rows within each group;
* [`medianBy`](median.md) / [`percentileBy`](percentile.md) — take the row with the median or a specific percentile value of the given `RowExpression` evaluated on rows within each group.

These functions return an instance of `ReducedPivot`, which is a class serving as a transitional step between performing a reduction on [`Pivot`](pivot.md) groups 
and specifying how the resulting reduced rows should be represented in a resulting [`DataRow`](DataRow.md).

#### Examples of reducing {collapsible="true"}
##### df.pivot {collapsible="true"}
<!---FUN pivotReducing-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotReducing_properties.html" width="100%" height="500px"></inline-frame>

##### first {collapsible="true"}
<!---FUN pivotFirst-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.first()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").first()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotFirst_properties.html" width="100%" height="500px"></inline-frame>

The [`first`](first.md) function can be used with a predicate:

<!---FUN pivotFirstWithPredicate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.first { age == 30 }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").first { "age"<Int>() == 30 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotFirstWithPredicate_properties.html" width="100%" height="500px"></inline-frame>

##### last {collapsible="true"}
<!---FUN pivotLast-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.last()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").last()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotLast_properties.html" width="100%" height="500px"></inline-frame>

The [`last`](last.md) function can be used with a predicate:

<!---FUN pivotLastWithPredicate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.last { age > 20 }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").last { "age"<Int>() > 20 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotLastWithPredicate_properties.html" width="100%" height="500px"></inline-frame>

##### minBy {collapsible="true"}
<!---FUN pivotMinBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.minBy { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").minBy { "weight"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMinBy_properties.html" width="100%" height="500px"></inline-frame>

##### maxBy {collapsible="true"}
<!---FUN pivotMaxBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.maxBy { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").maxBy { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMaxBy_properties.html" width="100%" height="500px"></inline-frame>

##### medianBy {collapsible="true"}
<!---FUN pivotMedianBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.medianBy { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").medianBy { "weight"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMedianBy_properties.html" width="100%" height="500px"></inline-frame>

##### percentileBy {collapsible="true"}
<!---FUN pivotPercentileBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.percentileBy(25.0) { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").percentileBy(25.0) { "weight"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotPercentileBy_properties.html" width="100%" height="500px"></inline-frame>


### Step 2: transform the resulting reduced rows into a new [`DataRow`](DataRow.md)
`ReducedPivot` can be transformed into a new [`DataRow`](DataRow.md) (either using the original reduced rows or their transformed versions).

To perform this transformation, use one of the following functions:
* [`values`](values.md) — creates a new [`DataRow`](DataRow.md) containing the values from the reduced rows in the selected columns;
* `with` — computes a new value for each reduced row using a `RowExpression` and produces a [`DataRow`](DataRow.md) containing these computed values.

Each of these functions returns a new [`DataRow`](DataRow.md) with [`Pivot`](pivot.md) keys as top-level columns (or as [`column groups`](DataColumn.md#columngroup)) 
and values composed of the reduced results from each group.

#### Examples of transforming {collapsible="true"}
##### values {collapsible="true"}
<!---FUN pivotValues-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.first().values()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").first().values()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotValues_properties.html" width="100%" height="500px"></inline-frame>

##### with {collapsible="true"}
<!---FUN pivotWith-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }
    .maxBy { age }
    .with { name.firstName + " " + name.lastName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy")
    .maxBy("age")
    .with { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotWith_properties.html" width="100%" height="500px"></inline-frame>

## Aggregation

Each Pivot group can be aggregated — that is, transformed into a new value, [`DataRow`](DataRow.md), or [`DataFrame`](DataFrame.md) — 
and then concatenated into a single [`DataRow`](DataRow.md) composed of these aggregated results, 
with pivot keys as top-level columns or as [`column groups`](DataColumn.md#columngroup).

The following aggregation methods are available:
* `frames` — returns this [`Pivot`](pivot.md) as a [`DataRow`](DataRow.md) with pivot keys as [`columns`](DataColumn.md) 
(or [`column groups`](DataColumn.md#columngroup)) and corresponding groups stored as [`FrameColumns`](DataColumn.md#framecolumn);
* [`values`](values.md) — collects values from all rows of each group for the selected columns into a single `List` 
(values from [`column groups`](DataColumn.md#columngroup) are collected into a [`FrameColumn`](DataColumn.md#framecolumn));
* [`count`](count.md) — creates a [`DataRow`](DataRow.md) with the `Pivot` key columns containing the number of rows in each corresponding group;
* `with` — creates a [`DataRow`](DataRow.md) containing values computed using a `RowExpression` across all rows of each group and collected into a single List for every group;
* `aggregate` — performs a set of custom aggregations using `AggregateDsl`, allowing computation of one or more derived values per group; 
* various [`aggregation statistics`](pivot.md#aggregation-statistics).

Each of these methods returns a new [`DataRow`](DataRow.md) with `Pivot` keys as top-level columns 
(or as [`column groups`](DataColumn.md#columngroup)) and values representing the aggregated results of each group.

### Examples of aggregation {collapsible="true"}
#### frames {collapsible="true"}
<!---FUN pivotFrames-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.frames()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").frames()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotFrames_properties.html" width="100%" height="500px"></inline-frame>

#### values on Pivot {collapsible="true"}
<!---FUN pivotValuesAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.values { name and age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").values("name", "age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotValuesAggregate_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotValuesAggregateAll-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.values()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").values()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotValuesAggregateAll_properties.html" width="100%" height="500px"></inline-frame>

#### count {collapsible="true"}
<!---FUN pivotCount-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.count()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").count()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotCount_properties.html" width="100%" height="500px"></inline-frame>

#### with on Pivot {collapsible="true"}
<!---FUN withOnPivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.with { name.lastName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").with { "name"["lastName"]<String>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/withOnPivot_properties.html" width="100%" height="500px"></inline-frame>

#### aggregate {collapsible="true"}
<!---FUN pivotAggregateMultiple-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.aggregate {
    count() into "total"
    count { age >= 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age }.name into "oldest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").aggregate {
    count() into "total"
    count { "age"<Int>() > 18 } into "adults"
    median("age") into "median age"
    min("age") into "min age"
    maxBy("age")["name"] into "oldest"
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotAggregateMultiple_properties.html" width="100%" height="500px"></inline-frame>

If only one aggregation function is used, the column name can be omitted:
<!---FUN pivotAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.aggregate { minBy { age }.name }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").aggregate { minBy("age")["name"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotAggregate_properties.html" width="100%" height="500px"></inline-frame>

### Aggregation statistics
[`Aggregation statistics`](summaryStatistics.md) are predefined shortcuts for common statistical aggregations 
such as sum, mean, median, and others. These aggregation operations are applied to each group within a `Pivot`.

Each function computes a statistic across the [`rows`](DataRow.md) of a pivot group and returns the result as a new value
in the resulting [`DataRow`](DataRow.md) (for [`Pivot`](pivot.md)) or as a new [`column`](DataColumn.md) 
in the resulting [`DataFrame`](DataFrame.md) (for `PivotGroupBy`).

The following aggregation statistics are available:
* [`count`](count.md);
* [`max`](minmax.md) / maxOf / maxFor;
* [`min`](minmax.md) / minOf / minFor;
* [`sum`](sum.md) / sumOf / sumFor;
* [`mean`](mean.md) / meanOf / meanFor;
* [`std`](std.md) / stdOf / stdFor;
* [`median`](median.md) / medianOf / medianFor;
* [`percentile`](percentile.md) / percentileOf / percentileFor.

To compute one or several [`statistics`](summaryStatistics.md) per every pivot group, use the [`aggregate`](pivot.md#aggregation) function.

The functions `max`, `maxOf`, and `maxFor` differ as follows. They all calculate the maximum of values, but:
* `max` computes it on the selected columns. If more than one column is selected, for each group it computes one maximum value among all selected columns.
* `maxOf` computes it by a `row expression`: the expression is calculated for each row of the group and the maximum value is returned.
* `maxFor` computes it for each of the selected columns within each group. If more than one column is selected, for each group it computes the maximum value for each selected column separately.

Similar logic applies to other statistics.

#### Direct aggregations

Most common aggregation functions can be computed directly on a [`Pivot`](pivot.md) or `PivotGroupBy`.

When applied to a [`Pivot`](pivot.md) (without [`groupBy`](groupBy.md)), the result is a [`DataRow`](DataRow.md) with pivot keys as columns
and the computed statistic as values.

When applied to a `PivotGroupBy`, the result is a [`DataFrame`](DataFrame.md) where rows correspond to the grouping keys
and columns correspond to the pivot keys, with computed statistics as cell values.

##### Examples of direct aggregations {collapsible="true"}
###### max {collapsible="true"}
<!---FUN pivotMax-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.max { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").max("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMax_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotMaxMultiple-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.max { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").max("age", "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMaxMultiple_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotMaxForMultiple-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.maxFor { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").maxFor("age", "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMaxForMultiple_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotMaxOf-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.maxOf { if (age < 30) weight else null }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").maxOf { if ("age"<Int>() < 30) "weight"<Int>() else null }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMaxOf_properties.html" width="100%" height="500px"></inline-frame>

###### min {collapsible="true"}
<!---FUN pivotMin-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.min { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").min("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMin_properties.html" width="100%" height="500px"></inline-frame>

###### sum {collapsible="true"}
<!---FUN pivotSum-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.sum { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").sum("weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotSum_properties.html" width="100%" height="500px"></inline-frame>

###### mean {collapsible="true"}
<!---FUN pivotMeanAll-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.mean()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").mean()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMeanAll_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotMean-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.mean { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").mean { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMean_properties.html" width="100%" height="500px"></inline-frame>

###### std {collapsible="true"}
<!---FUN pivotStd-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.std { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").std { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotStd_properties.html" width="100%" height="500px"></inline-frame>

###### median {collapsible="true"}
<!---FUN pivotMedian-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.median { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").median("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMedian_properties.html" width="100%" height="500px"></inline-frame>

###### percentile {collapsible="true"}
<!---FUN pivotPercentile-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.percentile(25.0) { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").percentile(25.0) { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotPercentile_properties.html" width="100%" height="500px"></inline-frame>

#### The `separate` flag

By default, when an aggregation function produces several values for a single data group, 
the column hierarchy in the resulting [`DataFrame`](DataFrame.md) 
is indexed first by pivot keys and then by the names of aggregated values.
To reverse this order so that resulting columns are indexed first by names of aggregated values and then by pivot keys, 
use the `separate=true` flag that is available in multi-result aggregation operations, such as `aggregate` or `<stat>For`:

<!---FUN pivotSeparate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.maxFor(separate = true) { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").maxFor("age", "weight", separate = true)
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotSeparate_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotAggregateSeparate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.aggregate(separate = true) {
    min { age } into "min age"
    maxOrNull { weight } into "max weight"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").aggregate(separate = true) {
    min("age") into "min age"
    maxOrNull("weight") into "max weight"
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.pivotSeparate.html" width="100%"/>
<!---END-->
<inline-frame src="./resources/pivotAggregateSeparate_properties.html" width="100%" height="500px"></inline-frame>

#### Default values for aggregation
By default, any aggregation function will result in a `null` value for those matrix cells where intersection of column and row keys produced an empty data group.
You can specify a default value for any aggregation by using the `default` infix function. This value will replace all `null` results of the aggregation function over non-empty data groups as well.
To use one default value for all aggregation functions, use `default()` before aggregation.

<!---FUN pivotDefault-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupBy { city }.aggregate { min { age } default 0 }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupBy("city").aggregate { min("age") default 0 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotDefault_properties.html" width="100%" height="500px"></inline-frame>


<!---FUN pivotDefaultDirect-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupBy { city }.default(0).min()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupBy("city").default(0).min()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotDefaultDirect_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotDefaultMultiple-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { isHappy }.groupBy { city }.aggregate {
    count() into "people" default 0
    any { age < 18 } into "hasMinors" default false
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("isHappy").groupBy("city").aggregate {
    count() into "people" default 0
    any { "age"<Int>() < 18 } into "hasMinors" default false
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotDefaultMultiple_properties.html" width="100%" height="500px"></inline-frame>

### Pivot inside aggregate

[`pivot`](pivot.md) can be used inside the [`aggregate`](groupBy.md#aggregation) function of [`groupBy`](groupBy.md). 
This allows combining column pivoting with other [`groupBy`](groupBy.md) aggregations:

<!---FUN pivotInAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name.firstName }.aggregate {
    pivot { isHappy }.aggregate(separate = true) {
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
    pivot("isHappy").aggregate(separate = true) {
        mean("age") into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotInAggregate_properties.html" width="100%" height="500px"></inline-frame>

### pivotCounts

Pivots one or several columns with [`count`](count.md) aggregation, preserving all other columns of [`DataFrame`](DataFrame.md) 
or [`GroupBy DataFrame`](groupBy.md#transformation).

<!---FUN pivotCountsOnDf-->
<tabs>
<tab title="Properties">

```kotlin
df.pivotCounts { isHappy }
// same as
df.pivot { isHappy }.groupByOther().count()
```

</tab>
<tab title="Strings">

```kotlin
df.pivotCounts("isHappy")
// same as
df.pivot("isHappy").groupByOther().count()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotCountsOnDf_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotCountsOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name }.pivotCounts { city }
// same as
df.groupBy { name }.pivot { city }.count()
// same as
df.groupBy { name }.aggregate {
    pivotCounts { city }
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("name").pivotCounts("city")
// same as
df.groupBy("name").pivot("city").count()
// same as
df.groupBy("name").aggregate {
    pivotCounts("city")
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotCountsOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

### pivotMatches

Pivots one or several columns with `Boolean` aggregation, preserving all other columns of [`DataFrame`](DataFrame.md).
Each cell in the resulting pivoted columns contains `true` if that value was present in the original data 
for the corresponding row, and `false` otherwise.

<!---FUN pivotMatchesGroupByOther-->
<tabs>
<tab title="Properties">

```kotlin
df.pivotMatches { city }
// same as
df.pivot { city }.groupByOther().matches()
```

</tab>
<tab title="Strings">

```kotlin
df.pivotMatches("city")
// same as
df.pivot("city").groupByOther().matches()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMatchesGroupByOther_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN pivotMatches-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name }.pivotMatches { city }
// same as
df.groupBy { name }.pivot { city }.matches()
// same as
df.groupBy { name }.aggregate {
    pivotMatches { city }
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("name").pivotMatches("city")
// same as
df.groupBy("name").pivot("city").matches()
// same as
df.groupBy("name").aggregate {
    pivotMatches("city")
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/pivotMatches_properties.html" width="100%" height="500px"></inline-frame>
