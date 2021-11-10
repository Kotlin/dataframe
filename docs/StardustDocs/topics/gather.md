[//]: # (title: gather)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Converts several columns into two columns `key` and `value`, where `key` column contains names of original columns and `value` column contains data from original columns

This operation is reverse to [pivot](pivot.md)

```kotlin
gather { columns }
    [.cast<Type>()]
    [.notNull()]
    [.where { valueFilter }]
    [.mapKeys { keyTransform }]
    [.mapValues { valueTransform }]
    .into(keyColumnName) | .into(keyColumnName, valueColumnName)

valueFilter: (Value) -> Boolean
keyTransform: (ColumnName) -> K
valueTransform: (Value) -> R 
```

When `valueColumnName` is not defined, only `key` column is created. This can be used to collect column names based on `valueFilter`. If `valueFilter` is also not defined, it will default to `{ it }` for `Boolean` columns and `{ it != null }` for other columns.

<!---FUN gather-->

```kotlin
pivoted.gather { "London".."Tokyo" }.cast<Int>().where { it > 0 }.into("city")
```

<!---END-->
