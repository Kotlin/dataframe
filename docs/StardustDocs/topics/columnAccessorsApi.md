[//]: # (title: Column accessors API)

For frequently accessed columns type casting can be reduced by Column Accessors:

```kotlin
val survived by column<Boolean>() // accessor for Boolean column with name 'survived'
val home by column<String>()
val age by column<Int?>()
```
Now columns can be accessed in a type-safe way:
```kotlin
df.filter { it[survived] && it[home].endsWith("NY") && it[age] in 10..20 }
```
or just using invoke operator at column accessors:
```kotlin
df.filter { survived() && home().endsWith("NY") && age() in 10..20 }
```

<warning>
Note that it still doesn’t solve the problem of whether the column actually exists in a data frame, but type-safety is now preserved.
</warning>
