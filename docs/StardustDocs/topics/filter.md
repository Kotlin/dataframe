[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Keeps only rows that satisfy [row condition](DataRow.md#row-conditions)

<!---FUN filter-->
<tabs>
<tab title="Properties">

```kotlin
df.filter { age > 18 && name.firstName.startsWith("A") }
df.drop { weight == null || city == null }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val weight by column<Int?>()
val city by column<String?>()
val firstName by name.column<String>()

df.filter { age() > 18 && firstName().startsWith("A") }
df.drop { weight() == null || city() == null }
// or
df.filter { it[age] > 18 && it[firstName].startsWith("A") }
df.drop { it[weight] == null || it[city] == null }
```

</tab>
<tab title="Strings">

```kotlin
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }.nrow shouldBe 1
df.drop { it["weight"] == null || it["city"] == null }
```

</tab></tabs>
<!---END-->
