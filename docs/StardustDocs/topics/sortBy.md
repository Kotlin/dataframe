[//]: # (title: sortBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` with rows sorted by one or several columns.

By default, columns are sorted in ascending order with `null` values going first. Available modifiers:
* `.desc` — changes column sort order from ascending to descending
* `.nullsLast` — forces `null` values to be placed at the end of the order

<!---FUN sortBy-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { age }
df.sortBy { age and name.firstName.desc() }
df.sortBy { weight.nullsLast() }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()

df.sortBy { age }
df.sortBy { age and firstName }
df.sortBy { weight.nullsLast() }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy("age")
df.sortBy { "age" and "name"["firstName"].desc() }
df.sortBy { "weight".nullsLast() }
```

</tab></tabs>
<!---END-->

## sortByDesc

Returns `DataFrame` sorted by one or several columns in descending order.

<!---FUN sortByDesc-->
<tabs>
<tab title="Properties">

```kotlin
df.sortByDesc { age and weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df.sortByDesc { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.sortByDesc("age", "weight")
```

</tab></tabs>
<!---END-->

## sortWith

Returns `DataFrame` sorted with comparator.

<!---FUN sortWith-->

```kotlin
df.sortWith { row1, row2 ->
    when {
        row1.age < row2.age -> -1
        row1.age > row2.age -> 1
        else -> row1.name.firstName.compareTo(row2.name.firstName)
    }
}
```

<!---END-->
