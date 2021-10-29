[//]: # (title: Create ColumnAccessor)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

Create [column accessors](DataColumn.md#column-accessors):

<!---FUN createColumnAccessor-->

```kotlin
val name by column<String>()
```

<!---END-->

Column name is defined by the variable name. To explicitly specify column name you should pass it as an argument:

<!---FUN createColumnAccessorRenamed-->

```kotlin
val accessor = column<String>("complex column name")
```

<!---END-->

You can create column accessors to access [ColumnGroup](DataColumn.md#columngroup) or [FrameColumn](DataColumn.md#framecolumn)

<!---FUN createGroupOrFrameColumnAccessor-->

```kotlin
val columns by columnGroup()
val frames by frameColumn()
```

<!---END-->

To create deep column accessor that references nested columns inside [ColumnGroups](DataColumn.md#columngroup) apply `column()` extension at parent accessor:

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->

You can create transformed column accessor that will evaluate custom expression on every data access

<!---FUN columnAccessorMap-->

```kotlin
val age by column<Int>()
val year by age.map { 2021 - it }
df.filter { year > 2000 }
```

<!---END-->
