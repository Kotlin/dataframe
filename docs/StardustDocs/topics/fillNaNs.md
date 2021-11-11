[//]: # (title: fillNaNs)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces `Double.NaN` and `Float.NaN` values with given value or expression. 

<!---FUN fillNaNs-->

```kotlin
df.fillNulls { intCols() }.with { -1 }
// same as
df.update { intCols() }.where { it == null }.with { -1 }
```

<!---END-->
