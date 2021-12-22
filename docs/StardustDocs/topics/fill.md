[//]: # (title: fill)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replace missing values.

## fillNulls

Replaces `null` values with given value or expression. 

<!---FUN fillNulls-->

```kotlin
df.fillNulls { colsOf<Int?>() }.with { -1 }
// same as
df.update { colsOf<Int?>() }.where { it == null }.with { -1 }
```

<!---END-->

## fillNaNs

Replaces `Double.NaN` and `Float.NaN` values with given value or expression.

<!---FUN fillNaNs-->

```kotlin
df.fillNaNs { colsOf<Double>() }.withZero()
```

<!---END-->

## fillNA

Replaces `null`, `Double.NaN` and `Float.NaN` values with given value or expression.

<!---FUN fillNA-->

```kotlin
df.fillNA { weight }.withValue(-1)
```

<!---END-->
