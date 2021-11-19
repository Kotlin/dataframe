[//]: # (title: pivot)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Splits the rows of `DataFrame` and groups them horizontally into new columns based on values from one or several columns of original `DataFrame`.

Pass a column to `pivot` function to use its values as grouping keys and names for new columns.

<!---FUN pivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()

df.pivot { city }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city")
```

</tab></tabs>
<!---END-->

Returns `Pivot`: an intermediate object that can be configured for further transformation and aggregation of data.

See [pivot aggregations](aggregatePivot.md)

By default, pivoted column will be replaced with new columns generated from its values. Instead, you can nest new columns as sub-columns of original column using `inward` flag:

<!---FUN pivotInward-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot(inward = true) { city }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()

df.pivot(inward = true) { city }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city", inward = true)
```

</tab></tabs>
<!---END-->

To pivot several columns in one operation you can combine them using `and` or `then` infix function:
* `and` will pivot columns independently
* `then` will create column hierarchy based on possible combinations of column values

<!---FUN pivot2-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city and name.firstName }
df.pivot { city then name.firstName }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()

df.pivot { city and firstName }
df.pivot { city then firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot { "city" and "name"["firstName"] }
df.pivot { "city" then "name"["firstName"] }
```

</tab></tabs>
<!---END-->

## pivot + groupBy

To create matrix table that is expanded both horizontally and vertically, apply `groupBy` function at `Pivot` passing the columns for vertical grouping. Reversed order of `pivot` and `groupBy` operations will produce the same result.

<!---FUN pivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name }
// same as
df.groupBy { name }.pivot { city }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()

df.pivot { city }.groupBy { name }
// same as
df.groupBy { name }.pivot { city }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name")
// same as
df.groupBy("name").pivot("city")
```

</tab></tabs>
<!---END-->

Combination of `pivot` and `groupBy` operations returns `PivotGroupBy` that can be used for further aggregation of data groups within matrix cells. 

See [pivot aggregations](aggregatePivot.md)

To group by all columns except pivoted use `groupByOther`:

<!---FUN pivotGroupByOther-->

```kotlin
df.pivot { city }.groupByOther()
```

<!---END-->

Pivot operation can be performed without any data aggregation:
* `Pivot` object can be converted to `DataRow` or `DataFrame`.
* `GroupedPivot` object can be converted to `DataFrame`.

Generated columns will have type [`FrameColumn`](DataColumn.md#framecolumn) and will contain data groups.

<!---FUN pivotAsDataRowOrFrame-->

```kotlin
df.pivot { city }.toDataRow()
df.pivot { city }.groupBy { name }.toDataFrame()
```

<!---END-->

## pivotCount

Pivots with `Int` count statistics one or several columns preserving all other columns of `DataFrame`.

<!---FUN pivotCount-->

```kotlin
df.pivotCount { city }
// same as
df.pivot(inward = true) { city }.groupByOther().count()
```

<!---END-->

## pivotMatches

Pivots with `Boolean` statistics one or several columns preserving all other columns of `DataFrame`.

<!---FUN pivotMatches-->

```kotlin
df.pivotMatches { city }
// same as
df.pivot(inward = true) { city }.groupByOther().matches()
```

<!---END-->
