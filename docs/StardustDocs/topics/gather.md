[//]: # (title: gather)

Converts several columns into two `key-value` columns, where `key` is a name of original column and `value` is column data
This is reverse to [pivot](#pivot)
```
df.gather { columns }.into(keyColumnName)

df.gather { columns }.where { valueFilter }.map { valueTransform }.mapNames { keyTransform }.into(keyColumnName, valueColumnName)
```
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

When `valueColumnName` is not defined, only 'key' column is added. In this case `valueFilter` will default to `{ it }` for `Boolean` columns and `{ it != null }` for other columns

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
