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

Returns a [`DataFrame`](DataFrame.md) with the last five rows.

**API reference**: [`tail`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/tail.html)

This is equivalent to calling [`takeLast`](sliceRows.md#takelast) with the same `numRows` argument.
By default, `numRows = 5`.

```kotlin
df.tail(numRows: Int = 5)
```

**Related operations**: [`head`](head.md), [`takeLast`](sliceRows.md#takelast), [`take`](sliceRows.md#take).

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
