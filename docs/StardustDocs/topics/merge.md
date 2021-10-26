[//]: # (title: merge)

Merges several columns into a single column. Reverse operation to [split](#split)
```
df.merge { columns }.into(columnPath)
df.merge { columns }.by(delimeters, options).into(columnPath)
df.merge { columns }.by { merger }.into(columnPath)

merger = List<T> -> Any
```
When no `delimeter` or `merger` are defined, values will be merged into the `List`
```kotlin
df.merge { firstName and lastName }.by(" ").into("fullName")

df.merge { cols { it.name.startsWith("value") } }.into("values")

df.merge { protocol and host and port and path }.by { it[0] + "://" + it[1] + ":" + it[2] + "/" + it[3] }.into("address")
```
