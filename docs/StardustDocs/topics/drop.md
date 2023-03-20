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
<tab title="Accessors">

```kotlin
val name by columnGroup()
val weight by column<Int?>()
val city by column<String?>()

df.drop { weight() == null || city() == null }
// or
df.drop { it[weight] == null || it[city] == null }
```

</tab>
<tab title="Strings">

```kotlin
df.drop { it["weight"] == null || it["city"] == null }
```

</tab></tabs>
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

<!---END-->

## dropNaNs

Remove rows with `Double.NaN` or `Float.NaN` values

<!---FUN dropNaNs-->

```kotlin
df.dropNaNs() // remove rows containing NaN in any column
df.dropNaNs(whereAllNaN = true) // remove rows with NaN in all columns
df.dropNaNs { weight } // remove rows where 'weight' is NaN
df.dropNaNs { age and weight } // remove rows where either 'age' or 'weight' is NaN
df.dropNaNs(whereAllNaN = true) { age and weight } // remove rows where both 'age' and 'weight' are NaN
```

<!---END-->

## dropNA

Remove rows with `null`, `Double.NaN` or `Float.NaN` values

<!---FUN dropNA-->

```kotlin
df.dropNA() // remove rows containing null or NaN in any column
df.dropNA(whereAllNA = true) // remove rows with null or NaN in all columns
df.dropNA { weight } // remove rows where 'weight' is null or NaN
df.dropNA { age and weight } // remove rows where either 'age' or 'weight' is null or NaN
df.dropNA(whereAllNA = true) { age and weight } // remove rows where both 'age' and 'weight' are null or NaN
```

<!---END-->
