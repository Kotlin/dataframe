[//]: # (title: mergeRows)

Merges values in selected columns into lists grouped by other columns

Input:

|name   | city    | age |
|-------|---------|-----|
| Alice | London  | 15  |
| Bob   | Milan   | 20  |
| Alice | Moscow  | 23  |
| Alice | London  | 30  |
| Bob   | Milan   | 11  |

```kotlin
df.mergeRows { age }
```
Output:

|name   | city   | age
|-------|--------|-----
| Alice | London | [15, 30]
| Bob   | Milan  | [20, 11]
| Alice | Moscow | [23]
