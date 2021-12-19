[//]: # (title: Column accessors API)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ApiLevels-->

For frequently accessed columns type casting can be reduced by [Column Accessors](DataColumn.md#column-accessors):

<!---FUN accessors1-->

```kotlin
val survived by column<Boolean>() // accessor for Boolean column with name 'survived'
val home by column<String>()
val age by column<Int?>()
val name by column<String>()
val lastName by column<String>()
```

<!---END-->

Now columns can be accessed in a type-safe way using `invoke` operator:

<!---FUN accessors2-->

```kotlin
DataFrame.read("titanic.csv")
    .add(lastName) { name().split(",").last() }
    .dropNulls { age }
    .filter { survived() && home().endsWith("NY") && age()!! in 10..20 }
```

<!---END-->

<warning>
Note that it still doesn’t solve the problem of whether the column actually exists in a data frame, but reduces type casting.
</warning>
