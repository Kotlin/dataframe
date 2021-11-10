[//]: # (title: gather)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Converts several columns into two columns `key` and `value`, where `key` column contains names of original columns and `value` column contains data from original columns

This operation is reverse to [pivot](pivot.md)

```kotlin
gather { columns }
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

**Input**

| city | Feb, 18 | Feb, 19 | Feb, 20 | Feb, 21
|--------|---------|---------|--------|---
| London | 3 | 5 | 4 | null
| Milan  | 7 | null | 3 | 5

```kotlin
df.gather { cols(1..4) }.where { it != null}.mapNames { it.substring(5) }.into("day", "temperature")
```

**Output**

| city | day | temperature
|--------|---------|---------
| London | 18 | 3
| London | 19 | 5
| London | 20 | 4
| Milan | 18 | 7
| Milan | 20 | 3
| Milan | 21 | 5


**Input**

name | London | Paris | Milan
-----|--------|-------|-------
Alice| true | false | true
Bob | false | true | true

```kotlin
df.gather { cols(1..4) }.into("visited")
```

**Output**

name | visited
-----|--------
Alice | London
Alice | Milan
Bob | Paris
Bob | Milan
