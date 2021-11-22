[//]: # (title: Get rows)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Get single [`DataRow`](DataRow.md) by [index](indexing.md):

<!---FUN getRowByIndex-->

```kotlin
df[2]
```

<!---END-->

Get single [`DataRow`](DataRow.md) by [row condition](DataRow.md#row-conditions):

<!---FUN getRowByCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.single { age == 45 }
df.first { weight != null }
df.minBy { age }
df.maxBy { name.firstName.length }
df.maxByOrNull { weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()

df.single { age() == 45 }
df.first { weight() != null }
df.minBy(age)
df.maxBy { firstName().length }
df.maxByOrNull { weight() }
```

</tab>
<tab title="Strings">

```kotlin
df.single { "age"<Int>() == 45 }
df.first { it["weight"] != null }
df.minBy("weight")
df.maxBy { "name"["firstName"]<String>().length }
df.maxByOrNull("weight")
```

</tab></tabs>
<!---END-->
