[//]: # (title: gather)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Converts several columns into two columns `key` and `value`. `key` column will contain names of original columns, `value` column will contain values from original columns.

This operation is reverse to [pivot](pivot.md)

```kotlin
gather { columns }
    [.explodeLists()]
    [.cast<Type>()]
    [.notNull()]
    [.where { valueFilter }]
    [.mapKeys { keyTransform }]
    [.mapValues { valueTransform }]
    .into(keyColumn, valueColumn) | .keysInto(keyColumn) | .valuesInto(valueColumn)

valueFilter: (value) -> Boolean
keyTransform: (columnName: String) -> K
valueTransform: (value) -> R 
```

See [column selectors](ColumnSelectors.md)

Configuration options:
* `explodeLists` — gathered values of type `List` will be exploded into their elements, so `where`, `cast`, `notNull` and `mapValues` will be applied to list elements instead of lists themselves
* `cast` — inform compiler about the expected type of gathered elements. This type will be passed to `where` and `mapKeys` lambdas
* `notNull` — skip gathered `null` values
* `where` — filter gathered values
* `mapKeys` — transform gathered column names (keys)
* `mapValues` — transform gathered column values 

Storage options:
* `into(keyColumn, valueColumn)` — store gathered key-value pairs in two new columns with names `keyColumn` and `valueColumn`
* `keysInto(keyColumn)` — store only gathered keys (column names) in a new column `keyColumn`
* `valuesInto(valueColumn)` — store only gathered values in a new column `valueColumn`

<!---FUN gather-->

```kotlin
pivoted.gather { "London".."Tokyo" }.into("city", "population")
```

<!---END-->

<!---FUN gatherWithMapping-->

```kotlin
pivoted.gather { "London".."Tokyo" }
    .cast<Int>()
    .where { it > 10 }
    .mapKeys { it.lowercase() }
    .mapValues { 1.0 / it }
    .into("city", "density")
```

<!---END-->
