[//]: # (title: sortBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.SortBySamples-->

Sorts the rows of a [`DataFrame`](DataFrame.md) by the values of one or several columns.

Returns a new [`DataFrame`](DataFrame.md) with the same rows, reordered according to the selected columns.

```text
sortBy { sortColumns } | sortBy(columnName, ...)

sortColumns = column [ .reversed() ] [ .nullsLast(flag = true) ] [ and sortColumns ]
```

The order in which columns are selected defines the sort priority:
rows are compared by the first selected column, ties are resolved by the second one, and so on.

Column values must be [`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/).

**Parameters:**

* `sortColumns` — columns to sort by, selected with the [Sort DSL](#sort-dsl)
  (an extension of the [Column Selection DSL](ColumnSelectors.md) with sort-specific modifiers).
* `columnName: String` — names of the columns to sort by; they are always sorted in ascending order.

**See also:**

* [`sortByDesc`](#sortbydesc) — sorts rows in descending order by default.
* [`sortWith`](#sortwith) — sorts rows with a custom comparator.
* [`reverse`](reverse.md) — reverses the current row order.
* [`shuffle`](shuffle.md) — reorders rows randomly.
* [`groupBy`](groupBy.md#transformation) — `GroupBy` has its own `sortBy`/`sortByDesc` (sorting rows inside
  each group) and `sortByGroup`/`sortByCount`/`sortByKey` (sorting the groups themselves).

The following dataframe will be used in the examples below:

<!---FUN sortByDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/sortByDf.html" width="100%" height="500px"></inline-frame>

Sort rows by a single column:

<!---FUN sortBy-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { age }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy("age")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortBy_properties.html" width="100%" height="500px"></inline-frame>

Sort rows by several columns — here by `age` first, and then by `name/lastName` for rows with equal `age`:

<!---FUN sortBySeveralColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { age and name.lastName }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy { "age" and "name"["lastName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortBySeveralColumns_properties.html" width="100%" height="500px"></inline-frame>

## Sort DSL

The Sort DSL is a specialized [Column Selection DSL](ColumnSelectors.md) that, in addition to selecting columns,
allows changing the sort direction of individual columns and the position of `null` values.

### reversed

`.reversed()` reverses the sort order for a single column or a column set:
in [`sortBy`](#sortby) it makes the column descending, and in [`sortByDesc`](#sortbydesc) — ascending.

<!---FUN sortByReversed-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { age.reversed() and name.lastName }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy { "age".reversed() and "name"["lastName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByReversed_properties.html" width="100%" height="500px"></inline-frame>

### nullsLast

By default, `null` values are considered the smallest values, so in ascending order they come first:

<!---FUN sortByNulls-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { weight }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy("weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByNulls_properties.html" width="100%" height="500px"></inline-frame>

`.nullsLast(flag: Boolean = true)` treats `null` values as the largest ones instead,
so they are placed after all non-null values when sorting in ascending order
(and before them when sorting in descending order).
With `flag = false` the selected columns keep the default `null` ordering,
which is handy when the position of `null`s is computed at runtime.

<!---FUN sortByNullsLast-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { weight.nullsLast() }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy { "weight".nullsLast() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByNullsLast_properties.html" width="100%" height="500px"></inline-frame>

### Sorting by an expression

Sort keys don't have to be existing columns — they can be computed inline with
[`expr { }`](ColumnSelectors.md#expr-column-expression), and all the modifiers above apply to them as well.

<!---FUN sortByExpr-->

```kotlin
// Sort rows by the full name length
df.sortBy { expr { name.firstName.length + name.lastName.length } }
```

<!---END-->
<inline-frame src="./resources/sortByExpr.html" width="100%" height="500px"></inline-frame>

## sortByDesc

Sorts the rows of a [`DataFrame`](DataFrame.md) by the values of one or several columns in descending order.

Returns a new [`DataFrame`](DataFrame.md) with the same rows, reordered according to the selected columns.

```text
sortByDesc { sortColumns } | sortByDesc(columnName, ...)

sortColumns = column [ .reversed() ] [ .nullsLast(flag = true) ] [ and sortColumns ]
```

It's the same operation as [`sortBy`](#sortby), except that every selected column is sorted in descending order
by default. All [Sort DSL](#sort-dsl) modifiers work the same way,
so `.reversed()` makes a particular column ascending again.

**Parameters:**

* `sortColumns` — columns to sort by, selected with the [Sort DSL](#sort-dsl).
* `columnName: String` — names of the columns to sort by; they are always sorted in descending order.

<!---FUN sortByDesc-->
<tabs>
<tab title="Properties">

```kotlin
df.sortByDesc { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.sortByDesc("age", "weight")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByDesc_properties.html" width="100%" height="500px"></inline-frame>

Sort by `age` descending, and then by `name/lastName` ascending:

<!---FUN sortByDescReversed-->
<tabs>
<tab title="Properties">

```kotlin
df.sortByDesc { age and name.lastName.reversed() }
```

</tab>
<tab title="Strings">

```kotlin
df.sortByDesc { "age" and "name"["lastName"].reversed() }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/sortByDescReversed_properties.html" width="100%" height="500px"></inline-frame>

## sortWith

Sorts the rows of a [`DataFrame`](DataFrame.md) using a custom row comparator.

Returns a new [`DataFrame`](DataFrame.md) with the same rows, reordered according to the given comparator.

```text
sortWith(comparator)
sortWith { row1, row2 -> Int }
```

**Parameters:**

* `comparator: Comparator<DataRow<T>>` — a comparator of [`DataRow`](DataRow.md)s that defines the row order;
* `{ row1, row2 -> Int }` — the same comparator given as a lambda:
  it takes two [`DataRow`](DataRow.md)s and returns a negative, zero, or positive number
  depending on their relative order.

Use it when the ordering can't be expressed as a combination of sort columns,
for example when it mixes several columns in a non-trivial way.

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

## Sorting a DataColumn

A [`DataColumn`](DataColumn.md) can be sorted on its own.
All these operations return a new [`DataColumn`](DataColumn.md) with the same name and type
containing the sorted values.

* `sort()` — sorts the values in ascending order; accepts only
  [`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/) values.
* `sortDesc()` — sorts the values in descending order; accepts only
  [`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/) values.
* `sortWith(comparator)` — sorts the values with the given `Comparator<T>` (or a comparison lambda);
  works with values of any type, including
  [column groups](DataColumn.md#columngroup) and [frame columns](DataColumn.md#framecolumn).

<!---FUN sortColumn-->

```kotlin
df.age.sort()
```

<!---END-->
<inline-frame src="./resources/sortColumn.html" width="100%" height="500px"></inline-frame>

<!---FUN sortColumnDesc-->

```kotlin
df.age.sortDesc()
```

<!---END-->
<inline-frame src="./resources/sortColumnDesc.html" width="100%" height="500px"></inline-frame>

<!---FUN sortColumnWith-->

```kotlin
// Sort "name"/"lastName" values by their length
df.name.lastName.sortWith { name1, name2 -> name1.length - name2.length }
```

<!---END-->
<inline-frame src="./resources/sortColumnWith.html" width="100%" height="500px"></inline-frame>
