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

**API reference**: [`tail`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/tail.html)

```kotlin
df.tail(numRows: Int = 5)
```

If `numRows` is greater than or equal to the number of rows, the whole [`DataFrame`](DataFrame.md) is returned.
A negative `numRows` throws an `IllegalArgumentException`.

The type of each column follows the values that are left: a nullable column
whose last `numRows` values contain no `null` becomes non-nullable in the result.

This is equivalent to calling [`takeLast`](sliceRows.md#takelast) with the same `numRows`.

**Related operations**: [`head`](head.md), [`takeLast`](sliceRows.md#takelast), [`dropLast`](sliceRows.md#droplast).

### Examples

<!---FUN notebook_test_tail_1-->

```kotlin
df
```

<inline-frame src="./resources/notebook_test_tail_1.html" width="100%" height="500px"></inline-frame>

<!---END-->

Default last 5 rows:
<!---FUN notebook_test_tail_2-->

```kotlin
df.tail()
```

<inline-frame src="./resources/notebook_test_tail_2.html" width="100%" height="500px"></inline-frame>

<!---END-->

Specify number of rows:
<!---FUN notebook_test_tail_3-->

```kotlin
df.tail(numRows = 2)
```

<inline-frame src="./resources/notebook_test_tail_3.html" width="100%" height="500px"></inline-frame>

<!---END-->
