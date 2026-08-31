[//]: # (title: fill)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.FillSamples-->

Replace missing values.

**Related operations**: [](updateConvert.md)

Unless stated otherwise, the examples on this page use the following dataframe:

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
df.fillNulls { colsOf<Int?>() }.with { -1 }
```

<!---END-->

same as

<!---FUN fillNullsAsUpdate-->

```kotlin
df.update { colsOf<Int?>() }.where { it == null }.with { -1 }
```

<!---END-->
<inline-frame src="./resources/fillNulls.html" width="100%" height="500px"></inline-frame>

## fillNaNs

Replaces [`NaN` values](nanAndNa.md#nan) (`Double.NaN` and `Float.NaN`) with given value or expression.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

For the `fillNaNs` example, the following dataframe is used:

<!---FUN fillNaNsDf-->

```kotlin
dfWithNaNs
```

<!---END-->
<inline-frame src="./resources/fillNaNsDf.html" width="100%" height="500px"></inline-frame>

<!---FUN fillNaNs-->

```kotlin
dfWithNaNs.fillNaNs { colsOf<Double>() }.withZero()
```

<!---END-->
<inline-frame src="./resources/fillNaNs.html" width="100%" height="500px"></inline-frame>

## fillNA

Replaces [`NA` values](nanAndNa.md#na) (`null`, `Double.NaN`, and `Float.NaN`) with given value or expression.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN fillNA-->

```kotlin
df.fillNA { weight }.with { -1 }
```

<!---END-->
<inline-frame src="./resources/fillNA.html" width="100%" height="500px"></inline-frame>
