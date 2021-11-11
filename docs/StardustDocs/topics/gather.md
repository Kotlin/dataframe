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

See [column selectors](ColumnSelectors.md)

<!---FUN gather-->

```kotlin
pivoted.gather { "London".."Tokyo" }.into("city", "population")
```

<!---END-->

To convert gathered keys or values use `mapKeys` and `mapValues`. If columns were selected in untyped way, use `cast` to get typed input in `mapValues` lambda

<!---FUN gatherWithMapping-->

```kotlin
pivoted.gather { "London".."Tokyo" }.cast<Int>().mapKeys { it.lowercase() }.mapValues { 1.0 / it }.into("city", "density")
```

<!---END-->

When `valueColumnName` is not defined, only `key` column is created. This can be used to collect column names based on value filter in `where`. If filter is also not defined, it will default to `{ it }` for `Boolean` columns and `{ it != null }` for other columns.

<!---FUN gatherNames-->

```kotlin
pivoted.gather { "London".."Tokyo" }.cast<Int>().where { it > 0 }.into("city")
```

<!---END-->
