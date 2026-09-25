[//]: # (title: cumSum)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Computes the cumulative sum of values in the selected columns.

```text
cumSum(skipNA = true) [ { columns } ]
```

Returns a [`DataFrame`](DataFrame.md) or [`DataColumn`](DataColumn.md) containing the cumulative sum.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

**Parameters:**
* `skipNA` — when `true`, ignores [`NA` values](nanAndNa.md#na) (`null` or `NaN`). 
  When `false`, all values after first `NA` will be `NaN` (for `Double` and `Float` columns) or `null` (for integer columns).

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

### Big numbers

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CumSumSamples-->

> `java.math.BigDecimal` and `java.math.BigInteger` are not supported:
> `cumSum` throws an exception at runtime for such columns.
> Compute the cumulative sum manually with Kotlin standard library methods and Java big number arithmetic,
> or [`convert`](convert.md) the column to a primitive type first.
> {style="warning"}

For a `BigDecimal` column `amount`, the exact cumulative sum can be computed with Java `BigDecimal` arithmetic:

<!---FUN cumSumBigNumbersManually-->

```kotlin
// exact cumulative sum, computed with Java `BigDecimal` arithmetic
df.amount.toList().runningReduce(BigDecimal::add).toColumn("amount")
```

<!---END-->

Alternatively, [`convert`](convert.md) the column to a primitive type first — at the cost of precision:

<!---FUN cumSumBigNumbersConverted-->

```kotlin
// approximate cumulative sum, computed after converting the column to `Double`
df.convert { amount }.toDouble().cumSum { amount }
```

<!---END-->
