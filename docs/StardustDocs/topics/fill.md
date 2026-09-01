[//]: # (title: fill)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.FillSamples-->

Replace missing values.

**Related operations**: [](updateConvert.md)

The examples on this page use the following dataframe:

<!---FUN fillDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/fillDf.html" width="100%" height="500px"></inline-frame>

## fillNulls

Replaces `null` values with given value or expression. 

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN fillNulls-->

```kotlin
df.fillNulls { weight }.with { -1.0 }
```

<!---END-->

same as

<!---FUN fillNullsAsUpdate-->

```kotlin
df.update { weight }.where { it == null }.with { -1.0 }
```

<!---END-->
<inline-frame src="./resources/fillNulls.html" width="100%" height="500px"></inline-frame>

## fillNaNs

Replaces [`NaN` values](nanAndNa.md#nan) (`Double.NaN` and `Float.NaN`) with given value or expression.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN fillNaNs-->

```kotlin
df.fillNaNs { weight }.withZero()
```

<!---END-->
<inline-frame src="./resources/fillNaNs.html" width="100%" height="500px"></inline-frame>

## fillNA

Replaces [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, and `Float.NaN`) with given value or expression.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN fillNA-->

```kotlin
df.fillNA { weight }.with { -1.0 }
```

<!---END-->
<inline-frame src="./resources/fillNA.html" width="100%" height="500px"></inline-frame>
