[//]: # (title: Aggregate GroupBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

To compute one or several [statistics](statistics.md) per every group of `GroupedDataFrame` use `aggregate` function. Its body will be executed for every data group and has a receiver of type `DataFrame` that represents current data group being aggregated.
To add new column to the resulting `DataFrame`, pass the name of new column to infix function `into`:

<!---FUN groupByAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate {
    nrow() into "total"
    count { age > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age }.name into "oldest"
}
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val name by columnGroup()

df.groupBy { city }.aggregate {
    nrow() into "total"
    count { age() > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age() }[name] into "name of oldest"
}
// or
df.groupBy(city).aggregate {
    nrow() into "total"
    count { age > 18 } into "adults"
    median(age) into "median age"
    min(age) into "min age"
    maxBy(age)[name] into "name of oldest"
}
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate {
    nrow() into "total"
    count { "age"<Int>() > 18 } into "adults"
    median("age") into "median age"
    min("age") into "min age"
    maxBy("age")["name"] into "oldest"
}
```

</tab></tabs>
<!---END-->

If only one aggregation function is used, column name can be omitted:

<!---FUN groupByAggregateWithoutInto-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.aggregate { maxBy { age }.name }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val name by columnGroup()

df.groupBy { city }.aggregate { maxBy { age() }[name] }
// or
df.groupBy(city).aggregate { maxBy(age)[name] }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").aggregate { maxBy("age")["name"] }
```

</tab></tabs>
<!---END-->

Most common aggregation functions can be computed directly at `GroupedDataFrame`:

<!---FUN groupByDirectAggregations-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.max() // max for every comparable column
df.groupBy { city }.mean() // mean for every numeric column
df.groupBy { city }.max { age } // max age into column "age"
df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
df.groupBy { city }.count() // number of rows into column "count"
df.groupBy { city }
    .max { name.firstName.length() and name.lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }
    .medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }
    .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy { city }.meanOf("mean ratio") { weight?.div(age) } // mean of weight/age into column "mean ratio"
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

df.groupBy { city }.max() // max for every comparable column
df.groupBy { city }.mean() // mean for every numeric column
df.groupBy { city }.max { age } // max age into column "age"
df.groupBy { city }.sum("total weight") { weight } // sum of weights into column "total weight"
df.groupBy { city }.count() // number of rows into column "count"
df.groupBy { city }
    .max { firstName.length() and lastName.length() } // maximum length of firstName or lastName into column "max"
df.groupBy { city }
    .medianFor { age and weight } // median age into column "age", median weight into column "weight"
df.groupBy { city }
    .minFor { (age into "min age") and (weight into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy { city }.meanOf("mean ratio") { weight()?.div(age()) } // mean of weight/age into column "mean ratio"
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").max() // max for every comparable column
df.groupBy("city").mean() // mean for every numeric column
df.groupBy("city").max("age") // max age into column "age"
df.groupBy("city").sum("weight", name = "total weight") // sum of weights into column "total weight"
df.groupBy("city").count() // number of rows into column "count"
df.groupBy("city").max {
    "name"["firstName"].strings().length() and "name"["lastName"].strings().length()
} // maximum length of firstName or lastName into column "max"
df.groupBy("city")
    .medianFor("age", "weight") // median age into column "age", median weight into column "weight"
df.groupBy("city")
    .minFor { ("age".ints() into "min age") and ("weight".intOrNulls() into "min weight") } // min age into column "min age", min weight into column "min weight"
df.groupBy("city").meanOf("mean ratio") {
    "weight".intOrNull()?.div("age".int())
} // mean of weight/age into column "mean ratio"
```

</tab></tabs>
<!---END-->

To get all column values for every group without aggregation use `values` function:
* for [ValueColumn](DataColumn.md#valuecolumn) of type `T` it will gather group values into lists of type `Many<T>`
* for [ColumnGroup](DataColumn.md#columngroup) it will gather group values into `DataFrame` and convert [ColumnGroup](DataColumn.md#columngroup) into [FrameColumn](DataColumn.md#framecolumn)

<!---FUN groupByWithoutAggregation-->
<tabs>
<tab title="Properties">

```kotlin
df.groupBy { city }.values()
df.groupBy { city }.values { name and age }
df.groupBy { city }.values { weight into "weights" }
```

</tab>
<tab title="Accessors">

```kotlin
val city by column<String?>()
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()

df.groupBy(city).values()
df.groupBy(city).values(name, age)
df.groupBy(city).values { weight into "weights" }
```

</tab>
<tab title="Strings">

```kotlin
df.groupBy("city").values()
df.groupBy("city").values("name", "age")
df.groupBy("city").values { "weight" into "weights" }
```

</tab></tabs>
<!---END-->
