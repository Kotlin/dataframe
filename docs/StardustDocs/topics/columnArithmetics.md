[//]: # (title: Column arithmetics)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Kotlin DataFrame provides operators for applying simple arithmetic, logical, string, and comparison operations
to [`DataColumn`](DataColumn.md) and `ColumnReference` values. These operations include, for example, adding a value 
to each cell in a column, multiplying a column by a value, and comparing elements in a column with a value.

## When useful

In most transformations, these column operations are usually not the preferred approach in Kotlin DataFrame, 
because the library provides row-based APIs such as [`add`](add.md), [`update`](update.md), and [`map`](map.md), 
which are usually recommended.

Also, the [`expr`](ColumnSelectors.md#expr-column-expression) function 
is particularly useful in this context, as it allows you to write row expressions 
inside the [`Columns Selection DSL`](ColumnSelectors.md).
In other words, [`expr`](ColumnSelectors.md#expr-column-expression) works as an adapter 
between a column selector and a row expression.

For example,
<!---FUN columnArithmetics_groupBy_without_expr-->

```kotlin
orders.groupBy { status + " orders"  }
```

<!---END-->
is equivalent to
<!---FUN columnArithmetics_groupBy_with_expr-->

```kotlin
orders.groupBy { expr("status") { status + " orders" } }
```

<!---END-->

but in the first case, `status` is used as a [`DataColumn`](DataColumn.md) of String values, 
and in the second case, `status` is treated as a String.

However, column arithmetics might still be useful in some cases. 
For example, when building temporary plotting expressions. 
If distance is stored in meters, but you need to [`plot`](https://kotlin.github.io/kandy/welcome.html) it in kilometers, 
you can convert it directly in the plotting expression without having to create a temporary column in your dataframe just for plotting. 

<!---FUN columnArithmetics_kandy-->

```kotlin
df.plot {
    line {
        x(day)
        y((distanceMeters / 1000.0) named "distanceKm")
    }
}
```

<!---END-->

## Not
Negates Boolean values in a [`DataColumn`](DataColumn.md) (`ColumnReference`). 
Returns a result of the same type.

<!---FUN columnArithmetics_not-->

```kotlin
df.select { !isHappy }
// or
!df.isHappy
```

<!---END-->

Nullable Boolean columns preserve `null` values.

## Plus
Adding a value to a column and concatenation of a column with a `String` are supported. 
In all cases, a [`DataColumn`](DataColumn.md) (`ColumnReference`) is returned.

### Column plus value or value plus column
Adds the value to each element of the column. The value can appear on either side of the operator.

<!---FUN columnArithmetics_addition-->

```kotlin
transactions.amount + 10
5.0 + transactions.amount
```

<!---END-->

`null` values are not changed by this operation.

### Column plus String
Converts each element of the column to a String and concatenates it with the value.

<!---FUN columnArithmetics_concatenation-->

```kotlin
weather.select { temperature + " °C" }
// or
weather.temperature + " °C"
```

<!---END-->

`null` values are converted to the string `"null"`.

## Minus

### Column minus value
Subtracts the value from each element of the column.

<!---FUN columnArithmetics_column_minus_value-->

```kotlin
transactions.amount - 10.0
```

<!---END-->

`null` values are not changed by this operation.

### Value minus column
Subtracts each element of the column from the value and returns a [`DataColumn`](DataColumn.md) (`ColumnReference`) 
with the results of the subtractions.

<!---FUN columnArithmetics_value_minus_column-->

```kotlin
100 - transactions.amount
```

<!---END-->

`null` values from the original column remain `null` values in the resulting column.

## Unary minus
Flips the sign of each element in the column.

<!---FUN columnArithmetics_unary_minus-->

```kotlin
-transactions.amount
```

<!---END-->

`null` values are not changed by this operation.

## Times
Multiplies each element of the column by the value.

<!---FUN columnArithmetics_times-->

```kotlin
routes.distanceKm * 1000.0
products.price * BigDecimal("1.20")
```

<!---END-->

`null` values are not changed by this operation.

## Div
Division by zero follows Kotlin semantics of the underlying type.

### Divide column by value
Divides each element of the column by the value.

<!---FUN columnArithmetics_column_div_value-->

```kotlin
products.weightGrams / 1000.0
```

<!---END-->

### Divide value by column
Divides the value by each element of the column and returns a [`DataColumn`](DataColumn.md) (`ColumnReference`)
with the results of the divisions.

<!---FUN columnArithmetics_value_div_column-->

```kotlin
40 / tasks.hoursPerTask
```

<!---END-->

If an element of the column is `null`, the corresponding value in the resulting column is also `null`.

## Compare
`eq`, `neq`, `gt`, and `lt` are available for [`DataColumn`](DataColumn.md). 
Each of them returns a [`DataColumn`](DataColumn.md) of `Boolean` values.

### eq
Compares each element of a [`DataColumn`](DataColumn.md) with the value for equality using the `==` operator.

<!---FUN columnArithmetics_eq-->

```kotlin
orders.status eq "canceled"
```

<!---END-->

### neq
Compares each element of a [`DataColumn`](DataColumn.md) with the value for inequality using the `!=` operator.

<!---FUN columnArithmetics_neq-->

```kotlin
orders.status neq "completed"
```

<!---END-->

### gt
Compares each element of a [`DataColumn`](DataColumn.md) with the value using the `>` operator.

<!---FUN columnArithmetics_gt-->

```kotlin
orders.cost gt 1000.0
```

<!---END-->

### lt
Compares each element of a [`DataColumn`](DataColumn.md) with the value using the `<` operator.

<!---FUN columnArithmetics_lt-->

```kotlin
orders.cost lt 20.0
```

<!---END-->
