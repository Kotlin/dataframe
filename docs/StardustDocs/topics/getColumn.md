[//]: # (title: getColumn)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Return column by column name or [column selector](ColumnSelectors.md) as [`DataColumn`](DataColumn.md). Throws exception if requested column doesn't exist.

<!---FUN getColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.getColumn { age }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.getColumn { age }
```

</tab>
<tab title="Strings">

```kotlin
df.getColumn("age")
```

</tab></tabs>
<!---END-->

## getColumnOrNull

Return top-level column by column name or [column selector](ColumnSelectors.md) as [`DataColumn`](DataColumn.md) or null if requested column doesn't exist.

<!---FUN getColumnOrNull-->
<tabs>
<tab title="Properties">

```kotlin
df.getColumnOrNull { age }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.getColumnOrNull(age)
```

</tab>
<tab title="Strings">

```kotlin
df.getColumnOrNull("age")
```

</tab></tabs>
<!---END-->

## getColumnGroup

Return top-level column by column name or [column selector](ColumnSelectors.md) as [`ColumnGroup`](DataColumn.md#columngroup). Throws exception if requested column doesn't exist or is not a `ColumnGroup`.

<!---FUN getColumnGroup-->
<tabs>
<tab title="Properties">

```kotlin
df.getColumnGroup { name }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()

df.getColumnGroup(name)
```

</tab>
<tab title="Strings">

```kotlin
df.getColumnGroup("name")
```

</tab></tabs>
<!---END-->

## getColumns

Return list of selected columns.

<!---FUN getColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.getColumns { age and name }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()

df.getColumns { age and name }
```

</tab>
<tab title="Strings">

```kotlin
df.getColumns("age", "name")
```

</tab></tabs>
<!---END-->
