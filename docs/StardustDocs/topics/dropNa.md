[//]: # (title: dropNa)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Remove rows with `null` or `Double.NaN` values

<!---FUN dropNa-->

```kotlin
df.dropNa() // remove rows containing null or Double.NaN in any column
df.dropNa(whereAllNa = true) // remove rows with null or Double.NaN in all columns
df.dropNa { weight } // remove rows where 'weight' is null or Double.NaN
df.dropNa { age and weight } // remove rows where either 'age' or 'weight' is null or Double.NaN
df.dropNa(whereAllNa = true) { age and weight } // remove rows where both 'age' and 'weight' are null or Double.NaN
```

<!---END-->
