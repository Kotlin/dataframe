[//]: # (title: Combine several DataFrames)

# Merge dataframes
## Add columns
Adds columns from another dataframe. New columns must have the same length as original columns
```
df.add(otherDf)
df.add(otherDf.columns())
df + otherDf.columns()
```
## union
Adds rows from another dataframe. Columns from both dataframes are unioned, values in missing columns are replaced with `null`
```
df.union(otherDf)
df + otherDf
```
**Input**

name | age
---|---
Alice | 15
Bob | 20

name | weight
---|---
Mark |60
Bob |70

```kotlin
df1 + df2
```
**Output**

name|age|weight
---|---|---
Alice | 15 | null
Bob | 20 | null
Mark | null | 60
Bob | null |70

## join
SQL-like joins. Matches rows from two dataframes by key columns and creates cross-product of other columns
```
df.innerJoin(otherDf) { columnMatches }
df.leftJoin(otherDf) { columnMatches }
df.rightJoin(otherDf) { columnMatches }
df.outerJoin(otherDf) { columnMatches }
df.filterJoin(otherDf) { columnMatches }
df.excludeJoin(otherDf) { columnMatches }

df.join(otherDf) { columnMatches } // same as innerJoin
```
To match columns with different names use `match` operation and `right` property to reference second `DataFrame`:
```kotlin
val df1 = dataFrameOf("name", "origin")("Alice", "London", "Bob", "Milan")
val df2 = dataFrameOf("city", "country")("London", "UK", "Milan", "Italy") 
                    
df1.join(df2) { origin.match(right.city) }
df1.join(df2) { origin match right.city } // infix form
```
To match columns with equal names just use column from the first `DataFrame`
```kotlin
df1.join(df2) { city }
df1.join(df2) { firstName and lastName }
```
If `columnMatches` is ommited, all columns with matching names from both dataframes will be used

```
df1
```

name | age
---|---
Alice | 15
Bob | 20

```
df2
```

name | weight
---|---
Mark |60
Bob |70

```kotlin
df1.join(df2)
```

name|age|weight
---|---|---
Bob | 20 | 70

```kotlin
df1.leftJoin(df2)
```

name|age|weight
---|---|---
Alice | 15 | null
Bob | 20 | 70

```kotlin
df1.rightJoin(df2)
```

name|age|weight
---|---|---
Bob | 20 | 70
Mark | null | 60

```kotlin
df1.outerJoin(df2)
```

name|age|weight
---|---|---
Alice | 15 | null
Bob | 20 | 70
Mark | null | 60

```kotlin
df1.filterJoin(df2)
```

name|age
---|---
Bob | 20

```kotlin
df1.excludeJoin(df2)
```

name|age
---|---
Alice | 15 
