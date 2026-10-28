[//]: # (title: drop / dropNulls / dropNaNs / dropNA)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.DropNullsNansNaSamples-->

**API reference**: [`drop`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/drop.html)

The examples on this page use the following dataframe:

<!---FUN dropDf-->

```kotlin
df
```

<inline-frame src="./resources/dropDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

## drop

Removes all rows that satisfy the [row condition](DataRow.md#row-conditions).

**Related operations**: [](filterRows.md)

<!---FUN dropWhere-->
<tabs>
<tab title="Properties">

```kotlin
df.drop { weight == null || name == null }
```

</tab>
<tab title="Strings">

```kotlin
df.drop { it["weight"] == null || it["name"] == null }
```

</tab></tabs>
<inline-frame src="./resources/dropWhere.html" width="100%" height="500px"></inline-frame>

<!---END-->

If called on a [`DataColumn`](DataColumn.md), removes all the values that match the predicate 
and returns a [`DataColumn`](DataColumn.md) containing the values that do not match the predicate.

<!---FUN dropDataColumnByPredicate-->

```kotlin
df.weight.drop { it != null && it < 60 }
```

<inline-frame src="./resources/dropDataColumnByPredicate.html" width="100%" height="500px"></inline-frame>

<!---END-->

## dropNulls

Removes rows with `null` values. This is a DataFrame equivalent of `filterNotNull`.

See also [fillNulls](fill.md#fillnulls), which replaces `null` values instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNulls-->

```kotlin
// remove rows with null value in any column
df.dropNulls()
```

<inline-frame src="./resources/dropNulls.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNullsWhereAllNull-->

```kotlin
// remove rows with null values in all columns
df.dropNulls(whereAllNull = true)
```

<inline-frame src="./resources/dropNullsWhereAllNull.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNullsSelector-->

```kotlin
// remove rows with null value in 'name' column
df.dropNulls { name }
```

<inline-frame src="./resources/dropNullsSelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNullsSelectorSeveralCols-->

```kotlin
// remove rows with null value in 'name' OR 'weight' columns
df.dropNulls { name and weight }
```

<inline-frame src="./resources/dropNullsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNullsWhereAllNullSelector-->

```kotlin
// remove rows with nulls in both 'name' and 'weight' columns
df.dropNulls(whereAllNull = true) { name and weight }
```

<inline-frame src="./resources/dropNullsWhereAllNullSelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

If called on a [`DataColumn`](DataColumn.md), removes `null` values from this [`DataColumn`](DataColumn.md), 
adjusting the type accordingly.

<!---FUN dropNullsDataColumn-->

```kotlin
df.weight.dropNulls()
```

<inline-frame src="./resources/dropNullsDataColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

## dropNaNs

Removes rows with [`NaN` values](nanAndNa.md#nan) (`Double.NaN` or `Float.NaN`).

See also [fillNaNs](fill.md#fillnans), which replaces [`NaN` values](nanAndNa.md#nan) instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNaNs-->

```kotlin
// remove rows containing NaN in any column
df.dropNaNs()
```

<inline-frame src="./resources/dropNaNs.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNaNsWhereAllNaN-->

```kotlin
// remove rows with NaN in all columns of type `Double?`
df.dropNaNs(whereAllNaN = true) { colsOf<Double?>() }
```

<inline-frame src="./resources/dropNaNsWhereAllNaN.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNaNsSelector-->

```kotlin
// remove rows where 'weight' is NaN
df.dropNaNs { weight }
```

<inline-frame src="./resources/dropNaNsSelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNaNsSelectorSeveralCols-->

```kotlin
// remove rows where either 'age' or 'weight' is NaN
df.dropNaNs { age and weight }
```

<inline-frame src="./resources/dropNaNsSelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNaNsWhereAllNaNSelector-->

```kotlin
// remove rows where both 'age' and 'weight' are NaN
df.dropNaNs(whereAllNaN = true) { age and weight }
```

<inline-frame src="./resources/dropNaNsWhereAllNaNSelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

If called on a [`DataColumn`](DataColumn.md), removes [`NaN` values](nanAndNa.md#nan) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.

<!---FUN dropNaNsDataColumn-->

```kotlin
df.weight.dropNaNs()
```

<inline-frame src="./resources/dropNaNsDataColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->

## dropNA

Removes rows with [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, or `Float.NaN`).

See also [fillNA](fill.md#fillna), which replaces [`NA` values](nanAndNa.md#na) instead of removing rows.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN dropNA-->

```kotlin
// remove rows containing null or NaN in any column
df.dropNA()
```

<inline-frame src="./resources/dropNA.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNAWhereAllNA-->

```kotlin
// remove rows with null or NaN in all columns
df.dropNA(whereAllNA = true)
```

<inline-frame src="./resources/dropNAWhereAllNA.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNASelector-->

```kotlin
// remove rows where 'weight' is null or NaN
df.dropNA { weight }
```

<inline-frame src="./resources/dropNASelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNASelectorSeveralCols-->

```kotlin
// remove rows where either 'age' or 'weight' is null or NaN
df.dropNA { age and weight }
```

<inline-frame src="./resources/dropNASelectorSeveralCols.html" width="100%" height="500px"></inline-frame>

<!---END-->

<!---FUN dropNAWhereAllNASelector-->

```kotlin
// remove rows where both 'age' and 'weight' are null or NaN
df.dropNA(whereAllNA = true) { age and weight }
```

<inline-frame src="./resources/dropNAWhereAllNASelector.html" width="100%" height="500px"></inline-frame>

<!---END-->

If called on a [`DataColumn`](DataColumn.md), removes [`NA` values](nanAndNa.md#na) from this [`DataColumn`](DataColumn.md),
adjusting the type accordingly.

<!---FUN dropNADataColumn-->

```kotlin
df.weight.dropNA()
```

<inline-frame src="./resources/dropNADataColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->
