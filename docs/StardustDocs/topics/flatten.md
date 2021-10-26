[//]: # (title: flatten)

Removes all column grouping under selected columns. Potential column name clashes are resolved by adding minimal required prefix from ancestor column names
```
df.flatten()
df.flatten { rootColumns }
```
Example
```kotlin
// a.b.c.d -> "d"
// a.f -> "f"
// a.c.d.e -> "d.e"
// a.b.e -> "b.e"
df.flatten { a }
```
