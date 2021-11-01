[//]: # (title: nullToZero)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replace `null` values with `0`. 

Works for `Int`, `Double`, `Long` and `BigDecimal` columns.

<!---FUN nullToZero-->

```kotlin
df.nullToZero { weight }
```

<!---END-->
