[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns [`DataFrame`](DataFrame.md) with rows that satisfy [row condition](DataRow.md#row-conditions)

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
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }
```

</tab></tabs>
<!---END-->

## filterBy

Returns [`DataFrame`](DataFrame.md) with rows that have value `true` in given column of type `Boolean`.

<!---FUN filterBy-->
<tabs>
<tab title="Properties">

```kotlin
df.filterBy { isHappy }
```

</tab>
<tab title="Accessors">

```kotlin
val isHappy by column<Boolean>()
df.filterBy { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.filterBy("isHappy")
```

</tab></tabs>
<!---END-->
