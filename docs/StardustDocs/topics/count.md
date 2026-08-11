[//]: # (title: count)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CountSamples-->

Counts the number of rows.

<!---FUN countDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/countDf.html" width="100%" height="500px"></inline-frame>

<!---FUN count-->

```kotlin
df.count() // the result is 10
```

<!---END-->

Pass a [row condition](DataRow.md#row-conditions) to count only the number of rows that satisfy that condition:

<!---FUN countCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.count { age > 15 } // the result is 8
```

</tab>
<tab title="Strings">

```kotlin
df.count { "age"<Int>() > 15 } // the result is 8
```

</tab></tabs>
<!---END-->

## On a [`GroupBy`](groupBy.md), [`Pivot`](pivot.md), [`PivotGroupBy`](pivot.md#pivot-groupby)

When `count` is used in [`groupBy`](groupBy.md#aggregation), [`pivot`](pivot.md#aggregation), 
or [`pivotGroupBy`](pivot.md#pivot-groupby) aggregations,
it counts rows for every data group:

<!---FUN countGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.count()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").count()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countGroupBy_properties.html" width="100%" height="500px"></inline-frame>


<!---FUN countPivot-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { city }.count { age > 18 }
```

</tab>
<tab title="Strings">

```kotlin
df.pivot("city").count { "age"<Int>() > 18 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countPivot_properties.html" width="100%" height="500px"></inline-frame>

<!---FUN countPivotGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.pivot { name.firstName }.groupBy { name.lastName }.count()
```

</tab>
<tab title="Strings">

```kotlin
df.pivot { "name"["firstName"] }
    .groupBy { "name"["lastName"] }
    .count()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countPivotGroupBy_properties.html" width="100%" height="500px"></inline-frame>


## On a [`DataRow`](DataRow.md)

When called on a [`DataRow`](DataRow.md), returns the number of columns in this [`DataRow`](DataRow.md). 

<!---FUN countDataRow-->

```kotlin
df[0].count() // the result is 5
```

<!---END-->

If a predicate is used, it counts the number of elements in the row that satisfy the given predicate.

<!---FUN countDataRowCondition-->

```kotlin
df[2].count { it == null } // the result is 1
```

<!---END-->

## On a [`DataColumn`](DataColumn.md)

When called on a [`DataColumn`](DataColumn.md), returns the count of elements in the column 
that either match the predicate or the total count of elements if no predicate is provided.

<!---FUN countDataColumn-->

```kotlin
df.age.count() // the result is 10
```

<!---END-->

<!---FUN countDataColumnCondition-->

```kotlin
df.age.count { it > 17 } // the result is 8
```

<!---END-->
