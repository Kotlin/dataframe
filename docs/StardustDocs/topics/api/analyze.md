[//]: # (title: Analyze)

<!---IMPORT docs.api.Analyze-->

## Basic statistics

<!---FUN basicInfo-->

To count number of rows that satisfy to [condition](rows.md#row-conditions) use `count`:

<!---FUN count-->

### Single column statistics

* `sum` and `mean` are available for numeric columns
* `min`, `max` and `median` are available for comparable columns

<!---FUN columnStats-->

### Multiple columns statistics

When several columns are specified, statistical operations compute a single value across all given columns  

<!---FUN multipleColumnsStat-->

To compute statistics separately for every column, use operations with `-for` suffix:

<!---FUN columnsFor-->

### Row expression statistics

To compute statistics for some expression evaluated for every row, you should use operations with `-of` suffix:

<!---FUN ofExpressions-->

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
