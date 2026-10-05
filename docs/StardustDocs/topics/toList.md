[//]: # (title: toList)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop.ToListSamples-->

Converts a [`DataFrame`](DataFrame.md) into a [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/)
of data class instances, one per row, in the order of the rows.

```text
toList()
toListOf<T>()
```

`toList()` creates instances of the type argument of the [`DataFrame`](DataFrame.md).
`toListOf<T>()` creates instances of the `T` you give in the call, whatever the type argument of the [`DataFrame`](DataFrame.md) is.

**Related operations**: [toDataFrame](createDataFrame.md#todataframe) (the reverse operation), [convertTo](convertTo.md), [toMap](toMap.md).

**More info**: [](collectionsInterop.md)

## How rows become objects

The rules are the same for `toList`, `toListOf`, `toSequence` and `toSequenceOf`.

* `T` has to be a data class. An interface does not work, even one marked with [`@DataSchema`](schemas.md).
  A generic data class works too, for example, `Pair<String, Int>`.
* Each instance is created by the primary constructor of `T`.
  Each constructor parameter gets the value of the column with the same name,
  or with the name given in `@ColumnName`. Names are case-sensitive.
* The order of the columns does not matter, and columns that match no parameter are ignored.
* A parameter with a default value gets this value when there is no column for it.
* When a column type differs from the parameter type, the values are converted the same way
  as in [`convertTo`](convertTo.md), for example, from `Int` to `Long`.
* A [`ColumnGroup`](DataColumn.md#columngroup) becomes a nested data class,
  and a [`FrameColumn`](DataColumn.md#framecolumn) becomes a `List` of data class instances,
  or a `DataFrame` that is passed as it is, with all its columns.

An exception is thrown when:

* `T` is not a data class;
* there is no column for a constructor parameter without a default value;
* a column has `null` values, but its parameter is not nullable;
* a value cannot be converted to the parameter type.

## toList

`toList()` creates instances of the type argument of the [`DataFrame`](DataFrame.md),
following the rules in [How rows become objects](#how-rows-become-objects).

Use it on a [`DataFrame`](DataFrame.md) whose type argument is a data class.
On a `DataFrame<*>`, such as the result of `dataFrameOf` or of reading a file,
`toList()` throws an exception, because there is no data class to create.
With the [compiler plugin](Compiler-Plugin.md), the same happens even right after
[`toDataFrame()`](createDataFrame.md#todataframe) on a list of data class instances:
the plugin gives the result a generated type argument, which is not a data class.
In these cases, use [`toListOf`](#tolistof), or [`cast`](cast.md) the [`DataFrame`](DataFrame.md) to a data class first:

<!---FUN toListAfterCast-->

```kotlin
data class Input(val a: Int, val b: Int)

val df = dataFrameOf("a", "b")(1, 2, 3, 4)

df.cast<Input>().toList()
```

Output:

```text
[Input(a=1, b=2), Input(a=3, b=4)]
```

<!---END-->

## toListOf

`toListOf<T>()` creates instances of the `T` given in the call,
following the rules in [How rows become objects](#how-rows-become-objects).
The type argument of the [`DataFrame`](DataFrame.md) does not matter, so it works on a `DataFrame<*>` too.

Here the `fullName` column group becomes a nested `FullName`:

<!---FUN toListOfNestedDataClass-->

```kotlin
val df = dataFrameOf("name", "lastName", "age")("John", "Doe", 21)
    .group("name", "lastName").into("fullName")

data class FullName(val name: String, val lastName: String)

data class Person(val fullName: FullName, val age: Int)

df.toListOf<Person>()
```

Output:

```text
[Person(fullName=FullName(name=John, lastName=Doe), age=21)]
```

<!---END-->

## toSequence and toSequenceOf

`toSequence()` and `toSequenceOf<T>()` are the same as `toList()` and `toListOf<T>()`,
but they return a [`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/).
The rules in [How rows become objects](#how-rows-become-objects) apply to them too.

```text
toSequence()
toSequenceOf<T>()
```

The columns are matched and converted when the function is called, so the exceptions are thrown right away.
The instances are created only when the sequence is iterated, one per row that is read,
and each iteration creates them again.
Here only the first row becomes an object.
The dataframes and the data classes are the ones from the [toList](#tolist) and [toListOf](#tolistof) examples:

<!---FUN toSequenceAfterCast-->

```kotlin
df.cast<Input>().toSequence().first()
```

Output:

```text
Input(a=1, b=2)
```

<!---END-->

<!---FUN toSequenceOfFirst-->

```kotlin
df.toSequenceOf<Person>().first()
```

Output:

```text
Person(fullName=FullName(name=John, lastName=Doe), age=21)
```

<!---END-->
