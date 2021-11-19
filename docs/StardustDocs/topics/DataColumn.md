[//]: # (title: DataColumn)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

`DataColumn` represents a column of values. It can store objects of primitive or reference types, or other [`DataFrames`](DataFrame.md).

See [how to create columns](createColumn.md)

### Properties
* `name: String` - name of the column, should be unique within containing dataframe
* `type: KType` - type of elements in the column
* `size: Int` - number of elements in the column
* `values: Iterable<T>` - column data
* `hasNulls: Boolean` - flag indicating whether column contains `null` values

### Column kinds
`DataColumn` instances can be one of three subtypes: `ValueColumn`, `ColumnGroup` or `FrameColumn`

#### ValueColumn

Represents a sequence of values. 

It can store values of primitive (integers, strings, decimals etc.) or reference types. Currently, it uses [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/) as underlying data storage.

#### ColumnGroup

Container for nested columns. Is used to create column hierarchy. 

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

See [how to create column accessor](createAccessor.md)

`ColumnAccessor` stores column [`name`](#properties) (for top-level columns) or column path (for nested columns), has type argument that corresponds to column [`type`](#properties), but doesn't contain any data.
To convert `ColumnAccessor` into `DataColumn` just add values:

<!---FUN columnAccessorToColumn-->

```kotlin
val age by column<Int>()
val ageCol1 = age.withValues(15, 20)
val ageCol2 = age.withValues(1..10)
```

<!---END-->

