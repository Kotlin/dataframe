[//]: # (title: count)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Counts the number of rows.

<!---FUN count-->

```kotlin
df.count()
```

<!---END-->

Pass [row condition](DataRow.md#row-conditions) to count number of rows that satisfy to that condition:

<!---FUN countCondition-->

```kotlin
df.count { age > 15 }
```

<!---END-->

When `count` is used in [`groupBy`](aggregateGroupBy.md) or [`pivot`](aggregatePivot.md) aggregations, it counts rows for every data group:

<!---FUN countAggregation-->

```kotlin
df.groupBy { city }.count()
df.pivot { city }.count { age > 18 }
df.pivot { name.firstName }.groupBy { name.lastName }.count()
```

<!---END-->
