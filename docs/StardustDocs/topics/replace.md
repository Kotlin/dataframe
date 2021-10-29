[//]: # (title: replace)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces one or several columns with new columns

```kotlin
df.replace { columnsSelector }.with(newColumns)
df.replace { columnsSelector }.with { columnExpression }
```

```
columnExpression = DataFrame.(DataColumn) -> DataColumn
```

See [column selectors](ColumnSelectors.md)

<!---FUN replace-->

```kotlin
df.replace { name }.with { name.firstName }
df.replace { stringCols() }.with { it.lowercase() }
df.replace { age }.with { 2021 - age named "year" }
```

<!---END-->

`replace { cols }.with { expr } ` is equivalent to [`convert { cols }.to { expr }`](convert.md)
