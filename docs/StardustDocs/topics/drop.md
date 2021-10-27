[//]: # (title: drop)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Removes all rows that satisfy [row condition](DataRow.md#row-conditions)

<!---FUN drop-->
<tabs>
<tab title="Properties">

```kotlin
df.drop { weight == null || city == null }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val weight by column<Int?>()
val city by column<String?>()

df.drop { weight() == null || city() == null }
// or
df.drop { it[weight] == null || it[city] == null }
```

</tab>
<tab title="Strings">

```kotlin
df.drop { it["weight"] == null || it["city"] == null }
```

</tab></tabs>
<!---END-->
