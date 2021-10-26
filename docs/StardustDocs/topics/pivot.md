[//]: # (title: pivot)

Converts two key-value columns into several columns using values in `key` column as new column names and values in `value` column as new column values.
This is reverse to [gather](#gather)
```
df.pivot { keyColumns }.withIndex { indexColumns }.into { rowExpression }
```
**Input**

| city | day | temperature
|--------|---------|---------
| London | 18 | 3
| London | 19 | 5
| London | 20 | 4
| Milan | 18 | 7
| Milan | 20 | 3
| Milan | 21 | 5

```kotlin
df.pivot { day.map { "Feb, $it" } }.withIndex { city }.into { temperature }
```
**Output**

| city | Feb, 18 | Feb, 19 | Feb, 20 | Feb, 21
|--------|---------|---------|--------|---
| London | 3 | 5 | 4 | null
| Milan  | 7 | null | 3 | 5
