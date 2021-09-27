[//]: # (title: Analyze)

## Basic statistics
### nrow
Returns number of rows in `DataFrame`
```kotlin
df.nrow()
```
### ncol
Returns number of columns in `DataFrame`
```kotlin
df.ncol()
```
### count
Returns the number of rows matching the given predicate
```kotlin
df.count { rowExpression }
```
### sum
Computes sum of expressions evaluated for every `DataRow` in `DataFrame`
```kotlin
df.sum { rowExpression }
```

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
