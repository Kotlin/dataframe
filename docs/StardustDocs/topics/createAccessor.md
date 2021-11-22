[//]: # (title: Create ColumnAccessor)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Create-->

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

### Deep column accessors

Deep column accessor references nested columns inside [ColumnGroups](DataColumn.md#columngroup).

<!---FUN createDeepColumnAccessor-->

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
```

<!---END-->

### Computed column accessors

Computed column accessor evaluates custom expression on every data access.

<!---FUN columnAccessorComputed-->
<tabs>
<tab title="Properties">

```kotlin
val fullName by df.column { name.firstName + " " + name.lastName }

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

When expression depends only on one column, use `map`:

<!---FUN columnAccessorMap-->

```kotlin
val age by column<Int>()
val year by age.map { 2021 - it }

df.filter { year > 2000 }
```

<!---END-->
