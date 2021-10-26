[//]: # (title: select)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns `DataFrame` with subset of columns

<!---FUN getColumnsByName-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age and weight }
df[df.age, df.weight]
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df.select { age and weight }
df[age, weight]
```

</tab>
<tab title="Strings">

```kotlin
df.select { "age"() and "weight"() }
df["age", "weight"]
```

</tab></tabs>
<!---END-->
