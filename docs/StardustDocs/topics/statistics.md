[//]: # (title: Statistics)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Analyze-->

To count number of rows that satisfy to [condition](DataRow.md#row-conditions) use `count`:

<!---FUN count-->

```kotlin
df.count { age > 15 }
```

<!---END-->

### Single column statistics

* `sum` and `mean` are available for numeric columns
* `min`, `max` and `median` are available for comparable columns

<!---FUN columnStats-->
<tabs>
<tab title="Properties">

```kotlin
df.sum { weight }
df.min { age }
df.mean { age }
df.median { age }

df.weight.sum()
df.age.max()
df.age.mean()
df.age.median()
```

</tab>
<tab title="Accessors">

```kotlin
val weight by column<Int?>()
val age by column<Int>()

df.sum { weight }
df.min { age }
df.mean { age }
df.median { age }

df.sum(weight)
df.min(age)
df.mean(age)
df.median(age)

df[weight].sum()
df[age].mean()
df[age].min()
df[age].median()
```

</tab>
<tab title="Strings">

```kotlin
df.sum("weight")
df.min("age")
df.mean("age")
df.median("age")
```

</tab></tabs>
<!---END-->

### Multiple columns statistics

When several columns are specified, statistical operations compute a single value across all given columns

<!---FUN multipleColumnsStat-->
<tabs>
<tab title="Properties">

```kotlin
df.min { intCols() }
df.max { name.firstName and name.lastName }
df.sum { age and weight }
df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()

df.min { intCols() }

df.max { firstName and lastName }
// or
df.max(firstName, lastName)

df.sum { age and weight }
// or
df.sum(age, weight)

df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab>
<tab title="Strings">

```kotlin
df.min { intCols() }

df.max { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

df.sum("age", "weight")
// or
df.sum { "age"().asNumbers() and "weight"().asNumbers() }

df.mean { cols(1, 3).asNumbers() }
df.median { name.cols().asComparable() }
```

</tab></tabs>
<!---END-->

To compute statistics separately for every column, use operations with `-for` suffix:

<!---FUN columnsFor-->
<tabs>
<tab title="Properties">

```kotlin
df.minFor { intCols() }
df.maxFor { name.firstName and name.lastName }
df.sumFor { age and weight }
df.meanFor { cols(1, 3).asNumbers() }
df.medianFor { name.cols().asComparable() }
```

</tab>
<tab title="Strings">

```kotlin
df.minFor { intCols() }
df.maxFor { "name"["firstName"].asComparable() and "name"["lastName"].asComparable() }

df.sumFor("age", "weight")
// or
df.sumFor { "age"().asNumbers() and "weight"().asNumbers() }

df.meanFor { cols(1, 3).asNumbers() }
df.medianFor { name.cols().asComparable() }
```

</tab></tabs>
<!---END-->

### Row expression statistics

To compute statistics for some expression evaluated for every row, you should use operations with `-of` suffix:

<!---FUN ofExpressions-->
<tabs>
<tab title="Properties">

```kotlin
df.minOf { 2021 - age }
df.maxOf { name.firstName.length + name.lastName.length }
df.sumOf { weight?.let { it - 50 } }
df.meanOf { Math.log(age.toDouble()) }
df.medianOf { city?.length }
```

</tab>
<tab title="Accessors">

```kotlin
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()
val age by column<Int>()
val weight by column<Int?>()
val city by column<String?>()

df.minOf { 2021 - age() }
df.maxOf { firstName().length + lastName().length }
df.sumOf { weight()?.let { it - 50 } }
df.meanOf { Math.log(age().toDouble()) }
df.medianOf { city()?.length }
```

</tab>
<tab title="Strings">

```kotlin
df.minOf { 2021 - "age"<Int>() }
df.maxOf { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length }
df.sumOf { "weight"<Int?>()?.let { it - 50 } }
df.meanOf { Math.log("age"<Int>().toDouble()) }
df.medianOf { "city"<String?>()?.length }
```

</tab></tabs>
<!---END-->
