[//]: # (title: cumSum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes cumulative sum of values in selected columns.

```text
cumSum(skipNA = true) [ { columns } ]
```

Returns a [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md) containing the cumulative sum.

**Parameters:**
* `skipNA` — when `true`, ignores `NA` (`null` or `NaN`) values. When `false`, all values after first `NA` will be `NaN` (for `Double` and `Float` columns) or `null` (for integer columns).

**Available for:**
* [`DataFrame`](DataFrame.md)
* [`DataColumn`](DataColumn.md)
* [`GroupBy DataFrame`](groupBy.md#transformation) — cumulative sum per every data group

<!---FUN cumSum-->

```kotlin
df.cumSum { weight }
df.weight.cumSum()
df.groupBy { city }.cumSum { weight }.concat()
```

<!---END-->
