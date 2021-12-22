[//]: # (title: merge)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Merges several columns into a single column. 

Reverse operation to [`split`](split.md)

```kotlin
merge { columns }
    [.notNull()]
    .by(delimeter) | .by { merger } 
    [.into(column) | .intoList() ]

merger: (DataRow).List<T> -> Any
```

<!---FUN merge-->

```kotlin
// Merge two columns into one column "fullName"
df.merge { name.firstName and name.lastName }.by(" ").into("fullName")
```

<!---END-->

`merger` accepts a `List` of collected values for every row typed by their common type:

<!---FUN mergeSameWith-->

```kotlin
df.merge { name.firstName and name.lastName }
    .by { it[0] + " (" + it[1].uppercase() + ")" }
    .into("fullName")
```

<!---END-->

When heterogeneous columns are merged, they may need to be cast to valid types in `merger`:

<!---FUN mergeDifferentWith-->

```kotlin
df.merge { name.firstName and age and isHappy }
    .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
    .into("status")
```

<!---END-->

By default, when no `delimeter` or `merger` is specified, values will be merged into the `List`:

<!---FUN mergeDefault-->

```kotlin
df.merge { colsOf<Number>() }.into("data")
```

<!---END-->

Merged column values can also be exported to `List`:

<!---FUN mergeIntoList-->

```kotlin
// Merge data from two columns into List<String>
df.merge { name.firstName and name.lastName }.by(",").intoList()
```

<!---END-->
