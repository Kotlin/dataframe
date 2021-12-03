[//]: # (title: Interop with Stdlib)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

`DataFrame` doesn't implement `Iterable` interface, but redefines some of extension functions available for `Iterable`:

<!---FUN iterableApi-->

```kotlin
df.forEachRow { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```

<!---END-->

To convert `DataFrame` into `Iterable`/`Sequence` of rows, columns or cell values:

<!---FUN getRowsColumns-->

```kotlin
df.columns() // List<DataColumn>
df.rows() // Iterable<DataRow>
df.values() // Sequence<Any?>
```

<!---END-->

* [toMap](toMap.md) - converts `DataFrame` into mapping from column names to column types `Map<String, List<*>>`
* [toList](toList.md) - converts `DataFrame` into `List` of data class objects 
