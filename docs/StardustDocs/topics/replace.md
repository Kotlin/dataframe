[//]: # (title: replace)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces one or several columns with new columns.

```kotlin
replace { columns }
    .with(newColumns) | .with { columnExpression }

columnExpression: DataFrame.(DataColumn) -> DataColumn
```

See [column selectors](ColumnSelectors.md)

<!---FUN replace-->

```kotlin
df.replace { name }.with { name.firstName }
df.replace { colsOf<String?>() }.with { it.lowercase() }
df.replace { age }.with { 2021 - age named "year" }
```

<!---END-->

<tip>

`replace { columns }.with { columnExpression } ` is equivalent to `convert { columns }.to { columnExpression }`. See [`convert`](convert.md) for details.

</tip>
