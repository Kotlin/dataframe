[//]: # (title: replace)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ReplaceSamples-->

Replaces one or several columns with new columns and returns a new [`DataFrame`](DataFrame.md).
To replace values inside the columns instead of whole columns, use [`replaceAll`](#replaceall).

```text
replace { columns }
    .with(newColumns) | .with { transform }

transform: ColumnsContainer.(DataColumn) -> DataColumn
```

**Related operations**: [](insertReplace.md)

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

Every new column takes the place of the column it replaces, in the same column group,
but keeps its own name, so the name of the column can change.

The examples on this page use the following dataframe:

<!---FUN replaceDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/replaceDf.html" width="100%" height="500px"></inline-frame>

## Replace with an expression

`transform` is called once for every selected column, in the order of the selection.
Its receiver gives access to all the columns of the original dataframe, including the ones being replaced,
and its argument is the selected column. The column it returns replaces the selected one.

Here the column group `name` is replaced with its column `firstName`:

<!---FUN replaceGroupWithColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.replace { name }.with { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.replace("name").with { this[pathOf("name", "firstName")] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceGroupWithColumn_properties.html" width="100%" height="500px"></inline-frame>

To keep the name, return a column with the same name. Here the `String` columns at the top level
(only `city` in this dataframe) get lowercase values, and the names stay the same:

<!---FUN replaceKeepName-->

```kotlin
df.replace { colsOf<String?>() }.with { col -> col.map { it?.lowercase() } }
```

<!---END-->
<inline-frame src="./resources/replaceKeepName.html" width="100%" height="500px"></inline-frame>

To change the name, use `named` or `rename` on the new column:

<!---FUN replaceRename-->
<tabs>
<tab title="Properties">

```kotlin
df.replace { age }.with { 2021 - age named "year" }
```

</tab>
<tab title="Strings">

```kotlin
df.replace("age").with { 2021 - it.cast<Int>() named "year" }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceRename_properties.html" width="100%" height="500px"></inline-frame>

The new column must have as many rows as the dataframe, and its name must not be used by another column
in the same column group that is not replaced. Otherwise, `with` throws an exception.

## Replace with new columns

`with(newColumns)` takes the new columns as arguments or as a `List`.
The examples use the dataframe from the top of this page.
The first selected column is replaced with the first new column, the second one with the second, and so on,
in the order of the selection, not in the order of the columns in the dataframe:

<!---FUN replaceWithColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.replace { weight and age }.with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
```

</tab>
<tab title="Strings">

```kotlin
df.replace("weight", "age").with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceWithColumns_properties.html" width="100%" height="500px"></inline-frame>

`weight` is selected first, so it is replaced with `weightInGrams`,
although `age` comes first in the dataframe.

If there are fewer new columns than selected columns, `with` throws an exception.
Extra new columns are ignored.
The same rules about the number of rows and the names hold as for `with { transform }`.

## Replace a column group and a column inside it

If you select a column group together with a column inside it, only the group is replaced:
the inner column is skipped. It is not passed to `transform`, no new column is used for it,
and no error is reported. Select either the group or the columns inside it.

## replace and convert

The examples use the dataframe from the top of this page.

To keep the names of the columns and change only their values and types,
use [`convert { columns }.asColumn { transform }`](convert.md) instead.
With the same expression, `asColumn` keeps the name `age`, while `replace` would name the column `ageText`:

<!---FUN replaceVsConvertAsColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.convert { age }.asColumn { it.convertToString().rename("ageText") }
```

</tab>
<tab title="Strings">

```kotlin
df.convert("age").asColumn { it.convertToString().rename("ageText") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceVsConvertAsColumn_properties.html" width="100%" height="500px"></inline-frame>

The [compiler plugin](Compiler-Plugin.md) does not track `replace`: after it, the extension properties keep
the old column names and types. In a project with the plugin, use `convert { columns }.asColumn { transform }`.

## Replace a column with a column of another kind

A new column does not have to be of the same kind as the old one.
Here every value of `contributors` is a JSON string. `transform` reads a [`DataFrame`](DataFrame.md) from each of them,
so `contributors` becomes a [`FrameColumn`](DataColumn.md#framecolumn).
It can then be used as a [`GroupBy`](groupBy.md#transformation) to compute [summary statistics](summaryStatistics.md)
or to perform an [aggregation](groupBy.md#aggregation):

<!---FUN replaceWithFrameColumn-->
<tabs>
<tab title="Properties">

```kotlin
val repos = dataFrameOf("name", "contributors")(
    "dataframe", """[{"login": "abc", "contributions": 111}, {"login": "dfg", "contributions": 100}]""",
    "kotlin", """[{"login": "abc", "contributions": 180}, {"login": "dfb", "contributions": 100}]""",
)

val reposWithFrames = repos.replace { contributors }.with { it.map { json -> DataFrame.readJsonStr(json) } }

reposWithFrames.asGroupBy("contributors").max("contributions")
```

</tab>
<tab title="Strings">

```kotlin
val repos = dataFrameOf("name", "contributors")(
    "dataframe", """[{"login": "abc", "contributions": 111}, {"login": "dfg", "contributions": 100}]""",
    "kotlin", """[{"login": "abc", "contributions": 180}, {"login": "dfb", "contributions": 100}]""",
)

val reposWithFrames = repos.replace("contributors").with {
    it.map { json -> DataFrame.readJsonStr(json as String) }
}

reposWithFrames.asGroupBy("contributors").max("contributions")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceWithFrameColumn_properties.html" width="100%" height="500px"></inline-frame>

## replaceAll

Replaces values, not columns: every cell equal to the first value of a pair gets the second value of that pair.
The names and the positions of the columns stay the same, and so do the values without a match.

```text
replaceAll(oldValue to newValue, ..)
replaceAll(oldValue to newValue, .., columns = { columns })
```

By default, values are replaced in all columns at any depth, so the columns inside column groups are included.
The dataframes inside frame columns are not changed.
Here `Alice` is replaced in `name.firstName`, inside the column group `name`,
and `Moscow` is replaced with `null` in `city`.
The examples use the dataframe from the top of this page:

<!---FUN replaceAllValues-->

```kotlin
df.replaceAll("Alice" to "Alicia", "Moscow" to null)
```

<!---END-->
<inline-frame src="./resources/replaceAllValues.html" width="100%" height="500px"></inline-frame>

If the same value is given in several pairs, the last pair wins.
A value can be replaced with `null`, and `null` can be replaced with a value.

Use `columns` to replace values only in some of the columns:

<!---FUN replaceAllInColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.replaceAll(null to "Unknown", columns = { city })
```

</tab>
<tab title="Strings">

```kotlin
df.replaceAll(null to "Unknown", columns = { "city"<String?>() })
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/replaceAllInColumns_properties.html" width="100%" height="500px"></inline-frame>

A new value has to fit the type of the column where it is put, otherwise `replaceAll` throws an exception.
`null` fits any column: the column becomes nullable.
For example, `replaceAll(null to "Unknown")` without `columns` fails on this dataframe,
because `weight` is a column of `Int` values that contains `null`.

See also [`update`](update.md), which computes new values with an expression,
and [`fillNulls`](fill.md#fillnulls), which replaces `null` values.
