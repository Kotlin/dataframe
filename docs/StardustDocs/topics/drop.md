[//]: # (title: drop / dropNulls / dropNaNs / dropNA)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DropNullsNansNaSamples-->

The examples on this page use the following dataframe:
<!---FUN dropDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/dropDf.html" width="100%" height="500px"></inline-frame>

## drop

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

<!---FUN dropDataColumnByPredicate-->

```kotlin
df.age.drop { it < 20 }
```

<!---END-->
<inline-frame src="./resources/dropDataColumnByPredicate.html" width="100%" height="500px"></inline-frame>

## dropNulls

Removes rows with `null` values. This is a DataFrame equivalent of `filterNotNull`.

See also [fillNulls](fill.md#fillnulls), which replaces `null` values instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNulls-->

```kotlin
// remove rows with null value in any column
df.dropNulls()
```

<!---END-->
<inline-frame src="./resources/dropNulls.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsWhereAllNull-->

```kotlin
// remove rows with null values in all columns
df.dropNulls(whereAllNull = true)
```

<!---END-->
<inline-frame src="./resources/dropNullsWhereAllNull.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsSelector-->

```kotlin
// remove rows with null value in 'city' column
df.dropNulls { city }
```

<!---END-->
<inline-frame src="./resources/dropNullsSelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsSelectorSeveralCols-->

```kotlin
// remove rows with null value in 'city' OR 'weight' columns
df.dropNulls { city and weight }
```

<!---END-->
<inline-frame src="./resources/dropNullsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNullsWhereAllNullSelector-->

```kotlin
// remove rows with nulls in both columns
df.dropNulls(whereAllNull = true) { city and weight }
```

<!---END-->
<inline-frame src="./resources/dropNullsWhereAllNullSelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes `null` values from this [`DataColumn`](DataColumn.md), 
adjusting the type accordingly.

<!---FUN dropNullsDataColumn-->

```kotlin
df.weight.dropNulls()
```

<!---END-->
<inline-frame src="./resources/dropNullsDataColumn.html" width="100%" height="500px"></inline-frame>

## dropNaNs

Removes rows with [`NaN` values](nanAndNa.md#nan) (`Double.NaN` or `Float.NaN`).

See also [fillNaNs](fill.md#fillnans), which replaces [`NaN` values](nanAndNa.md#nan) instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNaNs-->

```kotlin
// remove rows containing NaN in any column
df.dropNaNs()
```

<!---END-->
<inline-frame src="./resources/dropNaNs.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsWhereAllNaN-->

```kotlin
// remove rows with NaN in all columns
df.dropNaNs(whereAllNaN = true)
```

<!---END-->
<inline-frame src="./resources/dropNaNsWhereAllNaN.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsSelector-->

```kotlin
// remove rows where 'weight' is NaN
df.dropNaNs { weight }
```

<!---END-->
<inline-frame src="./resources/dropNaNsSelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsSelectorSeveralCols-->

```kotlin
// remove rows where either 'age' or 'weight' is NaN
df.dropNaNs { age and weight }
```

<!---END-->
<inline-frame src="./resources/dropNaNsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNaNsWhereAllNaNSelector-->

```kotlin
// remove rows where both 'age' and 'weight' are NaN
df.dropNaNs(whereAllNaN = true) { age and weight }
```

<!---END-->
<inline-frame src="./resources/dropNaNsWhereAllNaNSelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes [`NaN` values](nanAndNa.md#nan) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.

<!---FUN dropNaNsDataColumn-->

```kotlin
val values by columnOf(1.0, Double.NaN, 2.0, Double.NaN)
values.dropNaNs()
```

<!---END-->
<inline-frame src="./resources/dropNaNsDataColumn.html" width="100%" height="500px"></inline-frame>

## dropNA

Removes rows with [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, or `Float.NaN`).

See also [fillNA](fill.md#fillna), which replaces [`NA` values](nanAndNa.md#na) instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNA-->

```kotlin
// remove rows containing null or NaN in any column
df.dropNA()
```

<!---END-->
<inline-frame src="./resources/dropNA.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNAWhereAllNA-->

```kotlin
// remove rows with null or NaN in all columns
df.dropNA(whereAllNA = true)
```

<!---END-->
<inline-frame src="./resources/dropNAWhereAllNA.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNASelector-->

```kotlin
// remove rows where 'weight' is null or NaN
df.dropNA { weight }
```

<!---END-->
<inline-frame src="./resources/dropNASelector.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNASelectorSeveralCols-->

```kotlin
// remove rows where either 'age' or 'weight' is null or NaN
df.dropNA { age and weight }
```

<!---END-->
<inline-frame src="./resources/dropNASelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---FUN dropNAWhereAllNASelector-->

```kotlin
// remove rows where both 'age' and 'weight' are null or NaN
df.dropNA(whereAllNA = true) { age and weight }
```

<!---END-->
<inline-frame src="./resources/dropNAWhereAllNASelector.html" width="100%" height="500px"></inline-frame>

If called on a [`DataColumn`](DataColumn.md), removes [`NA` values](nanAndNa.md#na) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.

<!---FUN dropNADataColumn-->

```kotlin
val values by columnOf(1.0, null, Double.NaN, 2.0)
values.dropNA()
```

<!---END-->
<inline-frame src="./resources/dropNADataColumn.html" width="100%" height="500px"></inline-frame>
