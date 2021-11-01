[//]: # (title: fillNulls)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces `null` values with expression. 

<!---FUN fillNulls-->

```kotlin
df.fillNulls { intCols() }.with { -1 }
// same as
df.update { intCols() }.where { it == null }.with { -1 }
```

<!---END-->
