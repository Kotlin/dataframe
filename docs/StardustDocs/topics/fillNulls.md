[//]: # (title: fillNulls)

Replaces `null` values with expression. Equivalent to
```kotlin
update { columns }.where { it == null }
```
Example
```kotlin
df.fillNulls { intCols() }.with { -1 } 
```
