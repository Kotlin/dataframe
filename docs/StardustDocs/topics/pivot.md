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

See [pivot aggregations](#aggregation)

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

See [pivot aggregations](#aggregation)

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

## Aggregation


To aggregate data groups in [`Pivot`](pivot.md) or [`PivotGroupBy`](pivot.md#pivot-groupby) with one or several statistics use `aggregate`:

<!---FUN pivotAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.aggregate { minBy { age }.name }
df.pivot { city }.groupBy { name.firstName }.aggregate {
    meanFor { age and weight } into "means"
    stdFor { age and weight } into "stds"
    maxByOrNull { weight }?.name?.lastName into "biggest"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.aggregate { minBy(age).name }

df.pivot { city }.groupBy { firstName }.aggregate {
    meanFor { age and weight } into "means"
    stdFor { age and weight } into "stds"
    maxByOrNull(weight)?.name?.lastName into "biggest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").aggregate { minBy("age")["name"] }

df.pivot("city").groupBy { "name"["firstName"] }.aggregate {
    meanFor("age", "weight") into "means"
    stdFor("age", "weight") into "stds"
    maxByOrNull("weight")?.getColumnGroup("name")?.get("lastName") into "biggest"
}
```

</tab></tabs>
<!---END-->

Shortcuts for common aggregation functions are also available:

<!---FUN pivotCommonAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.maxFor { age and weight }
df.groupBy { name }.pivot { city }.median { age }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.maxFor { age and weight }
df.groupBy { name }.pivot { city }.median { age }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").maxFor("age", "weight")
df.groupBy("name").pivot("city").median("age")
```

</tab></tabs>
<!---END-->

By default, when aggregation function produces several values for data group, column hierarchy in resulting `DataFrame` will be indexed first by pivot keys and then by the names of aggregated values.
To reverse this order so that resulting columns will be indexed first by names of aggregated values and then by pivot keys, use `separate=true` flag that is available in multi-result aggregation operations, such as `aggregate` or `<stat>For`:

<!---FUN pivotSeparate-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.maxFor(separate = true) { age and weight }
df.pivot { city }.aggregate(separate = true) {
    min { age } into "min age"
    maxOrNull { weight } into "max weight"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()

df.pivot { city }.maxFor(separate = true) { age and weight }
df.pivot { city }.aggregate(separate = true) {
    min { age } into "min age"
    maxOrNull { weight } into "max weight"
}
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").maxFor("age", "weight", separate = true)
df.pivot("city").aggregate(separate = true) {
    min("age") into "min age"
    maxOrNull("weight") into "max weight"
}
```

</tab></tabs>
<!---END-->

By default, any aggregation function will result in `null` value for those matrix cells, where intersection of column and row keys produced an empty data group.
You can specify default value for any aggregation by `default` infix function. This value will replace all `null` results of aggregation function over non-empty data groups as well.
To use one default value for all aggregation functions, use `default()` before aggregation.

<!---FUN pivotDefault-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
df.pivot { city }.groupBy { name }.aggregate {
    median { age } into "median age" default 0
    minOrNull { weight } into "min weight" default 100
}
df.pivot { city }.groupBy { name }.default(0).min()
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()

df.pivot { city }.groupBy { name }.aggregate { min { age } default 0 }
df.pivot { city }.groupBy { name }.aggregate {
    median { age } into "median age" default 0
    minOrNull { weight } into "min weight" default 100
}
df.pivot { city }.groupBy { name }.default(0).min()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").groupBy("name").aggregate { min("age") default 0 }
df.pivot("city").groupBy("name").aggregate {
    median("age") into "median age" default 0
    minOrNull("weight") into "min weight" default 100
}
df.pivot("city").groupBy("name").default(0).min()
```

</tab></tabs>
<!---END-->

### Pivot inside aggregate

[pivot](pivot.md) operation can also be used inside `aggregate` body of `GroupBy`. This allows to combine column pivoting with other aggregation functions:

<!---FUN pivotInAggregate-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { name.firstName }.aggregate {
    pivot { city }.aggregate(separate = true) {
        mean { age } into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val name by columnGroup()
val firstName by name.column<String>()
val age by column<Int>()

df.groupBy { firstName }.aggregate {
    pivot { city }.aggregate(separate = true) {
        mean { age } into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy { "name"["firstName"] }.aggregate {
    pivot("city").aggregate(separate = true) {
        mean("age") into "mean age"
        count() into "count"
    }
    count() into "total"
}
```

</tab></tabs>
<!---END-->

### pivotCount

Pivots with `Int` count statistics one or several columns preserving all other columns of `DataFrame`.

<!---FUN pivotCount-->

```kotlin
df.pivotCount { city }
// same as
df.pivot(inward = true) { city }.groupByOther().count()
```

<!---END-->

### pivotMatches

Pivots with `Boolean` statistics one or several columns preserving all other columns of `DataFrame`.

<!---FUN pivotMatches-->

```kotlin
df.pivotMatches { city }
// same as
df.pivot(inward = true) { city }.groupByOther().matches()
```

<!---END-->
