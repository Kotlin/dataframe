[//]: # (title: distinctBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Keep only the first row for every group of rows grouped by some condition.

<!---FUN distinctBy-->
<tabs>
<tab title="Properties">

```kotlin
df.distinctBy { age and name } shouldBe df.groupBy { age and name }.mapToRows { group.first() }

df.distinctBy { expr { name.firstName.take(3).lowercase() } }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val firstName by name.column<String>()

df.distinctBy { age and name } shouldBe df.groupBy { age and name }.mapToRows { group.first() }

df.distinctBy { expr { firstName().take(3).lowercase() } }
```

</tab>
<tab title="Strings">

```kotlin
df.distinctBy("age", "name") shouldBe df.groupBy("age", "name").mapToRows { group.first() }

df.distinctBy { expr { "name"["firstName"]<String>().take(3).lowercase() } }
```

</tab></tabs>
<!---END-->
