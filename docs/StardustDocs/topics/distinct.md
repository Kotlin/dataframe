[//]: # (title: distinct)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Removes duplicate rows.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.

<!---FUN distinct-->

```kotlin
df.distinct()
```

<!---END-->

If columns are specified, resulting `DataFrame` will have only given columns with distinct values.

<!---FUN distinctColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.distinct { age and name } shouldBe df.select { age and name }.distinct()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
df.distinct { age and name } shouldBe df.select { age and name }.distinct()
```

</tab>
<tab title="Strings">

```kotlin
df.distinct("age", "name") shouldBe df.select("age", "name").distinct()
```

</tab></tabs>
<!---END-->

## distinctBy

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
