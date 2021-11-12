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

`merger` accepts a `List` of collected values for every row typed by their common type:

<!---FUN mergeSameWith-->

When heterogeneous columns are merged, they may need to be cast to valid types in `merger`:

<!---FUN mergeDifferentWith-->

By default, when no `delimeter` or `merger` is specified, values will be merged into the `List`:

<!---FUN mergeDefault-->
