[//]: # (title: Analyze)

<!---IMPORT docs.api.Analyze-->

## Basic statistics

<!---FUN basicInfo-->

```kotlin
df.nrow() // number of rows
df.ncol() // number of columns
df.schema() // schema of columns
```

<!---END-->

To count number of rows that satisfy to [condition](rows.md#row-conditions) use `count`:

<!---FUN count-->

```kotlin
df.count { age > 15 }
```

<!---END-->

### Single column statistics

Sum of values in number column

<!---FUN columnSum-->
<tabs>
<tab title="Properties">

```kotlin

```

</tab>
<tab title="Accessors">

```kotlin

```

</tab>
<tab title="Strings">

```kotlin

```

</tab></tabs>
<!---END-->

Min/max value in comparable column

<!---FUN columnMinMax-->
<tabs>
<tab title="Properties">

```kotlin
df.min { age }
df.age.min()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.min(age)
df.min { age }
df[age].min()
```

</tab>
<tab title="Strings">

```kotlin
df.min("age")
df["age"].asComparable().min()
df.getColumn<Int>("age").min()
```

</tab></tabs>
<!---END-->

Mean value in number column

<!---FUN columnMean-->
<tabs>
<tab title="Properties">

```kotlin
df.mean { age }
df.age.mean()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.mean(age)
df.mean { age }
df[age].mean()
```

</tab>
<tab title="Strings">

```kotlin
df.mean("age")
df["age"].asNumbers().mean()
df.getColumn<Int>("age").mean()
```

</tab></tabs>
<!---END-->

Median value in comparable column

<!---FUN columnMedian-->
<tabs>
<tab title="Properties">

```kotlin
df.median { age }
df.age.median()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.median(age)
df.median { age }
df[age].median()
```

</tab>
<tab title="Strings">

```kotlin
df.median("age")
df["age"].asComparable().median()
df.getColumn<Int>("age").median()
```

</tab></tabs>
<!---END-->

### Several columns statistics

When several columns are specified, statistical operations compute single value across given columns  

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

If you want to aggregate statistics separately for every column, use operations with `-for` suffix:

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

If you want to compute statistics for some expression evaluated for every row, you should use operations with `-of` suffix:

<!---FUN ofExpressions-->
<tabs>
<tab title="Properties">

```kotlin
df.minOf { 2021 - age }
df.maxOf { name.firstName.length + name.lastName.length }
df.sumOf { weight?.let { it - 50} }
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

df.minOf { 2021 - age }
df.maxOf { firstName().length + lastName().length }
df.sumOf { weight()?.let { it - 50} }
df.meanOf { Math.log(age().toDouble()) }
df.medianOf { city()?.length }
```

</tab>
<tab title="Strings">

```kotlin
df.minOf { 2021 - "age"<Int>() }
df.maxOf { "name"["firstName"]<String>().length + "name"["lastName"]<String>().length }
df.sumOf { "weight"<Int?>()?.let { it - 50} }
df.meanOf { Math.log("age"<Int>().toDouble()) }
df.medianOf { "city"<String?>()?.length }
```

</tab></tabs>
<!---END-->

## GroupBy

## Pivot

## Aggregation

`GroupedDataFrame` is any `DataFrame` with one selected [`FrameColumn`](columns.md#framecolumn) that is interpreted as data groups
So any `DataFrame` with `FrameColumn` can be converted to `GroupedDataFrame`:
```kotlin
val files by column("input1.csv", "input2.csv", "input3.csv") // create column of file names 
val data by files.map { DataFrame.read(it) } // create FrameColumn of dataframes
val df = DataFrame.of(files, data) // create DataFrame with two columns 'files' and 'data'
val groupedDf = df.asGrouped { data } // interpret 'data' column as groups of GroupedDataFrame
```

[Union](mix.md#union) operation all groups of `GroupedDataFrame` into single `DataFrame`. All other columns of `GroupedDataFrame` are ignored.
```kotlin
groupedDf.union()
```
`union` operation at `FrameColumn` will produce the same result:
```kotlin
groupedDf[data].union()
```
### aggregate
`GroupedDataFrame` can be aggregated into `DataFrame` with one or several [statistics](#statistics) computed for every data group.
```
groupedDf.aggregate { 
    stat1 into "column1"
    stat2 into "column2"
    ...
}
```
Every data group is passed to the body of `aggregate` function as a receiver of type `DataFrame`
```kotlin
groupedDf.aggregate {
    nrow() into "total"
    count { age > 18 } into "adults"
    median { age } into "median age"
    min { age } into "min age"
    maxBy { age }.name into "oldest"
}
```
If only one simple statistics is used, `aggregate` can be omitted:
```kotlin
groupedDf.max { age } // max age for every group into column "age"
groupedDf.mean { weight } // mean weight in every group into column "weight"
groupedDf.count() // number of rows in every group into column "n"
```
`aggregate` can also be applied to any `DataFrame` with [FrameColumn](columns.md#framecolumn)
```
df.aggregate { groups }.with {
    stat1 into "column1"
    stat2 into "column2"
    ...
}
```
### `pivot` inside `aggregate`
[pivot](#pivot) operation can also be used within [aggregate](#aggregate) with a slightly different syntax

## Statistics

## Working with `GroupedDataFrame`

**Input**

name|city|date
---|---|---
Alice|London|2020-10-01
Bob|Paris|2020-10-02
Alice|Paris|2020-10-03
Alice|London|2020-10-04
Bob|Milan|2020-10-05

```kotlin
df.groupBy { name }.aggregate {
    pivot { city }.max { date }
}
```
or
```kotlin
df.groupBy { name }.pivot { city }.max { date }
```
**Output**

name|London last visit|Paris last visit|Milan last visit
---|---|---|---
Alice|2020-10-04|2020-10-03|null
Bob|null|2020-10-02|2020-10-05

**Input**

name|city|date
---|---|---
Alice|London|2020-10-01
Bob|Paris|2020-10-02
Alice|Paris|2020-10-03
Alice|London|2020-10-04
Bob|Milan|2020-10-05

```kotlin
df.groupBy { name }.aggregate {
    countBy { city } into { it }
}
```
or
```kotlin
df.groupBy { name }.countBy { city }
```

**Output**

name|London|Paris|Milan
---|---|---|---
Alice|2|1|0
Bob|0|1|1
