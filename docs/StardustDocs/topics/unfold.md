[//]: # (title: unfold)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.UnfoldSamples-->

Returns a [`DataFrame`](DataFrame.md) in which the selected columns of objects are turned into
[`ColumnGroup`](DataColumn.md#columngroup)s, with a column for every public property of these objects.

**Related operations**: [](updateConvert.md)

```text
unfold(roots, maxDepth) { columns }: DataFrame
unfold(columnNames): DataFrame
```

`unfold` builds the new columns the way [`toDataFrame()`](createDataFrame.md#todataframe) builds a
[`DataFrame`](DataFrame.md) from a list of objects, but for a column inside an existing dataframe.
It is useful when a column holds instances of ordinary classes — for example, the ones a library API returns —
and you want to work with their properties as columns: select, filter, sort, or group by them.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

Every example on this page uses the same classes and objects as the
[`toDataFrame()` example](createDataFrame.md#dataframe-from-iterable-t), in a column called `student`:

<!---FUN unfoldDf-->

```kotlin
data class Name(val firstName: String, val lastName: String)

data class Score(val subject: String, val value: Int)

data class Student(val name: Name, val age: Int, val scores: List<Score>)

val df = dataFrameOf(
    "id" to columnOf(1, 2),
    "student" to columnOf(
        Student(Name("Alice", "Cooper"), 15, listOf(Score("math", 4), Score("biology", 3))),
        Student(Name("Bob", "Marley"), 20, listOf(Score("music", 5))),
    ),
    "year" to columnOf(2021, 2022),
)
```

<!---END-->
<inline-frame src="./resources/unfoldDf.html" width="100%" height="500px"></inline-frame>

## unfold

<!---FUN unfold-->
<tabs>
<tab title="Properties">

```kotlin
df.unfold { student }
```

</tab>
<tab title="Strings">

```kotlin
df.unfold("student")
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/unfold_properties.html" width="100%" height="500px"></inline-frame>

The new columns are named after the properties, in the order of the primary constructor
(for a class without one, in the order of their names),
and the column group has the name of the column it is made from.
The other columns and the order of the columns stay the same.
An object that is `null` gives `null` in every new column,
except that a list property that becomes a [`FrameColumn`](DataColumn.md#framecolumn) gets an empty dataframe.
The properties are those of the type of the column.

## maxDepth

By default, the values of the properties stay as they are: `name` holds `Name` objects, and `scores` holds lists.
With `maxDepth = 1`, nested objects become [`ColumnGroup`](DataColumn.md#columngroup)s,
and lists of objects become [`FrameColumn`](DataColumn.md#framecolumn)s;
every further step goes one level deeper.

<!---FUN unfoldMaxDepth-->
<tabs>
<tab title="Properties">

```kotlin
df.unfold(maxDepth = 1) { student }
```

</tab>
<tab title="Strings">

```kotlin
df.unfold(maxDepth = 1) { col("student") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/unfoldMaxDepth_properties.html" width="100%" height="500px"></inline-frame>

Properties of a [`@DataSchema`](schemas.md) type, lists of them, [`DataRow`](DataRow.md)s,
and [`DataFrame`](DataFrame.md)s are always unfolded into column groups and frame columns, whatever `maxDepth` is.

## Choosing properties

Pass the properties, `roots`, to make columns only for them:

<!---FUN unfoldRoots-->
<tabs>
<tab title="Properties">

```kotlin
df.unfold(Student::name, Student::age) { student }
```

</tab>
<tab title="Strings">

```kotlin
df.unfold(Student::name, Student::age) { col("student") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/unfoldRoots_properties.html" width="100%" height="500px"></inline-frame>

`roots` can also be getter-like functions, such as `getX()`.
They only apply to a column that can be unfolded:
a column of strings stays as it is even with `String::length` among `roots`.

With the [compiler plugin](Compiler-Plugin.md), a call with `roots` gets an empty compile-time schema for now,
so the result has no typed accessors.

## Columns that stay as they are

A column that cannot be unfolded stays as it is. These are:
* columns of simple values, such as numbers, strings, or enums;
* columns of objects without public properties;
* columns whose type is `Any` — for example, with objects of different classes;
  with [`unfold` on DataColumn](#unfold-on-datacolumn), only when the compiler sees the column as `Any` too;
* [`ColumnGroup`](DataColumn.md#columngroup)s and [`FrameColumn`](DataColumn.md#framecolumn)s.

## unfold on DataColumn

Unfolds a single [`DataColumn`](DataColumn.md) and returns a [`ColumnGroup`](DataColumn.md#columngroup)
with the name of the column, or the column itself when it cannot be unfolded.

```text
DataColumn.unfold(roots, maxDepth): DataColumn
```

<!---FUN unfoldOnColumn-->

```kotlin
df.student.unfold(maxDepth = 1)
```

<!---END-->
<inline-frame src="./resources/unfoldOnColumn.html" width="100%" height="500px"></inline-frame>

The properties are those of the type of the column as the compiler sees it.
When that type cannot be unfolded itself, such as `Any?` of an untyped column (`df["student"]`),
the properties are those of the type of the column at runtime.
A `DataColumn<Student>` is unfolded even when the type of the column at runtime is `Any`.

## See also

* [`toDataFrame()`](createDataFrame.md#todataframe) — builds a [`DataFrame`](DataFrame.md) from a list of objects
  by reading their properties.
* [`flatten`](flatten.md) and [`ungroup`](ungroup.md) — remove the column groups that `unfold` creates.
* [`convert`](convert.md) and [`replace`](replace.md) — change the selected columns in any other way.
* Don't confuse `unfold` with [`explode`](explode.md), which puts the elements of lists into separate rows.
