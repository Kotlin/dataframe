[//]: # (title: head)

<web-summary>
Discover `head` operation in Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `head` operation in Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `head` operation in Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.info.HeadSamples-->

Returns a [`DataFrame`](DataFrame.md) containing the first `numRows` rows, five by default.

```kotlin
df.head(numRows: Int = 5)
```

If `numRows` is greater than or equal to the number of rows, the whole [`DataFrame`](DataFrame.md) is returned
as it is, column types included.
Otherwise, each column keeps its runtime type except for nullability: a nullable column
whose first `numRows` values contain no `null` becomes non-nullable in the result.
In the examples below, the runtime type of `city` is `String?` in `df` and `String` in `df.head()`.

When `numRows` is `0`, the result has no rows but keeps all columns,
including the nested columns of column groups and frame columns.

A negative `numRows` throws an `IllegalArgumentException`.

This is equivalent to calling [`take`](sliceRows.md#take) with the same `numRows`.

**Related operations**: [`tail`](tail.md), [`take`](sliceRows.md#take), [`drop`](sliceRows.md#drop).

### Examples

<!---FUN headInput-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/headInput.html" width="100%" height="500px"></inline-frame>

The first five rows, by default:

<!---FUN headDefault-->

```kotlin
df.head()
```

<!---END-->
<inline-frame src="./resources/headDefault.html" width="100%" height="500px"></inline-frame>

The first three rows:

<!---FUN headNumRows-->

```kotlin
df.head(3)
```

<!---END-->
<inline-frame src="./resources/headNumRows.html" width="100%" height="500px"></inline-frame>
