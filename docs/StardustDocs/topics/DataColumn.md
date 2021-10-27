[//]: # (title: DataColumn)

## Column types

### ValueColumn

### ColumnGroup

### FrameColumn

[How to create columns](createColumn.md)

## Column properties
* `name: String`
* `path: ColumnPath`
* `type: KType`
* `hasNulls: Boolean`

## Column conditions

## Column accessors

`ColumnAccessors` can be used for typed data access in `DataFrame`. `ColumnAccessor` stores the name and type of the column, but doesn't contain column values.

<!---docs.Base.CreateColumns.namedColumnWithoutValues-->
```kotlin
val name by column<String>()
val col = column<String>("name")
```
<!---END-->
All `DataFrame` operations support typed data access via `ColumnAccessors`:
<!---docs.Base.CreateColumns.colRefForTypedAccess-->
```kotlin
df.filter { it[name].startsWith("A") }
df.sortBy { col }
```
<!---END-->
`ColumnAccessor` can be converted to `DataColumn` by adding values:
```kotlin
val col = name.withValues("Alice", "Bob")
```
or for `Iterable` of values:
```kotlin
val values = listOf("Alice", "Bob")
val col = name.withValues(values)
val col = values.toColumn(name)
```
