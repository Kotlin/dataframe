[//]: # (title: cumSum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes cumulative sum of values in selected columns of `DataFrame` or in single `DataColumn`.

```kotlin
cumSum(skipNA = true) { columns }
```

Returns a `DataFrame` or `DataColumn` containing the cumulative sum.

**Parameters:**
* `skipNA: Boolean = true` - ignore `NA` (`null` or `NaN`) values. When `false`, all values after first `NA` will be `NaN` (for `Double` and `Float` columns) or `null` (for integer columns). 

<!---FUN cumSum-->

```kotlin
df.cumSum { weight }
df.weight.cumSum()
```

<!---END-->
