[//]: # (title: pivot)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Splits the rows of `DataFrame` and groups them horizontally into new columns based on values from one or several columns of original `DataFrame`.

Pass a column to `pivot` function to use its values as grouping keys and names for new columns. To create multi-level column hierarchy, pass several columns to `pivot`:

<!---FUN pivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }
df.pivot { city and name.firstName }
df.pivot { city then name.lastName }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()

df.pivot { city }
df.pivot { city and firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city")
df.pivot { "city" and "name"["firstName"] }
```

</tab></tabs>
<!---END-->

`pivot` returns `PivotedDataFrame` which is an intermediate object that can be configured for further transformation and aggregation of data

By default, pivoted column will be replaced with new columns generated from its values. Instead, you can nest new columns as sub-columns of original column by setting `inward` flag to `true`:

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
* `then` will create column hierarchy based on combinations of column values

## pivot + groupBy

To create matrix table that is expanded both horizontally and vertically, apply `groupBy` function at `PivotedDataFrame` passing the columns to be used for vertical grouping. Reversed order of `pivot` and `groupBy` operations will produce the same result.

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

Combination of `pivot` and `groupBy` operations returns `GroupedPivot` that can be used for further aggregation of data groups within matrix cells. 

`PivotedDataFrame` can be converted to `DataRow` and `GroupedPivot` can be converted to `DataFrame` without any additional transformations. Generated columns will have type `FrameColumn` and will contain data groups (similar to `GroupedDataFrame`)

<!---FUN pivotAsDataRowOrFrame-->

```kotlin
df.pivot { city }.toDataRow()
df.pivot { city }.groupBy { name }.toDataFrame()
```

<!---END-->
