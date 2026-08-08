[//]: # (title: count)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Counts the number of rows.

<!---FUN count-->

```kotlin
df.count()
```

<!---END-->

Pass a [row condition](DataRow.md#row-conditions) to count only the number of rows that satisfy that condition:

<!---FUN countCondition-->

```kotlin
df.count { age > 15 }
```

<!---END-->

When `count` is used in [`groupBy`](groupBy.md#aggregation) or [`pivot`](pivot.md#aggregation) aggregations,
it counts rows for every data group:

<!---FUN countAggregation-->

```kotlin
df.groupBy { city }.count()
df.pivot { city }.count { age > 18 }
df.pivot { name.firstName }.groupBy { name.lastName }.count()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Analyze.countAggregation.html" width="100%"/>
<!---END-->

When called on a [`DataRow`](DataRow.md), returns the number of columns in this [`DataRow`](DataRow.md). If a predicate 
is used, it counts the number of elements in the row that satisfy the given predicate.
