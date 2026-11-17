[//]: # (title: head)

<web-summary>
Discover the `head` operation in Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `head` operation in Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `head` operation in Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.info.HeadSamples-->

**API reference**: [`head`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/head.html)

Returns a [`DataFrame`](DataFrame.md) containing the first `numRows` rows, five by default.

```text
head(numRows: Int = 5): DataFrame
```

If `numRows` is greater than or equal to the number of rows, the whole [`DataFrame`](DataFrame.md) is returned
as it is.
Otherwise, each column keeps its runtime type except for nullability: a nullable column
whose first `numRows` values contain no `null` becomes non-nullable at runtime;
the compile-time type is not affected.
In the examples below, the runtime type of `city` is `String?` in `df` and `String` in `df.head()`.

When `numRows` is `0`, the result has no rows but keeps all columns:
a column group keeps its nested columns, and a frame column keeps its schema.

A negative `numRows` throws an `IllegalArgumentException`.

This is equivalent to calling [`take`](sliceRows.md#take) with the same `numRows`.

**Related operations**: [`tail`](tail.md), [`take`](sliceRows.md#take), [`drop`](sliceRows.md#drop).

### Examples

<!---FUN headInput-->

```kotlin
df
```

<inline-frame src="./resources/headInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

The first five rows, by default:

<!---FUN headDefault-->

```kotlin
df.head()
```

<inline-frame src="./resources/headDefault.html" width="100%" height="500px"></inline-frame>

<!---END-->

The first three rows:

<!---FUN headNumRows-->

```kotlin
df.head(3)
```

<inline-frame src="./resources/headNumRows.html" width="100%" height="500px"></inline-frame>

<!---END-->
