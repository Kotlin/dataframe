[//]: # (title: Summary statistics)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Basic summary statistics:
* [count](count.md)
* [valueCounts](valueCounts.md)

Aggregating summary statistics:
* [sum](sum.md)
* [min/max](minmax.md)
* [mean](mean.md)
* [std](std.md)
* [median](median.md)
* [percentile](percentile.md)

Every summary statistics can be used in aggregations of:
* [`DataFrame`](DataFrame.md)
* [`DataColumn`](DataColumn.md)
* [`GroupBy DataFrame`](#groupby-statistics)
* [`Pivot`](#pivot-statistics)
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

[sum](sum.md), [mean](mean.md), [std](std.md) are available for (primitive) number columns of types 
`Int`, `Double`, `Float`, `Long`, `Byte`, `Short`, and any mix of those.

[min/max](minmax.md), [median](median.md), and [percentile](percentile.md) are available for self-comparable columns 
(so columns of type `T : Comparable<T>`, like `DateTime`, `String`, `Int`, etc.)
which includes all primitive number columns, but no mix of different number types.

In all cases, `null` values are ignored.

`NaN` values can optionally be ignored by setting the `skipNaN` flag to `true`.
When it's set to `false`, a `NaN` in the input will be propagated to the result.

Big numbers (`BigInteger`, `BigDecimal`) are generally **not** supported for statistics.
Please [convert](convert.md) them to primitive types before using statistics.

When statistics `x` is applied to several columns, it can be computed in several modes:
* `x(): DataRow` computes separate value per every suitable column
* `x { columns }: Value` computes single value across all given columns 
* `xFor { columns }: DataRow` computes separate value per every given column
* `xOf { rowExpression }: Value` computes single value across results of [row expression](DataRow.md#row-expressions) evaluated for every row

(See [column selectors](ColumnSelectors.md) for how to select the columns for these operations)

[min/max](minmax.md), [median](median.md), and [percentile](percentile.md) have additional mode `by`:
* `minBy { rowExpression }: DataRow` finds a row with the minimal result of the [rowExpression](DataRow.md#row-expressions)
* `medianBy { rowExpression }: DataRow` finds a row where the median lies based on the results of the [rowExpression](DataRow.md#row-expressions)

To perform statistics for a single row, see [row statistics](rowStats.md).

<!---FUN statisticModes-->

```kotlin
df.sum() // sum of values per every numeric column
df.sum { age and weight } // sum of all values in `age` and `weight`
df.sumFor(skipNaN = true) { age and weight } // sum of values per `age` and `weight` separately
df.sumOf { (weight ?: 0) / age } // sum of expression evaluated for every row
```

<!---END-->

### groupBy statistics

When statistics are applied to [`GroupBy DataFrame`](groupBy.md#transformation), it is computed for every data group. 

If a statistic is applied in a mode that returns a single value for every data group,
it will be stored in a single column named according to the statistic name.

<!---FUN statisticGroupBySingle-->

```kotlin
df.groupBy { city }.mean { age } // [`city`, `mean`]
df.groupBy { city }.meanOf { age / 2 } // [`city`, `mean`]
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticGroupBySingle.html" width="100%"/>
<!---END-->

You can also pass a custom name for the aggregated column:

<!---FUN statisticGroupBySingleNamed-->

```kotlin
df.groupBy { city }.mean("mean age") { age } // [`city`, `mean age`]
df.groupBy { city }.meanOf("custom") { age / 2 } // [`city`, `custom`]
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticGroupBySingleNamed.html" width="100%"/>
<!---END-->

If a statistic is applied in a mode that returns a separate value for every column in a data group,
aggregated values will be stored in columns with original column names.

<!---FUN statisticGroupByMany-->

```kotlin
df.groupBy { city }.meanFor { age and weight } // [`city`, `age`, `weight`]
df.groupBy { city }.mean() // [`city`, `age`, `weight`, ...]
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticGroupByMany.html" width="100%"/>
<!---END-->

### pivot statistics

When statistics are applied to `Pivot` or `PivotGroupBy`, it is computed for every data group.

If a statistic is applied in a mode that returns a single value for every data group,
it will be stored in a `DataFrame` cell without any name.

<!---FUN statisticPivotSingle-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.pivot { name.lastName }.mean { age }
df.groupBy { city }.pivot { name.lastName }.meanOf { age / 2.0 }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").pivot { "name"["lastName"] }.mean("age")
df.groupBy("city").pivot { "name"["lastName"] }.meanOf { "age"<Int>() / 2.0 }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticPivotSingle.html" width="100%"/>
<!---END-->

If a statistic is applied in such a way that it returns separate value per every column in a data group, 
every cell in the nested dataframe will contain [`DataRow`](DataRow.md) with values for every aggregated column.

<!---FUN statisticPivotMany-->

```kotlin
df.groupBy { city }.pivot { name.lastName }.meanFor { age and weight }
df.groupBy { city }.pivot { name.lastName }.mean()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticPivotMany.html" width="100%"/>
<!---END-->

To group columns in aggregation results not by pivoted values, but by aggregated columns, apply the `separate` flag:

<!---FUN statisticPivotManySeparate-->

```kotlin
df.groupBy { city }.pivot { name.lastName }.meanFor(separate = true) { age and weight }
df.groupBy { city }.pivot { name.lastName }.mean(separate = true)
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.statisticPivotManySeparate.html" width="100%"/>
<!---END-->
