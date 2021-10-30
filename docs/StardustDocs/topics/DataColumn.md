[//]: # (title: DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

`DataColumn` represents a column of values. It can store primitive types, objects or other [`DataFrames`](DataFrame.md).

See [how to create columns](createColumn.md)

### Properties
* `name: String` - unique name of the column
* `type: KType` - type of elements in the column
* `size: Int` - length of the column
* `values: Iterable<T>` - column data
* `hasNulls: Boolean` - flag indicating whether column contains `null` values

### Column kinds
`DataColumn` instances can be one of three subtypes: `ValueColumn`, `ColumnGroup` or `FrameColumn`

#### ValueColumn

Represents a sequence of values. 

It can store values of primitive (integers, strings, decimals etc.) or object types. Currently, it uses [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) as underlying data storage.

#### ColumnGroup

Container for nested columns and is used to create column hierarchy. 

#### FrameColumn

Special case of [`ValueColumn`](#valuecolumn) that stores other [`DataFrames`](DataFrame.md) as elements. 

`DataFrames` stored in `FrameColumn` may have different schemas. 

`FrameColumn` may appear after [reading](read.md) from JSON or other hierarchical data structures, or after grouping operations such as [groupBy](groupBy.md) or [pivot](pivot.md).  

## Column conditions

## Column accessors

`ColumnAccessors` are used for [typed data access](columnAccessorsApi.md) in `DataFrame`:

<!---FUN columnAccessorsUsage-->

```kotlin
val age by column<Int>()

df[age][3] + 5
df[1][age] * 2
df.sortBy(age)
df.add("year of birth") { 2021 - age }
df.filter { age > 30 }
```

<!---END-->

See [how to create column accessor](createAccessor.md)

`ColumnAccessor` stores column [`name`](#column-properties) (for top-level columns) or column path (for nested columns), has type argument that corresponds to column [`type`](#column-properties), but doesn't contain any data.
To convert `ColumnAccessor` into `DataColumn` just add values:

<!---FUN columnAccessorToColumn-->

```kotlin
val age by column<Int>()
val ageCol = age.withValues(15, 20)
```

<!---END-->

