# concat

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples-->

<web-summary>
Discover `concat` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `concat` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `concat` operation for Kotlin DataFrame.
</link-summary>

## `concat` on `DataFrame`

Appends the rows of one or more [`DataFrame`](DataFrame.md) objects or an iterable of [`DataRow`](DataRow.md)
objects to a `DataFrame`. Rows from every input keep their order, and the inputs are processed in the order in
which they are passed or iterated.

Returns a [`DataFrame`](DataFrame.md) containing the receiver's rows followed by all appended rows. The input
objects are not modified.

```kotlin
dataFrame.concat(vararg frames: DataFrame<T>): DataFrame<T>
dataFrame concat frame
dataFrame.concat(frames: Iterable<DataFrame<T>>): DataFrame<T>
dataFrame.concat(rows: Iterable<DataRow<T>>): DataFrame<T>
```

When `concat` combines dataframes, it unifies their schemas:

* The result contains the union of the input columns, ordered by their first appearance.
* When two or more input schemas are combined, values from columns with the same name form one result column. It keeps
  the contributing columns' shared runtime type, except for its nullability. If their runtime types differ, the result
  type is inferred from the concatenated values. If all contributing columns are empty, their runtime types
  determine the result type. The result type is nullable only if the resulting values contain `null`.
* If an input does not contain a result [`ValueColumn`](DataColumn.md#valuecolumn),
  that column is filled with `null` for the input's rows and becomes nullable. 
  A missing `List` column is filled with empty lists, and a missing [`FrameColumn`](DataColumn.md#framecolumn) 
  with empty `DataFrame`s instead; these columns do not become nullable.
  A missing [`ColumnGroup`](DataColumn.md#columngroup) remains a `ColumnGroup`,
  and its nested columns are filled according to these same rules recursively.

For overloads that append [`DataFrame`](DataFrame.md) objects, an appended `DataFrame` with no rows adds no rows,
but its columns still participate in schema unification. Calling the vararg overload without any `frames` or passing
an empty iterable of `DataFrame` objects returns the receiver as the same `DataFrame` instance.
An empty iterable of `DataRow` objects adds no rows and provides no additional schema to unify.

See also:

* [`add`](add.md), which adds columns to a `DataFrame`.
* [`append`](append.md), which appends rows supplied as values or schema objects.
* [`join`](join.md), which combines rows from two dataframes using matching keys or a condition.
* [Multiple DataFrames](multipleDataFrames.md), an overview of operations that combine dataframes.

### Parameters {id="parameters_df"}

* `vararg frames: DataFrame | frames: Iterable<DataFrame>` — the `DataFrame`s whose rows are appended.
  Vararg `frames` are processed in argument order; iterable `frames` are processed in iteration order.
* `frame: DataFrame` — a single `DataFrame` appended with infix syntax.
* `rows: Iterable<DataRow>` — the rows to append, processed in iteration order.

### Examples {id="examples_df"}

The following [`DataFrame`](DataFrame.md) is used in examples that share one input:

<!---FUN concatDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/concatDf.html" width="100%" height="500px"></inline-frame>

Pass several dataframes as vararg arguments to append their rows in argument order:

<!---FUN concatDataFrames-->

```kotlin
val firstBatch = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
val secondBatch = dataFrameOf("name", "age", "city")("Alice", 20, "London")
val thirdBatch = dataFrameOf("name", "age", "city")("Charlie", 25, "London")

firstBatch.concat(secondBatch, thirdBatch)
```

<!---END-->
<inline-frame src="./resources/concatDataFrames.html" width="100%" height="500px"></inline-frame>

### Schema unification examples

The following two dataframes have different schemas. Their `age` columns also have different runtime types:

<!---FUN concatDataFramesFirstInput-->

```kotlin
peopleDf1
```

<!---END-->
<inline-frame src="./resources/concatDataFramesFirstInput.html" width="100%" height="500px"></inline-frame>

<!---FUN concatDataFramesSecondInput-->

```kotlin
peopleDf2
```

<!---END-->
<inline-frame src="./resources/concatDataFramesSecondInput.html" width="100%" height="500px"></inline-frame>

`concat` combines both schemas. The resulting `age` column contains
`Int` and `Double` values and has the common runtime type `Number`. The missing `city` values are filled with
`null`, so `city` becomes nullable.

<!---FUN concatDataFramesWithDifferentSchemas-->

```kotlin
peopleDf1 concat peopleDf2
```

<!---END-->
<inline-frame src="./resources/concatDataFramesWithDifferentSchemas.html" width="100%" height="500px"></inline-frame>

New data can be added to a [`DataFrame`](DataFrame.md) as an iterable of rows:

<!---FUN concatDataFrameAndRows-->

```kotlin
val registeredPeople = dataFrameOf("name", "age", "city")("Alice", 20, "London")
val newPeople = dataFrameOf(
    "name" to columnOf("Bob", "Charlie"),
    "age" to columnOf(15, 25),
    "city" to columnOf("Paris", "London")
)

registeredPeople.concat(newPeople.rows())
```

<!---END-->
<inline-frame src="./resources/concatDataFrameAndRows.html" width="100%" height="500px"></inline-frame>

An iterable of `DataFrame`s can be appended in the same way:

<!---FUN concatDataFrameAndIterable-->

```kotlin
val registeredPeople = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
val newPeople = listOf(
    dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
    dataFrameOf("name", "age", "city")("Alice", 20, "London"),
)

registeredPeople.concat(newPeople)
```

<!---END-->
<inline-frame src="./resources/concatDataFrameAndIterable.html" width="100%" height="500px"></inline-frame>

## `concat` on `DataRow`

Appends one or more rows to a [`DataRow`](DataRow.md). Returns a [`DataFrame`](DataFrame.md) containing the receiver
followed by the supplied rows in argument order. If the rows have different schemas, they are unified according to
the same rules as [`DataFrame.concat`](#concat-on-dataframe).

```kotlin
row.concat(vararg rows: DataRow<T>): DataFrame<T>
```

### Parameters {id="parameters_row"}

* `vararg rows: DataRow` — the rows to append in argument order.

### Examples {id="examples_row"}

`DataRow.concat` is useful when working with operations that produce rows:

<!---FUN concatDataRows-->
<tabs>
<tab title="Properties">

```kotlin
val youngest = df.minBy { age }
val oldest = df.maxBy { age }

youngest.concat(oldest)
```

</tab>
<tab title="Strings">

```kotlin
val youngest = df.minBy("age")
val oldest = df.maxBy("age")

youngest.concat(oldest)
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/concatDataRows_properties.html" width="100%" height="500px"></inline-frame>

## `concat` on `DataColumn`

The result of `concat` on a [`DataColumn`](DataColumn.md) depends on the values stored in the column:

```kotlin
column.concat(vararg other: DataColumn<T>): DataColumn<T>
frameColumn.concat(): DataFrame<T>
collectionColumn.concat(): List<T>
```

For value columns, `concat` appends the values from `other` in argument order and preserves the receiver's name.
With one input column, its runtime type is preserved. With two or more input columns that have the same runtime type,
the result keeps this type except for its nullability. If their types differ, the result type is inferred from the
concatenated values. If all input columns are empty, their runtime types determine the result type.
The result runtime type is nullable only if the resulting values contain `null`.

For a column of `DataFrame` values, `concat` combines the rows of all stored dataframes and performs
schema unification. Dataframes are processed in their order of appearance, and row order within every dataframe is
preserved. An empty column of `DataFrame` values produces an empty `DataFrame`.
A stored dataframe with no rows adds no rows, but its columns still participate in schema unification.

For a column of `Collection<T>` values, it flattens the collections into a `List<T>` while preserving the order of
both the column values and the elements in each collection.

### Parameters {id="parameters_column"}

* `vararg other: DataColumn<T>` — the columns whose values are appended to the receiver in argument order.

### Examples {id="examples_column"}

Concatenating value columns keeps the name of the receiver and finds a common type for the values:

<!---FUN concatDataColumns-->

```kotlin
peopleDf1.age.concat(peopleDf2.age)
```

<!---END-->
<inline-frame src="./resources/concatDataColumns.html" width="100%" height="500px"></inline-frame>

A [`FrameColumn`](DataColumn.md#framecolumn) can store nested dataframes.
`concat()` combines those dataframes in their order of appearance:

<!---FUN concatFrameColumn-->

```kotlin
val teamMembers = columnOf(
    dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(20, 15),
    ),
    dataFrameOf("name", "age")("Charlie", 25),
)

teamMembers.concat()
```

<!---END-->
<inline-frame src="./resources/concatFrameColumn.html" width="100%" height="500px"></inline-frame>

Concatenating a column of collections flattens all collection values. Empty collections add no elements:

<!---FUN concatCollectionColumn-->

```kotlin
val nameGroups = columnOf<Collection<String>>(
    listOf("Alice", "Bob"),
    emptySet(),
    setOf("Charlie")
)

nameGroups.concat()
```

<!---END-->
<inline-frame src="./resources/concatCollectionColumn.html" width="100%" height="500px"></inline-frame>

## `concat` on `GroupBy`

A [`GroupBy`](groupBy.md) stores its grouping keys separately from the group `DataFrame`s.
[`GroupBy.concat()`](groupBy.md#aggregation) combines only those group dataframes, in group order, and preserves the row
order within each group. It does not add the separate key columns. If a group already contains a column with the
same name as a key, that original column remains unchanged.

`concatWithKeys()` also adds every grouping key column that is absent from a group. Each added key value is
repeated for every row in its group. An existing group column with the same name is not overwritten.

[`ReducedGroupBy.concat()`](groupBy.md#reducing) applies the stored reducer to every group and returns one result
row per group, in group order.

If the reduced rows have different schemas, the result contains the union of their schemas. Columns are ordered by
their first appearance in the reduced rows. Values missing from a reduced row follow the
[schema-unification rules for `DataFrame`s](#concat-on-dataframe).

If the reducer returns `null` for a group, the result still contains one row for that group. This row is treated as
missing every result column, so its values are filled according to the same rules.

```kotlin
groupBy.concat(): DataFrame<G>
groupBy.concatWithKeys(): DataFrame<G>
reducedGroupBy.concat(): DataFrame<G>
```

### Examples {id="examples_groupby"}

Here the grouping key `ageGroup` is created by an expression and is stored separately from the groups.
The `adult` group appears before the `teen` group, so `concatWithKeys()` changes the row order to Alice, Charlie, Bob.
It adds the `ageGroup` key column and repeats each key value for the rows in its group.
The added column is highlighted in the result,
and values of this column are colored according to the grouping expression:

<!---FUN concatGroupByWithKeys-->
<tabs>
<tab title="Properties">

```kotlin
val grouped = df.groupBy {
    expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
}

grouped.concatWithKeys()
```

</tab>
<tab title="Strings">

```kotlin
val grouped = df.groupBy {
    expr { if ("age"<Int>() >= 20) "adult" else "teen" } named "ageGroup"
}

grouped.concatWithKeys()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/concatGroupByWithKeys_properties.html" width="100%" height="500px"></inline-frame>

`concat()` does not include the `ageGroup` key column:

<!---FUN concatGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
val grouped = df.groupBy {
    expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
}

grouped.concat()
```

</tab>
<tab title="Strings">

```kotlin
val grouped = df.groupBy {
    expr { if ("age"<Int>() >= 20) "adult" else "teen" } named "ageGroup"
}

grouped.concat()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/concatGroupBy_properties.html" width="100%" height="500px"></inline-frame>

After a reducing operation such as [`first`](first.md), `concat()` applies the reducer and combines one selected
row from every group:

<!---FUN concatReducedGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.first().concat()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").first().concat()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/concatReducedGroupBy_properties.html" width="100%" height="500px"></inline-frame>

## `concat` on `Iterable`

`concat()` combines an `Iterable` of dataframes, columns, or nullable rows in iteration order:

```kotlin
frames: Iterable<DataFrame<T>>
frames.concat(): DataFrame<T>

columns: Iterable<DataColumn<T>>
columns.concat(): DataColumn<T>

rows: Iterable<DataRow<T>?>
rows.concat(): DataFrame<T>
```

For `Iterable<DataFrame>`, `concat` uses the same schema-unification rules as
[`DataFrame.concat`](#concat-on-dataframe). A `DataFrame` with no rows adds no rows, but its columns still
participate in schema unification. An empty iterable returns an empty [`DataFrame`](DataFrame.md).

For `Iterable<DataColumn>`, the values form one result column. The result keeps the name of the first column and
uses the same type rules as [`DataColumn.concat`](#concat-on-datacolumn). An empty iterable returns an empty column.

For `Iterable<DataRow?>`, `concat` returns a `DataFrame` containing rows from the iterable in iteration order.
Non-null rows follow the same schema-unification rules described for [`DataRow.concat`](#concat-on-datarow). A `null`
element contributes one result row that is treated as missing every result column, so its values follow the same
missing-column rules. An empty iterable returns an empty [`DataFrame`](DataFrame.md).

### Examples {id="examples_iterable"}

An iterable of dataframes is concatenated in iteration order:

<!---FUN concatIterableDataFrames-->

```kotlin
val batches = listOf(
    dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
    dataFrameOf(
        "name" to columnOf("Alice", "Bob"),
        "age" to columnOf(20, 15),
        "city" to columnOf("London", "Paris"),
    ),
)

batches.concat()
```

<!---END-->
<inline-frame src="./resources/concatIterableDataFrames.html" width="100%" height="500px"></inline-frame>

An iterable of columns is concatenated into one column. Its name comes from the first input column:

<!---FUN concatIterableDataColumns-->

```kotlin
val ageColumns = listOf(
    columnOf(20, 25).named("age"),
    columnOf(15).named("additionalAges"),
)

ageColumns.concat()
```

<!---END-->
<inline-frame src="./resources/concatIterableDataColumns.html" width="100%" height="500px"></inline-frame>

A `null` element in an iterable of rows becomes a row with `null` values:

<!---FUN concatIterableRows-->

```kotlin
val rows = listOf(df[0], null, df[1])

rows.concat()
```

<!---END-->
<inline-frame src="./resources/concatIterableRows.html" width="100%" height="500px"></inline-frame>
