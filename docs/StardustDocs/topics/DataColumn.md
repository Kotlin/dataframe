[//]: # (title: DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

[`DataColumn`](DataColumn.md) represents a column of values. It can store objects of primitive or reference types, or other [`DataFrames`](DataFrame.md).

See [how to create columns](createColumn.md)

### Properties
* `name: String` — name of the column, should be unique within containing dataframe
* `path: ColumnPath` — path to the column, depends on the way column was retrieved from dataframe
* `type: KType` — type of elements in the column
* `hasNulls: Boolean` — flag indicating whether column contains `null` values
* `values: Iterable<T>` — column data
* `size: Int` — number of elements in the column

### Column kinds
[`DataColumn`](DataColumn.md) instances can be one of three subtypes: `ValueColumn`, [`ColumnGroup`](DataColumn.md#columngroup) or [`FrameColumn`](DataColumn.md#framecolumn)

#### ValueColumn

Represents a sequence of values. 

It can store values of primitive (integers, strings, decimals etc.) or reference types. Currently, it uses [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) as underlying data storage.

#### ColumnGroup

Container for nested columns. Is used to create column hierarchy. 

#### FrameColumn

Special case of [`ValueColumn`](#valuecolumn) that stores other [`DataFrames`](DataFrame.md) as elements. 

[`DataFrames`](DataFrame.md) stored in [`FrameColumn`](DataColumn.md#framecolumn) may have different schemas. 

[`FrameColumn`](DataColumn.md#framecolumn) may appear after [reading](read.md) from JSON or other hierarchical data structures, or after grouping operations such as [groupBy](groupBy.md) or [pivot](pivot.md).  

## Column accessors

`ColumnAccessors` are used for [typed data access](columnAccessorsApi.md) in [`DataFrame`](DataFrame.md). `ColumnAccessor` stores column [`name`](#properties) (for top-level columns) or column path (for nested columns), has type argument that corresponds to [`type`](#properties) of thep column, but it doesn't contain any actual data.

<!---FUN columnAccessorsUsage-->

```kotlin
val age by column<Int>()

// Access fourth cell in the "age" column of dataframe `df`.
// This expression returns `Int` because variable `age` has `ColumnAccessor<Int>` type.
// If dataframe `df` has no column "age" or column "age" has type which is incompatible with `Int`,
// runtime exception will be thrown.
df[age][3] + 5

// Access first cell in the "age" column of dataframe `df`.
df[0][age] * 2

// Returns new dataframe sorted by age column (ascending)
df.sortBy(age)

// Returns new dataframe with the column "year of birth" added
df.add("year of birth") { 2021 - age }

// Returns new dataframe containing only rows with age > 30
df.filter { age > 30 }
```

<!---END-->

[Column accessors](DataColumn.md#column-accessors) are created by [property delegate](https://kotlinlang.org/docs/delegated-properties.html) `column`. Column [`type`](DataColumn.md#properties) should be passed as type argument, column [`name`](DataColumn.md#properties) will be taken from the variable name.

<!---FUN createColumnAccessor-->

```kotlin
val name by column<String>()
```

<!---END-->

To assign column name explicitly, pass it as an argument.

<!---FUN createColumnAccessorRenamed-->

```kotlin
val accessor by column<String>("complex column name")
```

<!---END-->

You can also create column accessors for [ColumnGroups](DataColumn.md#columngroup) and [FrameColumns](DataColumn.md#framecolumn)

<!---FUN createGroupOrFrameColumnAccessor-->

```kotlin
val columns by columnGroup()
val frames by frameColumn()
```

<!---END-->

To reference nested columns inside [ColumnGroups](DataColumn.md#columngroup), invoke `column<T>()` on accessor to parent [`ColumnGroup`](#columngroup):

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->

You can also create virtual accessor that doesn't point to a real column but computes some expression on every data access:

<!---FUN columnAccessorComputed-->
<tabs>
<tab title="Properties">

```kotlin
val fullName by column(df) { name.firstName + " " + name.lastName }

df[fullName]
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

val fullName by column { firstName() + " " + lastName() }

df[fullName]
```

</tab>
<tab title="Strings">

```kotlin
val fullName by column { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }

df[fullName]
```

</tab></tabs>
<!---END-->

If expression depends only on one column, you can also use `map`:

<!---FUN columnAccessorMap-->

```kotlin
val age by column<Int>()
val year by age.map { 2021 - it }

df.filter { year > 2000 }
```

<!---END-->

To convert `ColumnAccessor` into [`DataColumn`](DataColumn.md) add values using `withValues` function:

<!---FUN columnAccessorToColumn-->

```kotlin
val age by column<Int>()
val ageCol1 = age.withValues(15, 20)
val ageCol2 = age.withValues(1..10)
```

<!---END-->

