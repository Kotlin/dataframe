[//]: # (title: Interop with Collections)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Collections-->

_Kotlin DataFrame_ and _Kotlin Collection_ represent two different approaches to data storage:
* `DataFrame` stores data by fields/columns
* `Collection` stores data by records/rows

Although `DataFrame` doesn't implement `Collection` or `Iterable` interface, it has many similar operations, such as [`filter`](filter.md), [`take`](sliceRows.md#take.md), [`first`](first.md), [`map`](map.md), [`groupBy`](groupBy.md) etc.

`DataFrame` has two-way compatibility with `Map` and `List`:
* [toDataFrame](createDataFrame.md#todataframe)  — converts `List<T>` into `DataFrame<T>` and `Map<String, List<*>>` into `DataFrame<*>`.
* [toMap](toMap.md) and [toList](toList.md) — converts `DataFrame<T>` into `List<T>` or `Map<String, List<*>>`.

Columns, rows and values of `DataFrame` can be accessed as [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/), [`Iterable`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/) and [`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/) accordingly:

<!---FUN getRowsColumns-->

```kotlin
df.columns() // List<DataColumn>
df.rows() // Iterable<DataRow>
df.values() // Sequence<Any?>
```

<!---END-->

## Interop with data classes

`DataFrame` can be used as an intermediate object for transformation from one data structure to another.

Assume you have a list of instances of some [data class](https://kotlinlang.org/docs/data-classes.html) that you need to transform into some other format.

<!---FUN listInterop1-->

```kotlin
data class Input(val a: Int, val b: Int)

val list = listOf(Input(1, 2), Input(3, 4))
```

<!---END-->

You can convert this list into `DataFrame` using [`toDataFrame()`](createDataFrame.md#todataframe) extension:

<!---FUN listInterop2-->

```kotlin
val df = list.toDataFrame()
```

<!---END-->

Mark original data class with [`DataSchema`](schemas.md) annotation to get [extension properties](extensionPropertiesApi.md) and perform data transformations.

<!---FUN listInterop3-->

```kotlin
@DataSchema
data class Input(val a: Int, val b: Int)

val df2 = df.add("c") { a + b }
```

<!---END-->

<tip>

To enable extension properties generation you should use [dataframe plugin](gradle.md) for Gradle or [Kotlin jupyter kernel](installation.md)

</tip>

After data is transformed, `DataFrame` can be exported into `List` of another data class using [toList](toList.md) or [toListOf](toList.md#tolistof) extensions:

<!---FUN listInterop4-->

```kotlin
data class Output(val a: Int, val b: Int, val c: Int)

val result = df2.toListOf<Output>()
```

<!---END-->
