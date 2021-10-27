[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Keeps only rows that satisfy [row condition](DataRow.md#row-conditions)

<!---FUN filter-->
<tabs>
<tab title="Properties">

```kotlin
df.filter { age > 18 && name.firstName.startsWith("A") }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val firstName by name.column<String>()

df.filter { age() > 18 && firstName().startsWith("A") }
// or
df.filter { it[age] > 18 && it[firstName].startsWith("A") }
```

</tab>
<tab title="Strings">

```kotlin
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }.nrow shouldBe 1
```

</tab></tabs>
<!---END-->
