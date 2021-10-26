[//]: # (title: Create ColumnAccessor)

Create [column accessors](DataColumn.md#column-accessors) and store it in the variable with the same name as column name:

<!---FUN createColumnAccessor-->

```kotlin
val name by column<String>()
```

<!---END-->

To explicitly specify column name pass it as an argument:

<!---FUN createColumnAccessorRenamed-->

```kotlin
val accessor = column<String>("complex column name")
```

<!---END-->

You can also create column accessors to access [ColumnGroup](DataColumn.md#columngroup) or [FrameColumn](DataColumn.md#framecolumn)

<!---FUN createGroupOrFrameColumnAccessor-->

```kotlin
val columns by columnGroup()
val frames by frameColumn()
```

<!---END-->

And you can create deep column accessors for columns within [ColumnGroup](DataColumn.md#columngroup)

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->
