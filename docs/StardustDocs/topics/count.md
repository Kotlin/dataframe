[//]: # (title: count)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

Counts the number of rows.

<!---FUN count-->

```kotlin
df.count()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Analyze.count.html"/>
<!---END-->

Pass [row condition](DataRow.md#row-conditions) to count number of rows that satisfy to that condition:

<!---FUN countCondition-->

```kotlin
df.count { age > 15 }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Analyze.countCondition.html"/>
<!---END-->

When `count` is used in [`groupBy`](groupBy.md#aggregation) or [`pivot`](pivot.md#aggregation) aggregations, it counts rows for every data group:

<!---FUN countAggregation-->

```kotlin
df.groupBy { city }.count()
df.pivot { city }.count { age > 18 }
df.pivot { name.firstName }.groupBy { name.lastName }.count()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Analyze.countAggregation.html"/>
<!---END-->
