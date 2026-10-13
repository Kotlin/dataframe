# tail


<web-summary>
Discover `tail` operation in Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `tail` operation in Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `tail` operation in Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.info.TailSamples-->

Returns a [`DataFrame`](DataFrame.md) containing the last `numRows` rows, five by default.

```kotlin
df.tail(numRows: Int = 5)
```

If `numRows` is greater than or equal to the number of rows, the whole [`DataFrame`](DataFrame.md) is returned
as it is, column types included.
Otherwise, each column keeps its runtime type except for nullability: a nullable column
whose last `numRows` values contain no `null` becomes non-nullable in the result.
In the examples below, the runtime type of `city` stays `String?` in `df.tail()`, whose last five cities
include a `null`, and becomes `String` in `df.tail(2)`.

When `numRows` is `0`, the result has no rows but keeps all columns,
including the nested columns of column groups and frame columns.

A negative `numRows` throws an `IllegalArgumentException`.

This is equivalent to calling [`takeLast`](sliceRows.md#takelast) with the same `numRows`.

**Related operations**: [`head`](head.md), [`takeLast`](sliceRows.md#takelast), [`dropLast`](sliceRows.md#droplast).

### Examples

<!---FUN notebook_test_tail_1-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/notebook_test_tail_1.html" width="100%" height="500px"></inline-frame>

The last five rows, by default:

<!---FUN notebook_test_tail_2-->

```kotlin
df.tail()
```

<!---END-->
<inline-frame src="./resources/notebook_test_tail_2.html" width="100%" height="500px"></inline-frame>

The last two rows:

<!---FUN notebook_test_tail_3-->

```kotlin
df.tail(2)
```

<!---END-->
<inline-frame src="./resources/notebook_test_tail_3.html" width="100%" height="500px"></inline-frame>
