[//]: # (title: drop)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Removes all rows that satisfy [row condition](DataRow.md#row-conditions)

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
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropWhere.html"/>
<!---END-->

## dropNulls

Remove rows with `null` values

<!---FUN dropNulls-->

```kotlin
df.dropNulls() // remove rows with null value in any column
df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
df.dropNulls { city } // remove rows with null value in 'city' column
df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNulls.html"/>
<!---END-->

## dropNaNs

Remove rows with [`NaN` values](nanAndNa.md#nan) (`Double.NaN` or `Float.NaN`).

<!---FUN dropNaNs-->

```kotlin
df.dropNaNs() // remove rows containing NaN in any column
df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
df.dropNaNs { weight } // remove rows where 'weight' is NaN
df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNaNs.html"/>
<!---END-->

## dropNA

Remove rows with [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, or `Float.NaN`).

<!---FUN dropNA-->

```kotlin
df.dropNA() // remove rows containing null or NaN in any column
df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
df.dropNA { weight } // remove rows where 'weight' is null or NaN
df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.dropNA.html"/>
<!---END-->
