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

**Related operations**: [](splitMerge.md)

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN merge-->

```kotlin
// Merge two columns into one column "fullName"
df.merge { name.firstName and name.lastName }.by(" ").into("fullName")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.merge.html" width="100%"/>
<!---END-->

`merger` accepts a `List` of collected values for every row typed by their common type:

<!---FUN mergeSameWith-->

```kotlin
df.merge { name.firstName and name.lastName }
    .by { it[0] + " (" + it[1].uppercase() + ")" }
    .into("fullName")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mergeSameWith.html" width="100%"/>
<!---END-->

When heterogeneous columns are merged, they may need to be cast to valid types in `merger`:

<!---FUN mergeDifferentWith-->

```kotlin
df.merge { name.firstName and age and isHappy }
    .by { "${it[0]} aged ${it[1]} is " + (if (it[2] as Boolean) "" else "not ") + "happy" }
    .into("status")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mergeDifferentWith.html" width="100%"/>
<!---END-->

By default, when no `delimeter` or `merger` is specified, values will be merged into the [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/):

<!---FUN mergeDefault-->

```kotlin
df.merge { colsOf<Number>() }.into("data")
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mergeDefault.html" width="100%"/>
<!---END-->

Merged column values can also be exported to [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/):

<!---FUN mergeIntoList-->

```kotlin
// Merge data from two columns into List<String>
df.merge { name.firstName and name.lastName }.by(",").intoList()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mergeIntoList.html" width="100%"/>
<!---END-->
