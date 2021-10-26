[//]: # (title: explode)

Splits list-like values and spreads them vertically. Reverse to [mergeRows](#mergerows)
The following types of values will be splitted:
* List
* DataFrame
* String (splits by ',')
  Scalar values will not be transformed. Empty lists will result in `null`
  Row values in other columns will be duplicated

Input:

| A | B
|---|---
| 1 | [1, 2, 3]
| 2 | null
| 3 | [4, 5]
| 4 | []
```kotlin
df.explode { B }
```
Output:

| A | B
|---|---
| 1 | 1
| 1 | 2
| 1 | 3
| 2 | null
| 3 | 4
| 3 | 5
| 4 | null

Note: exploded `FrameColumn` turns into `ColumnGroup`

When several columns are exploded, lists in different columns are aligned:

Input:

| A | B | C
|---|---|---
| 1 | [1, 2] | [1, 2]
| 2 | [] | [3, 4]
| 3 | [3, 4, 5] | [5, 6]
```kotlin
df.explode { B and C }
```
Output:

| A | B | C
|---|---|---
| 1 | 1 | 1
| 1 | 2 | 2
| 2 | null | 3
| 2 | null | 4
| 3 | 3 | 5
| 3 | 4 | 6
| 3 | 5 | null
