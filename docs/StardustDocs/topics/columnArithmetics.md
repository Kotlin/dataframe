[//]: # (title: Column arithmetics)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Kotlin DataFrame provides operators for applying simple arithmetic, logical, string, and comparison operations
to [`DataColumn`](DataColumn.md) and `ColumnReference` values. These operations include, for example, adding a value to a column,
multiplying a column by a value, and comparing elements in a column with a value.

## When useful

In most transformations, these column operations are usually not the preferred approach in Kotlin DataFrame, 
because the library provides row-based APIs such as [`add`](add.md), [`update`](update.md), [`map`](map.md), and `expr`, 
which are usually recommended.

However, column arithmetics might still be useful in some cases. 
For example, when building temporary plotting expressions. 
If distance is stored in meters, but you need to [`plot`](https://kotlin.github.io/kandy/welcome.html) it in kilometers, 
you can convert it directly in the plotting expression without having to create a temporary column in your dataframe just for plotting. 

```kotlin
df.plot {
    line {
        x(day)
        y((distanceMeters / 1000.0) named "distanceKm")
    }
}
```

## Not
Negates Boolean values in a [`DataColumn`](DataColumn.md) (`ColumnReference`). 
Returns a result of the same type.

<!---FUN columnArithmetics_not-->

```kotlin
!df.isHappy
// or
df.isHappy.not()
```

<!---END-->

Nullable Boolean columns preserve `null` values.

## Plus
Adding a value to a column and concatenation of a column with a `String` are supported. 
In all cases, a [`DataColumn`](DataColumn.md) (`ColumnReference`) is returned.

### Column plus value or value plus column
Adds the value to each element of the column. The value can appear on either side of the operator.

```kotlin
df.amount + 10
5.0 + df.amount
```

`null` values are not changed by this operation.

### Column plus String
Converts each element of the column to a String and concatenates it with the value.

```kotlin
df.amount + "€"
```

`null` values are converted to the string `"null"`.

## Minus

### Column minus value
Subtracts the value from each element of the column.

```kotlin
df.amount - 10.0
```

`null` values are not changed by this operation.

### Value minus column
Subtracts each element of the column from the value and returns a [`DataColumn`](DataColumn.md) (`ColumnReference`) 
with the results of the subtractions.

```kotlin
100 - df.amount
```

`null` values from the original column remain `null` values in the resulting column.

## Unary minus
Flips the sign of each element in the column.

```kotlin
-df.expenses
```

`null` values are not changed by this operation.

## Times
Multiplies each element of the column by the value.

```kotlin
df.distanceKm * 1000.0
df.price * BigDecimal("1.20")
```

`null` values are not changed by this operation.

## Div
Division by zero follows Kotlin semantics of the underlying type.

### Divide column by value
Divides each element of the column by the value.

```kotlin
df.distanceMeters / 1000.0
```

### Divide value by column
Divides the value by each element of the column and returns a [`DataColumn`](DataColumn.md) (`ColumnReference`)
with the results of the divisions.

```kotlin
40 / df.hoursPerTask
```
If an element of the column is `null`, the corresponding value in the resulting column is also `null`.

## Compare
`eq`, `neq`, `gt`, and `lt` are available for [`DataColumn`](DataColumn.md). 
Each of them returns a [`DataColumn`](DataColumn.md) of `Boolean` values.

### eq
Compares each element of a [`DataColumn`](DataColumn.md) with the value for equality using the `==` operator.

```kotlin
df.status eq "canceled"
```

### neq
Compares each element of a [`DataColumn`](DataColumn.md) with the value for inequality using the `!=` operator.

```kotlin
df.status neq "completed"
```

### gt
Compares each element of a [`DataColumn`](DataColumn.md) with the value using the `>` operator.

```kotlin
df.orderCost gt 1000
```

### lt
Compares each element of a [`DataColumn`](DataColumn.md) with the value using the `<` operator.

```kotlin
df.orderCost lt 20
```
