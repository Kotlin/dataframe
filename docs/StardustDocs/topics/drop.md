[//]: # (title: drop / dropNulls / dropNaNs / dropNA)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

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
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.dropWhere.html" width="100%"/>
<!---END-->

## dropNulls

Remove rows with `null` values. This is a DataFrame equivalent of `filterNotNull`.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNulls-->

```kotlin
df.dropNulls() // remove rows with null value in any column
df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
df.dropNulls { city } // remove rows with null value in 'city' column
df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNulls.html" width="100%"/>
<!---END-->

## dropNaNs

Remove rows with [`NaN` values](nanAndNa.md#nan) (`Double.NaN` or `Float.NaN`).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNaNs-->

```kotlin
df.dropNaNs() // remove rows containing NaN in any column
df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
df.dropNaNs { weight } // remove rows where 'weight' is NaN
df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNaNs.html" width="100%"/>
<!---END-->

## dropNA

Remove rows with [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, or `Float.NaN`).

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNA-->

```kotlin
df.dropNA() // remove rows containing null or NaN in any column
df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
df.dropNA { weight } // remove rows where 'weight' is null or NaN
df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNA.html" width="100%"/>
<!---END-->
