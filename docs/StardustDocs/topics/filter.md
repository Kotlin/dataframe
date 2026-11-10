[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.FilterSamples-->

<web-summary>
Discover the `filter` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover the `filter` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover the `filter` operation for Kotlin DataFrame.
</link-summary>

Keeps only the rows of a [`DataFrame`](DataFrame.md) that satisfy a [row condition](DataRow.md#row-conditions).
The order of the kept rows doesn't change.

Returns a new [`DataFrame`](DataFrame.md) with the rows that satisfy the condition.
If no row satisfies it, the result is an empty `DataFrame` with the same columns.

```text
df.filter { DataRow.(DataRow) -> Boolean }
```

The row condition receives each row both as `this` and as `it`, so the values of the row can be accessed directly
using [Extension Properties](extensionPropertiesApi.md) or
[`String` column accessors](StringApi.md#string-column-accessors),
including the values of nested columns of a [`ColumnGroup`](DataColumn.md#columngroup).

See also:

* [`drop`](drop.md), which removes the rows that satisfy a [row condition](DataRow.md#row-conditions).
* [`distinct`](distinct.md), which removes duplicate rows.
* [`takeWhile`](sliceRows.md#takewhile) / [`dropWhile`](sliceRows.md#dropwhile),
  which keep or remove only the first rows that satisfy a condition.
* [Filter rows](filterRows.md), an overview of the operations that filter rows.

**API reference**: [`filter`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/filter.html)

### Parameters

* `predicate: DataRow.(DataRow) -> Boolean` — a [row condition](DataRow.md#row-conditions)
  that returns `true` if the row should be kept in the result.

### Examples

The following dataframe will be used in the examples below.
The rows that satisfy the condition from the next example are highlighted:

<!---FUN filterDf-->

```kotlin
df
```

<inline-frame src="./resources/filterDf.html" width="100%" height="500px"></inline-frame>

<!---END-->

Keep only the people older than 18 whose first name starts with "A":

<!---FUN filter-->
<tabs>
<tab title="Properties">

```kotlin
df.filter { age > 18 && name.firstName.startsWith("A") }
```

</tab>
<tab title="Strings">

```kotlin
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }
```

</tab></tabs>
<inline-frame src="./resources/filter.html" width="100%" height="500px"></inline-frame>

<!---END-->

The row can also be taken as an explicit lambda parameter instead of `it`.

Besides the row values, the row condition can use the [`DataRow` API](DataRow.md#row-functions) —
for example, the index of the row and the previous row.
Keep the people starting from the third row whose previous person is happy:

<!---FUN filterWithRowLambda-->

```kotlin
df.filter { row -> row.index() >= 2 && row.prev()!!.isHappy }
```

<inline-frame src="./resources/filterWithRowLambda.html" width="100%" height="500px"></inline-frame>

<!---END-->

### `filter` on `DataColumn` {id="filter-on-a-datacolumn"}

Keeps only the values of a [`DataColumn`](DataColumn.md) that satisfy the given predicate.

Returns a new [`DataColumn`](DataColumn.md) with the same name, containing only the matching values in their
original order.

```text
col.filter { value -> Boolean }
```

#### Parameters {id="parameters_column"}

* `predicate: (value) -> Boolean` — a lambda that receives a value of the column
  and returns `true` if the value should be kept in the result.

#### Examples {id="examples_column"}

Keep only the ages greater than 17:

<!---FUN filterColumn-->

```kotlin
df.age.filter { it > 17 }
```

<inline-frame src="./resources/filterColumn.html" width="100%" height="500px"></inline-frame>

<!---END-->
