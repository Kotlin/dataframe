[//]: # (title: Statistics)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Available statistics:
* [count](count.md)
* [sum](sum.md)
* [min/max](minmax.md)
* [mean](mean.md)
* [median](median.md)
* [std](std.md)

Every statistic can be used in aggregations of:
* [`DataFrame`](DataFrame.md)
* [`DataColumn`](DataColumn.md)
* [`GroupBy`](#groupby-statistics)
* [`Pivot`](pivot.md)
* [`PivotGroupBy`](pivot.md#pivot-groupby)

<!---FUN statisticAggregations-->

```kotlin
df.mean()
df.age.sum()
df.groupBy { city }.mean()
df.pivot { city }.median()
df.pivot { city }.groupBy { name.lastName }.std()
```

<!---END-->

[sum](sum.md), [mean](mean.md), [std](std.md) are available for numeric columns of types `Int`, `Double`, `Float`, `BigDecimal`, `Long`, `Byte`.

[min/max](minmax.md), [median](median.md) are available for `Comparable` columns.

When statistics `x` is applied to several columns, it can be computed in several modes:
* `x(): DataRow` computes separate value per every suitable column
* `x { columns }: Value` computes single value across all given columns 
* `xFor { columns }: DataRow` computes separate value per every given column
* `xOf { rowExpression }: Value` computes single value across results of [row expression](DataRow.md#row-expressions) evaluated for every row

[min](minmax.md) and [max](minmax.md) statistics have additional mode `by`:
* `minBy { rowExpression }: DataRow` finds a row with minimal result of [expression](DataRow.md#row-expressions)

<!---FUN statisticModes-->

```kotlin
df.sum() // sum of values per every numeric column
df.sum { age and weight } // sum of all values in `age` and `weight`
df.sumFor { age and weight } // sum of values per `age` and `weight` separately
df.sumOf { (weight ?: 0) / age } // sum of expression evaluated for every row
```

<!---END-->

### groupBy statistics

When statistics is applied to `GroupBy`, it is computed for every data group. 

If statistic is applied in a mode that returns a single value for every data group, it will be stored in a single column named by statistic name.

<!---FUN statisticGroupBySingle-->

```kotlin
df.groupBy { city }.mean { age } // [`city`, `mean`]
df.groupBy { city }.meanOf { age / 2 } // [`city`, `mean`]
```

<!---END-->

You can also pass custom name for aggregated column:

<!---FUN statisticGroupBySingleNamed-->

```kotlin
df.groupBy { city }.mean("mean age") { age } // [`city`, `mean age`]
df.groupBy { city }.meanOf("custom") { age / 2 } // [`city`, `custom`]
```

<!---END-->

If statistic is applied in a mode that returns separate value per every column in data group, aggregated values will be stored in columns with original column names.

<!---FUN statisticGroupByMany-->

```kotlin
df.groupBy { city }.meanFor { age and weight } // [`city`, `age`, `weight`]
df.groupBy { city }.mean() // [`city`, `age`, `weight`, ...]
```

<!---END-->

### pivot statistics

When statistics is applied to `Pivot` or `PivotGroupBy`, it is computed for every data group.

If statistic is applied in a mode that returns a single value for every data group, it will be stored in matrix cell without any name.

<!---FUN statisticPivotSingle-->

```kotlin
df.groupBy { city }.pivot { name.lastName }.mean { age }
df.groupBy { city }.pivot { name.lastName }.meanOf { age / 2 }
```

<!---END-->

If statistic is applied in such a way that it returns separate value per every column in data group, every cell in matrix will contain `DataRow` with values for every aggregated column.

<!---FUN statisticPivotMany-->

```kotlin
df.groupBy { city }.pivot { name.lastName }.meanFor { age and weight }
df.groupBy { city }.pivot { name.lastName }.mean()
```

<!---END-->

To group columns in aggregation results not by pivoted values, but by aggregated columns, apply `separate` flag:

<!---FUN statisticPivotManySeparate-->

```kotlin
df.groupBy { city }.pivot { name.lastName }.meanFor(separate = true) { age and weight }
df.groupBy { city }.pivot { name.lastName }.mean(separate = true)
```

<!---END-->
