[//]: # (title: groupBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.GroupBySamples-->

Splits the rows of [`DataFrame`](DataFrame.md) into groups using one or several columns as grouping keys.

```text
groupBy(moveToTop = true) { columns }
      [ transformations ]
      reducer | aggregator | pivot

transformations = [ .sortByGroup { expression } | .sortByGroupDesc { expression } | .sortByCount() | .sortByCountAsc() | .sortByKey() | .sortByKeyDesc() | .sortBy { columns } | .sortByDesc { columns } ]
                  [ .updateGroups { frameExpression } ]
                  [ .filter { rowExpression } ]
                  [ .add(column) { rowExpression } ]
    
reducer = .minBy { column } | .maxBy { column } | .medianBy { rowExpression } | .percentileBy(percentile) { rowExpression } | .first [ { rowCondition } ] | .last [ { rowCondition } ] 
          .concat() | .into([column]) [{ rowExpression }] | .values { valueColumns } 

aggregator = .count() | .concat() | .concatWithKeys() | toDataFrame() | .into([column]) [{ rowExpression }] | .values { valueColumns } | .aggregate { aggregations } | .<stat> [ { columns } ]

pivot = .pivot { columns }
      [ .default(defaultValue) ]
         pivotReducer | pivotAggregator  
```

See [`column selectors`](ColumnSelectors.md) for how to select the columns for this operation,
[`groupBy transformations`](#transformation), [`groupBy reducing`](#reducing), [`groupBy aggregations`](#aggregation), 
and [`pivot+groupBy`](pivot.md#pivot-groupby).

<!---FUN groupByDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/groupByDf.html" width="100%" height="500px"></inline-frame>

<!---FUN groupByDfGrouped-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByDfGrouped_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN groupByTwoColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name.firstName and isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { "name"["firstName"]<String>() and "isHappy" }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByTwoColumns_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN groupByNewColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { age / 10 named "ageDecade" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { "age"<Int>() / 10 named "ageDecade" }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByNewColumn_properties.html" width="100%" height="500px"></inline-frame>


Grouping columns can be created inplace:

<!---FUN groupByExpr-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { expr { name.firstName.length + name.lastName.length } named "nameLength" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy {
    expr { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length } named
        "nameLength"
}
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByExpr_properties.html" width="100%" height="500px"></inline-frame>


With the optional ` moveToTop ` parameter, you can choose whether to make a selected *nested column* a top-level column:  

<!---FUN groupByMoveToTop-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy(moveToTop = true) { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy(moveToTop = true) { "name"["firstName"]<String>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByMoveToTop_properties.html" width="100%" height="500px"></inline-frame>


or to keep it inside a `ColumnGroup`:

<!---FUN groupByMoveToTopFalse-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy(moveToTop = false) { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy(moveToTop = false) { "name"["firstName"]<String>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByMoveToTopFalse_properties.html" width="100%" height="500px"></inline-frame>

Returns `GroupBy` object.

## Transformation

The `groupBy` function returns a `GroupBy` object. A `GroupBy` is a [`DataFrame`](DataFrame.md) with one chosen [`FrameColumn`](DataColumn.md#framecolumn) containing data groups.

A `GroupBy` can be transformed into a new `GroupBy` using one of the following methods:

* `sortByGroup` / `sortByGroupDesc` — sorts the order of groups (and their corresponding keys) by values computed with a `DataFrameExpression` applied to each group;
* `sortByCount` / `sortByCountAsc` — sorts the order of groups (and their corresponding keys) by the number of rows they contain;
* `sortByKey` / `sortByKeyDesc` — sorts the order of groups (and their corresponding keys) by the grouping key values;
* [`sortBy`](sortBy.md) / [`sortByDesc`](sortBy.md#sortbydesc) — sorts the order of rows within each group by one or more column values;
* `updateGroups` — transforms each group into a new one using the provided transforming function;
* [`filter`](filter.md) — filters group rows by the given predicate;
* [`add`](add.md) — adds a new column to each group.

Any [`DataFrame`](DataFrame.md) with [FrameColumn](DataColumn.md#framecolumn) can be reinterpreted as `GroupBy DataFrame`:

<!---FUN dataFrameToGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
val df = dataFrameOf(
    "key" to columnOf(1, 2),
    "data" to columnOf(df[0..3], df[4..6]),
) // create dataframe with two columns

df.asGroupBy { data } // convert dataframe to GroupBy by interpreting 'data' column as groups
```

</tab>
<tab title="Strings">

```kotlin
val df = dataFrameOf(
    "key" to columnOf(1, 2),
    "data" to columnOf(df[0..3], df[4..6]),
) // create dataframe with two columns

df.asGroupBy("data") // convert dataframe to GroupBy by interpreting 'data' column as groups
```

</tab></tabs>
<!---END-->

### Examples of transformation {collapsible="true"}
#### sortByGroup {collapsible="true"}
<!---FUN sortByGroupOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.sortByGroup { mean { age } }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").sortByGroup { mean("age") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByGroupOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### sortByCount {collapsible="true"}
<!---FUN sortByCountOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { age }.sortByCount()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("age").sortByCount()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByCountOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### sortByKey {collapsible="true"}
<!---FUN sortByKeyOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { age }.sortByKey()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { age }.sortByKey()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByKeyOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### sortBy {collapsible="true"}
<!---FUN sortByOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.sortBy { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").sortBy("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### updateGroups {collapsible="true"}
<!---FUN updateGroupsOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.updateGroups { sortByDesc { age }.take(2) }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").updateGroups { sortByDesc("age").take(2) }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/updateGroupsOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### filter {collapsible="true"}
<!---FUN filterOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.filter { group.median { age } > 20 }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").filter { group.median { "age"<Int>() } > 20 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/filterOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

#### add {collapsible="true"}
<!---FUN addOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.add("isAdult") { age >= 18 }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").add("isAdult") { "age"<Int>() >= 18 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/addOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

 ## Reducing

A `GroupBy DataFrame` can be reduced to a [`DataFrame`](DataFrame.md). 
It means that each group in this [`GroupBy DataFrame`](groupBy.md#transformation) is collapsed into a single representative row,
and these rows are concatenated into a new [`DataFrame`](DataFrame.md).

Reducing is a specific case of aggregation.

This mechanism includes two steps.

### Step 1: use a reducing function to make a single row from each group
To perform a reducing operation, use the following functions:
* [`first`](first.md) / [`last`](last.md) – to get the first / last [row](DataRow.md) 
(optionally, the first or last one that satisfies a predicate) of each group.

* [`minBy`](minBy.md) / [`maxBy`](maxBy.md) – to get from each group the first row that has the smallest / largest value in the given column.

* [`medianBy`](median.md) / [`percentileBy`](percentile.md) – to get the row with the median or specific percentile value of the given `RowExpression` calculated on rows within each group.

These functions return an instance of `ReducedGroupBy`, which is a class serving as a transitional step
between performing a reduction on groups and specifying how the resulting reduced rows (either original or transformed)
should be represented in a new [`DataFrame`](DataFrame.md).

#### Examples of reducing {collapsible="true"}
##### df.groupBy {collapsible="true"}
<!---FUN groupByDfGroupedReducing-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByDfGroupedReducing_properties.html" width="100%" height="500px"></inline-frame>

##### first {collapsible="true"}
<!---FUN groupByFirst-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.first { age == 30 }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").first { it["age"] == 30 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByFirst_properties.html" width="100%" height="500px"></inline-frame>

##### last {collapsible="true"}
<!---FUN groupByLast-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.last { weight == null }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").last { it["weight"] == null }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByLast_properties.html" width="100%" height="500px"></inline-frame>

##### minBy {collapsible="true"}
<!---FUN groupByMinBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.minBy { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").minBy("weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByMinBy_properties.html" width="100%" height="500px"></inline-frame>

##### maxBy {collapsible="true"}
<!---FUN groupByMaxBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.maxBy { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").maxBy("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByMaxBy_properties.html" width="100%" height="500px"></inline-frame>

##### medianBy {collapsible="true"}
<!---FUN groupByMedianBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.medianBy { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").medianBy("weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByMedianBy_properties.html" width="100%" height="500px"></inline-frame>

##### percentileBy {collapsible="true"}
<!---FUN groupByPercentileBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.percentileBy(25.0) { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").percentileBy(25.0, "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByPercentileBy_properties.html" width="100%" height="500px"></inline-frame>

### Step 2: transform the result to a [`DataFrame`](DataFrame.md)
A `ReducedGroupBy` can be transformed into a [`DataFrame`](DataFrame.md) using the following functions:
* [`concat`](concat.md) – to concatenate all reduced rows into a single [`DataFrame`](DataFrame.md).
* [`values`](values.md) – to create a [`DataFrame`](DataFrame.md) that contains the values from the reduced rows in the selected columns. 
* `into` – to add a new [`column`](DataColumn.md) to the resulting [`DataFrame`](DataFrame.md) with values computed with `RowExpression` on each row, or a new [`column group`](DataColumn.md) containing each group reduced to a single row.

Each method returns a new [`DataFrame`](DataFrame.md) that includes the grouping key columns, containing all unique grouping key values 
(or value combinations for multiple keys) along with their corresponding reduced rows.

#### Examples of transforming ReducedGroupBy to DataFrame {collapsible="true"}
##### concat {collapsible="true"}

<!---FUN groupByConcat-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.minBy { age }.concat()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").minBy("age").concat()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByConcat_properties.html" width="100%" height="500px"></inline-frame>


##### values {collapsible="true"}

<!---FUN groupByValues-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.minBy { age }.values { name and age and city }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").minBy("age").values("name", "age", "city")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByValues_properties.html" width="100%" height="500px"></inline-frame>

##### into {collapsible="true"}

<!---FUN groupByInto-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.minBy { age }.into("youngest") { name }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").minBy("age").into("youngest") { getColumnGroup("name") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/groupByInto_properties.html" width="100%" height="500px"></inline-frame>

## Aggregation

A `GroupBy DataFrame` can be directly transformed into a new [`DataFrame`](DataFrame.md) by applying one or more aggregation operations to its groups.

Aggregation is a generalization of [`reducing`](groupBy.md#reducing).

The following aggregation methods are available:
* [`concat`](concat.md) — concatenates all [`rows`](DataRow.md) from all groups into a single [`DataFrame`](DataFrame.md), without preserving grouping keys.
* [`toDataFrame`](createDataFrame.md#todataframe) — returns this `GroupBy` as [`DataFrame`](DataFrame.md) with the grouping keys and corresponding groups in [FrameColumn](DataColumn.md#framecolumn).
* `concatWithKeys` — a variant of [`concat`](concat.md) that also includes grouping keys that were not present in the original [`DataFrame`](DataFrame.md).
* `into` — creates a new [`column`](DataColumn.md) containing a list of values computed with a `RowExpression` for each group, or a new [FrameColumn](DataColumn.md#framecolumn) containing the groups themselves.
* [`values`](values.md) — collects all column values for every group without aggregation. for [ValueColumn](DataColumn.md#valuecolumn) of type `T` it will gather group values into lists of type `List<T>`.
For [ColumnGroup](DataColumn.md#columngroup) it will gather group values into [`DataFrame`](DataFrame.md) and convert [ColumnGroup](DataColumn.md#columngroup) into [FrameColumn](DataColumn.md#framecolumn).
* [`count`](count.md) — creates a [`DataFrame`](DataFrame.md) containing the grouping key columns and an additional [`column`](DataColumn.md) with the number of rows in each corresponding group.
* `aggregate` — performs a set of custom aggregations using `AggregateDsl`, allowing you to compute one or more [statistics](summaryStatistics.md) per every group of `GroupBy`. 
The body if this function will be executed for every data group and has a receiver of type [`DataFrame`](DataFrame.md) that represents current data group being aggregated. 
To add a new column to the resulting [`DataFrame`](DataFrame.md), pass the name of new column to infix function `into`.

Each of these methods returns a new DataFrame that includes the grouping key columns (except for [`concat`](concat.md)) along with the columns of values aggregated from the corresponding groups.

### Examples of aggregation {collapsible="true"}
#### concat on GroupBy {collapsible="true"}
[`concat`](concat.md) can be used to union all data groups of `GroupBy` into original [`DataFrame`](DataFrame.md) preserving new order of rows produced by grouping:

<!---FUN concatOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.concat()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").concat()
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/concatOnGroupBy_properties.html" width="100%"/>

#### toDataFrame on GroupBy {collapsible="true"}
Any [`GroupBy DataFrame`](groupBy.md#transformation) can be reinterpreted as [`DataFrame`](DataFrame.md) with [FrameColumn](DataColumn.md#framecolumn):

<!---FUN toDfOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.toDataFrame()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").toDataFrame()
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/toDfOnGroupBy_properties.html" width="100%"/>

#### concatWithKeys on GroupBy {collapsible="true"}
<!---FUN concatWithKeysOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { expr { age >= 18 } named "isAdult" }.concatWithKeys()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { expr { "age"<Int>() >= 18 } named "isAdult" }.concatWithKeys()
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/concatWithKeysOnGroupBy_properties.html" width="100%"/>

#### into on GroupBy {collapsible="true"}
<!---FUN intoOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.into("ages") { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").into("ages") { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/intoOnGroupBy_properties.html" width="100%"/>

#### values on GroupBy {collapsible="true"}
##### all columns
<!---FUN valuesOnGroupByAllColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.values()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").values()
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/valuesOnGroupByAllColumns_properties.html" width="100%"/>

##### selected columns
<!---FUN valuesOnGroupBySelectedColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.values { name and age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").values("name", "age")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/valuesOnGroupBySelectedColumns_properties.html" width="100%"/>

##### rename columns
<!---FUN valuesOnGroupByRenameColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.values { age into "ages" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").values { "age" into "ages" }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/valuesOnGroupByRenameColumns_properties.html" width="100%"/>

#### count on GroupBy {collapsible="true"}
<!---FUN countOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.count()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").count()
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/countOnGroupBy_properties.html" width="100%"/>

#### aggregate on GroupBy {collapsible="true"}
<!---FUN aggregateOnGroupBy-->
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
<inline-frame src="resources/aggregateOnGroupBy_properties.html" width="100%"/>

If only one aggregation function is used, the column name can be omitted:

<!---FUN aggregateOnGroupByWithoutInto-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate { maxBy { age }.name }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate { maxBy("age")["name"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/aggregateOnGroupByWithoutInto_properties.html" width="100%"/>

### Aggregation statistics
[`Aggregation statistics`](summaryStatistics.md) are predefined shortcuts for common statistical aggregations such as sum, mean, median, and others.

Each function computes a statistic across the [`rows`](DataRow.md) of a group and returns the result as a new [`column`](DataColumn.md) (or several columns) in the resulting [`DataFrame`](DataFrame.md).

The following aggregation statistics are available:
* [`count`](count.md);
* [`max`](minmax.md) / maxOf / maxFor; 
* [`min`](minmax.md) / minOf / minFor; 
* [`sum`](sum.md) / sumOf / sumFor; 
* [`mean`](mean.md) / meanOf / meanFor; 
* [`std`](std.md) / stdOf / stdFor; 
* [`median`](median.md) / medianOf / medianFor; 
* [`percentile`](percentile.md) / percentileOf / percentileFor.

To compute one or several [`statistics`](summaryStatistics.md) per every group of `GroupBy`, use the [`aggregate`](groupBy.md#aggregation) function.

The functions `max`, `maxOf`, and `maxFor` differ as follows. They all calculate the maximum of values, but:
* `max` computes it on the selected columns. If more than one column is selected, for each group it computes one maximum value among all selected columns.
* `maxOf` computes it by a `row expression`: the expression is calculated for each row of the group and the maximum value is returned.
* `maxFor` computes it for each of the selected columns within each group. If more than one column is selected, for each group it computes the maximum value for each selected column separately.

Similar logic applies to other statistics.

#### Direct aggregations
Most common aggregation functions can be computed directly at [`GroupBy DataFrame`](groupBy.md#transformation).

##### Examples of direct aggregations {collapsible="true"}
###### max {collapsible="true"}
<!---FUN maxOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.max() // max for every column with mutually comparable values
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").max() // max for every column with mutually comparable values
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/maxOnGroupBy_properties.html" width="100%"/>

<!---FUN maxSelectedOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.max { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").max("age", "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/maxSelectedOnGroupBy_properties.html" width="100%"/>

<!---FUN maxForOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.maxFor { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").maxFor("age", "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/maxForOnGroupBy_properties.html" width="100%"/>

<!---FUN maxOfOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.maxOf { if (age < 30) weight else null }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").maxOf { if ("age"<Int>() < 30) "weight"<Int>() else null }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/maxOfOnGroupBy_properties.html" width="100%"/>

<!---FUN maxOnGroupByNameLength-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }
    .max {
        name.firstName.map {
            it.length
        } and name.lastName.map { it.length }
    } // maximum length of firstName or lastName into column "max"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").max {
    "name"["firstName"]<String>().map { it.length } and "name"["lastName"]<String>().map { it.length }
} // maximum length of firstName or lastName into column "max"
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/maxOnGroupByNameLength_properties.html" width="100%"/>

###### min {collapsible="true"}
<!---FUN minOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.min { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").min("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/minOnGroupBy_properties.html" width="100%"/>

<!---FUN minForOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }
    .minFor {
        (age into "minAge") and (weight into "minWeight")
    } // min age into column "min age", min weight into column "min weight"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city")
    .minFor {
        ("age"<Int>() into "minAge") and ("weight"<Int?>() into "minWeight")
    } // min age into column "min age", min weight into column "min weight"
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/minForOnGroupBy_properties.html" width="100%"/>

###### sum {collapsible="true"}
<!---FUN sumOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.sum("totalWeight") { weight } // sum of weights into column "total weight"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").sum("weight", name = "totalWeight") // sum of weights into column "total weight"
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/sumOnGroupBy_properties.html" width="100%"/>

###### mean {collapsible="true"}
<!---FUN meanOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.mean() // mean for every numeric column
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").mean() // mean for every numeric column
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/meanOnGroupBy_properties.html" width="100%"/>

<!---FUN meanOfOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.meanOf("meanRatio") { weight?.div(age) } // mean of weight/age into column "mean ratio"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").meanOf("meanRatio") {
    "weight"<Int?>()?.div("age"<Int>())
} // mean of weight/age into column "mean ratio"
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/meanOfOnGroupBy_properties.html" width="100%"/>

###### std {collapsible="true"}
<!---FUN stdOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.std { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").std("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/stdOnGroupBy_properties.html" width="100%"/>

###### median {collapsible="true"}
<!---FUN medianOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.median { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").median("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/medianOnGroupBy_properties.html" width="100%"/>

<!---FUN medianForOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }
    .medianFor { age and weight } // median age into column "age", median weight into column "weight"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city")
    .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/medianForOnGroupBy_properties.html" width="100%"/>

###### percentile {collapsible="true"}
<!---FUN percentileOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.percentile(25.0) { age }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").percentile(25.0) { "age"<Int>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/percentileOnGroupBy_properties.html" width="100%"/>

## Pivot + GroupBy
`GroupBy` can be pivoted with [`pivot`](pivot.md#pivot-groupby) method. It will produce a `PivotGroupBy`.

<!---FUN pivotOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { isHappy }.pivot { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("isHappy").pivot { "name"["firstName"]<String>() }
```

</tab></tabs>
<!---END-->
<inline-frame src="resources/pivotOnGroupBy_properties.html" width="100%"/>

For more information, see [`pivot + groupBy`](pivot.md#pivot-groupby)
