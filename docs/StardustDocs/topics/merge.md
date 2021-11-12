[//]: # (title: merge)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Merges several columns into a single column. 

Reverse operation to [`split`](split.md)

```kotlin
merge { columns }
    .by(delimeter) | with { merger } 
    .into(column)

merger: (DataRow).List<T> -> Any
```

<!---FUN merge-->

```kotlin
df.merge { name.firstName and name.lastName }.by(" ").into("name")
```

<!---END-->

`merger` accepts a `List` of collected values for every row typed by their common type:

<!---FUN mergeSameWith-->

```kotlin
df.merge { name.firstName and name.lastName }.with { it[0].first() + "." + it[1].first() + "." }.into("initials")
```

<!---END-->

When heterogeneous columns are merged, they may need to be cast to valid types in `merger`:

<!---FUN mergeDifferentWith-->

```kotlin
df.merge { name.firstName and age and isHappy }
    .with { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
    .into("status")
```

<!---END-->

By default, when no `delimeter` or `merger` is specified, values will be merged into the `List`:

<!---FUN mergeDefault-->

```kotlin
df.merge { numberCols() }.into("data")
```

<!---END-->
