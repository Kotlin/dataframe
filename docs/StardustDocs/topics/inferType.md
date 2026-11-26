[//]: # (title: inferType)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.InferTypeSamples-->

Changes the type of the selected columns based on the runtime values stored in them.
The new type of a column is the nearest common supertype of its values and never goes outside
the current type of the column: the current type is an upper bound of the inferred type.
The inferred type is nullable if the column contains `null` values, and non-nullable otherwise.

**API reference**: [`inferType`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/infer-type.html)

```text
inferType [ { columnsSelector } ]
inferType(columnName: String, ...)
```
See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

The name, the position, and the values of every column stay the same; only the type can change.
A column group stays a column group, and the value columns inside it are inferred.
A frame column is returned as it is: the dataframes inside it keep their schema.

## When to use it

`inferType` exists for one practical case: a column can be created with a not precise type, for example
`Any?` — after reading data without a schema, after [filter](filter.md), or after a transformation that
disabled inference, such as [convert](convert.md) with `Infer.None`. `inferType` checks whether such a type
can be a more useful, specific one: in a [`DataFrame`](DataFrame.md) it is preferred to know what is inside
the columns.

More precise column types then help operations that depend on the schema, such as
[colsOf](ColumnSelectors.md#cols-of), [convertTo](convertTo.md),
[generateDataClasses](DataSchemaGenerationMethods.md#generatedataclasses), or schema checks.

The basic examples on this page use a [`DataFrame`](DataFrame.md) with a loosely typed `value` column:

<!---FUN inferTypeInput-->

```kotlin
val shownDf = dataFrameOf(
    "source" to columnOf("sensor", "manual", "sensor"),
    "value" to DataColumn.createValueColumn("value", listOf(1, null, 3), typeOf<Any?>()),
)
```

<inline-frame src="./resources/inferTypeInput.html" width="100%" height="500px"></inline-frame>

<!---END-->

A single [`DataColumn`](DataColumn.md) can be refined the same way — for example, after filtering:

<!---FUN inferTypeNarrowsColumnAfterFilter-->

```kotlin
val items = columnOf<Comparable<*>>("Alice", 1, 3.5)
val strings = items.filter { it is String }
val inferred = strings.inferType()
```

Output:

```text
before: kotlin.Comparable<*>
after: kotlin.String
```

<!---END-->

## What it does not do

`inferType` does not parse or convert values: `1` stays `1`, and a column of strings stays a column
of strings — `"1"` does not become `1`.

<!---FUN inferTypeDoesNotParseStrings-->

```kotlin
val numbersAsText = DataColumn.createValueColumn("numbersAsText", listOf("1", "2", "3"), typeOf<Any?>())
val inferred = numbersAsText.inferType()
```

Output:

```text
type: kotlin.String
```

<inline-frame src="./resources/inferTypeDoesNotParseStrings.html" width="100%" height="500px"></inline-frame>

<!---END-->

To parse `String` values into typed values, use [parse](parse.md).
To convert values explicitly, use [convert](convert.md).

The values are not unified either: `1` and `2.0` together give the common type `Number`,
and each value keeps its own type.

<!---FUN inferTypeMixedValues-->

```kotlin
val mixed = DataColumn.createValueColumn("mixed", listOf<Any?>(1, 2.0), typeOf<Any?>())
val inferred = mixed.inferType()
```

Output:

```text
type: kotlin.Number
```

<!---END-->

## Whole-DataFrame inference

`df.inferType()` infers the types of all value columns at any depth — the columns inside
[column groups](DataColumn.md#columngroup) too. Column groups stay column groups, and
frame columns stay frame columns; the dataframes inside frame columns keep their schema.

<!---FUN inferTypeAllColumns-->

```kotlin
val inferred = df.inferType()
```

Output:

```text
value: kotlin.Int?
```

<inline-frame src="./resources/inferTypeAllColumns.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Selected columns

Use a [column selector](ColumnSelectors.md) or column names to refine only some of the columns.
Among the selected columns, value columns are inferred, including the value columns inside a selected
column group; a selected column group stays a column group, and a frame column is returned as it is.

<!---FUN inferTypeSelectedColumns-->
<tabs>
<tab title="Properties">

```kotlin
val inferred = df.inferType { value }
```

</tab>
<tab title="Strings">

```kotlin
val inferred = df.inferType("value")
```

</tab></tabs>
Output:

```text
value: kotlin.Int?
```

<inline-frame src="./resources/inferTypeSelectedColumns.html" width="100%" height="500px"></inline-frame>

<!---END-->

## Working with refined types

Only the runtime types of the columns change: the static types known to the compiler
are not affected, so in a project with the compiler plugin the column accessors keep the old types.

Once the type is refined, operations that select columns by type see the column:

<!---FUN inferTypeSelectByType-->

```kotlin
val refined = df.inferType("value")
val selected = refined.select { colsOf<Int?>() }
```

Output:

```text
value: kotlin.Int?
```

<inline-frame src="./resources/inferTypeSelectByType.html" width="100%" height="500px"></inline-frame>

<!---END-->

## See also

* [parse](parse.md) — parse `String` values into typed values
* [convert](convert.md) — convert values with a type converter
* [convertTo](convertTo.md) — convert the values of the whole [`DataFrame`](DataFrame.md) to match a given schema
