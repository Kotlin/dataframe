[//]: # (title: countDistinct)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CountDistinctSamples-->


Counts distinct rows or distinct combinations of values in selected columns.

When `countDistinct` is used on a [`DataFrame`](DataFrame.md), 
it returns the number of distinct rows in this [`DataFrame`](DataFrame.md).

<!---FUN countDistinctDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/countDistinctDf.html" width="100%" height="500px"></inline-frame>

<!---FUN countDistinct-->

```kotlin
df.countDistinct() // the result is 4
```

<!---END-->

You can also specify which columns to use when counting distinct combinations of values.

<inline-frame src="./resources/countDistinctColumnsDf.html" width="100%" height="500px"></inline-frame>

<!---FUN countDistinctColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.countDistinct { name.firstName and city } // the result is 3
```

</tab>
<tab title="Strings">

```kotlin
df.countDistinct { "name"["firstName"] and "city" } // the result is 3
```

</tab></tabs>
<!---END-->

When `countDistinct` is used on a `GroupBy`, it counts distinct rows within each group.
That is, this function returns a [`DataFrame`](DataFrame.md) where each row corresponds to a group 
from the original `GroupBy`. The result contains the original group key columns 
and a new column with the number of distinct rows (or combinations of values in selected columns) in each group.

Let's take this `GroupBy` as an example:

<!---FUN countDistinctGroupBy-->

```kotlin
df.groupBy { city }
```

<!---END-->
<inline-frame src="./resources/countDistinctGroupBy.html" width="100%" height="500px"></inline-frame>

Applying `countDistinct` to this `GroupBy` yields the following result:

<!---FUN countDistinctOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.countDistinct()
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").countDistinct()
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countDistinctOnGroupBySmallTable_properties.html" width="100%" height="500px"></inline-frame>

You can also specify which columns in the groups should be used to determine distinctness.

<inline-frame src="./resources/countDistinctColumnsGroupBy.html" width="100%" height="500px"></inline-frame>

<!---FUN countDistinctColumnsOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.countDistinct { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").countDistinct { "name"["firstName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countDistinctColumnsOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>

The default name of the new column is `countDistinct`, but you can choose a different one.

<!---FUN countDistinctColumnsCustomNameOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.countDistinct("uniqueFirstNames") { name.firstName }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").countDistinct("uniqueFirstNames") { "name"["firstName"] }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/countDistinctColumnsCustomNameOnGroupBy_properties.html" width="100%" height="500px"></inline-frame>
