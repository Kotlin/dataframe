## Row expressions
Row expressions provide a value for every row of `DataFrame` and are used in [add](modify.md#add), [filter](access.md#filter--drop), forEach, [update](access.md#update) and other operations

Row expression syntax is ```DataRow.(DataRow) -> T``` so row values can be accessed with or without ```it``` keyword
```kotlin
df.filter { it.name.startsWith("A") }
df.filter { name.length == 5 }
```
Within row expression you can access [row-properties](rows.md#row-members)
```kotlin
df.add("diff") { value - prev?.value }
df.filter { index % 5 == 0 }
```
