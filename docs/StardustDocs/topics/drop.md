[//]: # (title: drop / dropNulls / dropNaNs / dropNA)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DropNullsNansNa-->

Removes all rows that satisfy [row condition](DataRow.md#row-conditions)

**Related operations**: [](filterRows.md)

<!---FUN dropWhere-->
<tabs>
<tab title="Properties">

```kotlin
df.drop { weight == null || city == null }
```

</tab>
<tab title="Strings">

```kotlin
df.drop { it["weight"] == null || it["city"] == null }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/dropWhere_properties.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes all the values that match the predicate 
and returns a [`DataColumn`](DataColumn.md) containing the values that do not match the predicate.

## dropNulls

Remove rows with `null` values. This is a DataFrame equivalent of `filterNotNull`.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNulls-->

```kotlin
df.dropNulls() // remove rows with null value in any column
```

<!---END-->
<inline-frame src="./resources/dropNulls.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsWhereAllNull-->

```kotlin
df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
```

<!---END-->
<inline-frame src="./resources/dropNullsWhereAllNull.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsSelector-->

```kotlin
df.dropNulls { city } // remove rows with null value in 'city' column
```

<!---END-->
<inline-frame src="./resources/dropNullsSelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsSelectorSeveralCols-->

```kotlin
df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
```

<!---END-->
<inline-frame src="./resources/dropNullsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsWhereAllNullSelector-->

```kotlin
df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
```

<!---END-->
<inline-frame src="./resources/dropNullsWhereAllNullSelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes `null` values from this [`DataColumn`](DataColumn.md), 
adjusting the type accordingly.

## dropNaNs

Remove rows with [`NaN` values](nanAndNa.md#nan) (`Double.NaN` or `Float.NaN`).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNaNs-->

```kotlin
df.dropNaNs() // remove rows containing NaN in any column
```

<!---END-->
<inline-frame src="./resources/dropNaNs.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsWhereAllNaN-->

```kotlin
df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
```

<!---END-->
<inline-frame src="./resources/dropNaNsWhereAllNaN.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsSelector-->

```kotlin
df.dropNaNs { weight } // remove rows where 'weight' is NaN
```

<!---END-->
<inline-frame src="./resources/dropNaNsSelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsSelectorSeveralCols-->

```kotlin
df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
```

<!---END-->
<inline-frame src="./resources/dropNaNsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsWhereAllNaNSelector-->

```kotlin
df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
```

<!---END-->
<inline-frame src="./resources/dropNaNsWhereAllNaNSelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes [`NaN` values](nanAndNa.md#nan) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.

## dropNA

Remove rows with [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, or `Float.NaN`).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNA-->

```kotlin
df.dropNA() // remove rows containing null or NaN in any column
```

<!---END-->
<inline-frame src="./resources/dropNA.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNAWhereAllNA-->

```kotlin
df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
```

<!---END-->
<inline-frame src="./resources/dropNAWhereAllNA.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNASelector-->

```kotlin
df.dropNA { weight } // remove rows where 'weight' is null or NaN
```

<!---END-->
<inline-frame src="./resources/dropNASelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNASelectorSeveralCols-->

```kotlin
df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
```

<!---END-->
<inline-frame src="./resources/dropNASelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNAWhereAllNASelector-->

```kotlin
df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
```

<!---END-->
<inline-frame src="./resources/dropNAWhereAllNASelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes [`NA` values](nanAndNa.md#na) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.
