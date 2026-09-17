[//]: # (title: sortWith)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.SortWithSamples-->

<web-summary>
Discover `sortWith` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `sortWith` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `sortWith` operation for Kotlin DataFrame.
</link-summary>

## sortWith

Sorts the rows of a [`DataFrame`](DataFrame.md) using a custom row comparator.

Returns a new [`DataFrame`](DataFrame.md) with the same rows, reordered according to the given comparator.

```kotlin
sortWith(comparator: Comparator<DataRow>): DataFrame
sortWith { row1: DataRow, row2: DataRow -> Int }: DataFrame
```

Use it when the ordering can't be expressed as a combination of sort columns,
for example when it mixes several columns in a non-trivial way.

**See also:**

* [`sortBy`](sortBy.md) and [`sortByDesc`](sortBy.md#sortbydesc) — sort rows by the selected columns.
* [`reverse`](reverse.md) — reverses the current row order.
* [`shuffle`](shuffle.md) — reorders rows randomly.

### Parameters

* `comparator: Comparator<DataRow>` — a comparator of [`DataRow`](DataRow.md)s of this
  [`DataFrame`](DataFrame.md) that defines the order of rows.
* `{ row1: DataRow, row2: DataRow -> Int }` — the same comparator given as a lambda:
  it takes two [`DataRow`](DataRow.md)s and returns an `Int` — a negative, zero, or positive number
  depending on their relative order.

This operation doesn't select columns, so it has neither
[string column names](concepts/StringApi.md) nor
[Columns Selection DSL](ColumnSelectors.md) overloads: the whole row is available inside
the comparator, and any of its columns can be used there.

### Examples

The following dataframe will be used in the examples below:

<!---FUN sortWithDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/sortWithDf.html" width="100%" height="500px"></inline-frame>

Sort rows with a comparator built with the standard library functions:

<!---FUN sortWithComparator-->

```kotlin
// Sort rows by "age" ascending, then by ("name"/"lastName") descending
df.sortWith(
    compareBy<DataRow<Person>> { it.age }
        .thenByDescending { it.name.lastName },
)
```

<!---END-->
<inline-frame src="./resources/sortWithComparator.html" width="100%" height="500px"></inline-frame>

The same comparator can be given as a lambda that compares two rows directly:

<!---FUN sortWithLambda-->

```kotlin
// Sort rows by "age" ascending, then by ("name"/"firstName") ascending
df.sortWith { row1, row2 ->
    when {
        row1.age != row2.age -> row1.age.compareTo(row2.age)
        else -> row1.name.firstName.compareTo(row2.name.firstName)
    }
}
```

<!---END-->
<inline-frame src="./resources/sortWithLambda.html" width="100%" height="500px"></inline-frame>

### `sortWith` on `DataColumn`

Sorts the values of a [`DataColumn`](DataColumn.md) using a custom comparator.

Returns a new [`DataColumn`](DataColumn.md) with the same name and type containing the sorted values.

```kotlin
sortWith(comparator: Comparator<T>): DataColumn<T>
sortWith { value1: T, value2: T -> Int }: DataColumn<T>
```

Unlike [`sort`](sortBy.md#sort-on-datacolumn) and [`sortDesc`](sortBy.md#sortdesc-on-datacolumn),
which require [`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/) values,
`sortWith` works with values of any type `T`, including
[column groups](DataColumn.md#columngroup) and [frame columns](DataColumn.md#framecolumn).

#### Parameters

* `comparator: Comparator<T>` — a comparator of the column values, where `T` is the value type
  of this column.
* `{ value1: T, value2: T -> Int }` — the same comparator given as a lambda:
  it takes two column values and returns an `Int` — a negative, zero, or positive number
  depending on their relative order.

#### Examples

<!---FUN sortWithColumn-->

```kotlin
// Sort "name"/"lastName" values by their length
df.name.lastName.sortWith { name1, name2 -> name1.length - name2.length }
```

<!---END-->
<inline-frame src="./resources/sortWithColumn.html" width="100%" height="500px"></inline-frame>
