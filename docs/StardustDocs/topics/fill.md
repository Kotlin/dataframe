[//]: # (title: fill)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replace missing values.

## fillNulls

Replaces `null` values with given value or expression. 

<!---FUN fillNulls-->

```kotlin
df.fillNulls { intCols() }.with { -1 }
// same as
df.update { intCols() }.where { it == null }.with { -1 }
```

<!---END-->

## fillNaNs

Replaces `Double.NaN` and `Float.NaN` values with given value or expression.

<!---FUN fillNaNs-->

```kotlin
df.fillNaNs { doubleCols() }.withZero()
```

<!---END-->

## fillNA

Replaces `null`, `Double.NaN` and `Float.NaN` values with given value or expression.

<!---FUN fillNA-->

```kotlin
df.fillNA { weight }.withValue(-1)
```

<!---END-->
