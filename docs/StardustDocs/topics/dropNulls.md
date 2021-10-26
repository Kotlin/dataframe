[//]: # (title: dropNulls)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

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
