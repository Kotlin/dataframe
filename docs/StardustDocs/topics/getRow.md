[//]: # (title: Get row)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

You can get a single [`DataRow`](DataRow.md) by [index](indexing.md):

<!---FUN getRowByIndex-->

```kotlin
df[2]
```

<!---END-->

Or you can get single row by [row condition](DataRow.md#row-conditions):

<!---FUN getRowByCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.single { age == 45 }
df.first { weight != null }
df.minBy { age }
df.maxBy { name.firstName.length }
df.maxByOrNull { weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()

df.single { age() == 45 }
df.first { weight() != null }
df.minBy(age)
df.maxBy { firstName().length }
df.maxByOrNull { weight() }
```

</tab>
<tab title="Strings">

```kotlin
df.single { "age"<Int>() == 45 }
df.first { it["weight"] != null }
df.minBy("weight")
df.maxBy { "name"["firstName"]<String>().length }
df.maxByOrNull("weight")
```

</tab></tabs>
<!---END-->

## single

Returns the single [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or throws exception if there is no or more than one matching row.

## singleOrNull

Returns the single [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or `null` if there is no or more than one matching row.

## first

Returns the first [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or throws exception if there is no matching rows.

### firstOrNull

Returns the first [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or `null` if there is no matching rows.

## last

Returns the last [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or throws exception if there is no matching rows.

### lastOrNull

Returns the last [row](DataRow.md) that matches the given [condition](DataRow.md#row-conditions), or `null` if there is no matching rows.

## maxBy

Returns the first [row](DataRow.md) that has the largest value in the given column, or throws exception if `DataFrame` is empty.

### maxByOrNull

Returns the first [row](DataRow.md) that has the largest value in the given column, or `null` if `DataFrame` is empty.

## minBy

Returns the first [row](DataRow.md) that has the smallest value in the given column, or throws exception if `DataFrame` is empty.

## minByOrNull

Returns the first [row](DataRow.md) that has the smallest value in the given column, or `null` if `DataFrame` is empty.
