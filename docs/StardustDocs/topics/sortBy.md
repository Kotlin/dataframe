[//]: # (title: sortBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.SortBySamples-->

<web-summary>
Discover `sortBy` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `sortBy` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `sortBy` operation for Kotlin DataFrame.
</link-summary>

## sortBy

Sorts the rows of a [`DataFrame`](DataFrame.md) by the values of one or several columns.

Returns a new [`DataFrame`](DataFrame.md) with the same rows, reordered according to the selected columns.

```kotlin
sortBy { sortColumns }: DataFrame
sortBy(vararg columnNames: String): DataFrame

sortColumns = column [ .reversed() ] [ .nullsLast(flag: Boolean = true) ] [ and sortColumns ]
```

The order in which columns are selected defines the sort priority:
rows are compared by the first selected column, ties are resolved by the second one, and so on.

Column values must be [`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/).

**See also:**

* [`sortWith`](sortWith.md) — sorts rows with a custom comparator.
* [`reverse`](reverse.md) — reverses the current row order.
* [`shuffle`](shuffle.md) — reorders rows randomly.
* [`groupBy`](groupBy.md#transformation) — `GroupBy` has its own `sortBy`/`sortByDesc` (sorting rows inside
  each group) and `sortByGroup`/`sortByCount`/`sortByKey` (sorting the groups themselves).

### Parameters

* `sortColumns: SortColumnsSelector` — a lambda that selects the columns to sort by.
  Columns here are selected with the [Sort DSL](#sort-dsl) — a specialized
  [Columns Selection DSL](ColumnSelectors.md) that additionally allows reversing the order
  of a particular column and changing the position of `null` values.
* `columnNames: String` — one or several (`vararg`) names of the columns to sort by.
  This overload accepts only [string column names](concepts/StringApi.md), without any modifiers,
  so all of them are sorted in ascending order.

### Examples

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

### `sort` on `DataColumn`

Sorts the values of a [`DataColumn`](DataColumn.md) in ascending order.

Returns a new [`DataColumn`](DataColumn.md) with the same name and type containing the sorted values.

```kotlin
sort(): DataColumn<T>
```

Takes no parameters and accepts only columns whose value type `T` is
[`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/).
To sort values of any other type, use
[`sortWith` on a `DataColumn`](sortWith.md#sortwith-on-datacolumn).

#### Examples

<!---FUN sortColumn-->

```kotlin
df.age.sort()
```

<!---END-->
<inline-frame src="./resources/sortColumn.html" width="100%" height="500px"></inline-frame>

## Sort DSL

The Sort DSL (`SortDsl`) is a specialized [Columns Selection DSL](ColumnSelectors.md):
everything you can do there works here as well, and on top of that it allows changing
the sort direction of individual columns and the position of `null` values.

It's available in the `sortBy { }` and `sortByDesc { }` overloads that take a lambda;
the overloads that take [string column names](concepts/StringApi.md) always sort
in the default direction of the operation.

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

```kotlin
sortByDesc { sortColumns }: DataFrame
sortByDesc(vararg columnNames: String): DataFrame

sortColumns = column [ .reversed() ] [ .nullsLast(flag: Boolean = true) ] [ and sortColumns ]
```

It's the same operation as [`sortBy`](#sortby), except that every selected column is sorted in descending order
by default. All [Sort DSL](#sort-dsl) modifiers work the same way,
so `.reversed()` makes a particular column ascending again.

### Parameters

* `sortColumns: SortColumnsSelector` — a lambda that selects the columns to sort by
  with the [Sort DSL](#sort-dsl), the same way as in [`sortBy`](#sortby).
* `columnNames: String` — one or several (`vararg`) names of the columns to sort by.
  This overload accepts only [string column names](concepts/StringApi.md), without any modifiers,
  so all of them are sorted in descending order.

### Examples

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

### `sortDesc` on `DataColumn`

Sorts the values of a [`DataColumn`](DataColumn.md) in descending order.

Returns a new [`DataColumn`](DataColumn.md) with the same name and type containing the sorted values.

```kotlin
sortDesc(): DataColumn<T>
```

Takes no parameters and accepts only columns whose value type `T` is
[`Comparable`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-comparable/).

#### Examples

<!---FUN sortColumnDesc-->

```kotlin
df.age.sortDesc()
```

<!---END-->
<inline-frame src="./resources/sortColumnDesc.html" width="100%" height="500px"></inline-frame>
