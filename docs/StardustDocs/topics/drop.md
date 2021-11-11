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

## dropNA

Remove rows with `null`, `Double.NaN` or `Float.NaN` values

<!---FUN dropNa-->

```kotlin
df.dropNa() // remove rows containing null or Double.NaN in any column
df.dropNa(whereAllNa = true) // remove rows with null or Double.NaN in all columns
df.dropNa { weight } // remove rows where 'weight' is null or Double.NaN
df.dropNa { age and weight } // remove rows where either 'age' or 'weight' is null or Double.NaN
df.dropNa(whereAllNa = true) { age and weight } // remove rows where both 'age' and 'weight' are null or Double.NaN
```

<!---END-->
