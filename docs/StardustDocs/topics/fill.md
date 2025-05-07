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

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.fillNulls.html" width="100%"/>
<!---END-->

## fillNaNs

Replaces [`NaN` values](nanAndNa.md#nan) (`Double.NaN` and `Float.NaN`) with given value or expression.

<!---FUN fillNaNs-->

```kotlin
df.fillNaNs { colsOf<Double>() }.withZero()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.fillNaNs.html" width="100%"/>
<!---END-->

## fillNA

Replaces [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, and `Float.NaN`) with given value or expression.

<!---FUN fillNA-->

```kotlin
df.fillNA { weight }.with { -1 }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.fillNA.html" width="100%"/>
<!---END-->
