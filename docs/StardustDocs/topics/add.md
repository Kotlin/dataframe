[//]: # (title: add)

Adds new column to `DataFrame`
```kotlin
add(columnName) { rowExpression }

rowExpression: DataRow.(DataRow) -> Value
```
See [row expressions](DataRow.md#row-expressions)
```kotlin
df.add("year of birth") { 2021 - age }
df.add("diff") { temperature - (prev?.temperature ?: 0) }
```
Add several columns:
```kotlin
df.add {
   "is adult" { age > 18 }
   "name length" { name.length } 
}
```
or with `+` operator
```kotlin
df + {
   "is adult" { age > 18 }
   "name length" { name.length } 
}
```
