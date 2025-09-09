[//]: # (title: join)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.multiple.JoinSamples-->

Joins two [`DataFrame`](DataFrame.md) object by join columns.

```kotlin
join(otherDf, type = JoinType.Inner) [ { joinColumns } ]

joinColumns: JoinDsl.(LeftDataFrame) -> Columns

interface JoinDsl: LeftDataFrame {
    
    val right: RightDataFrame
    
    fun DataColumn.match(rightColumn: DataColumn)
}
```

`joinColumns` is a [column selector](ColumnSelectors.md) that defines column mapping for join:

Related operations: [](multipleDataFrames.md)

## Examples

<!---FUN notebook_test_join_3-->

```kotlin
dfAges
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_3.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_5-->

```kotlin
dfCities
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_5.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_6-->

```kotlin
dfAges.join(dfCities) { firstName match right.name }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_6.html" width="100%" height="500px"></inline-frame>

If mapped columns have the same name, just select join columns from the left [`DataFrame`](DataFrame.md):

<!---FUN notebook_test_join_8-->

```kotlin
dfLeft
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_8.html" width="100%" height="500px"></inline-frame>


<!---FUN notebook_test_join_10-->

```kotlin
dfRight
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_10.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_11-->

```kotlin
dfLeft.join(dfRight) { name }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_11.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_12-->

If `joinColumns` is not specified, columns with the same name from both [`DataFrame`](DataFrame.md) 
objects will be used as join columns:

```kotlin
dfLeft.join(dfRight)
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_12.html" width="100%" height="500px"></inline-frame>


## Join types

Supported join types:
* `Inner` (default) — only matched rows from left and right [`DataFrame`](DataFrame.md) objects
* `Filter` — only matched rows from left [`DataFrame`](DataFrame.md)
* `Left` — all rows from left [`DataFrame`](DataFrame.md), mismatches from right [`DataFrame`](DataFrame.md) filled with `null`
* `Right` — all rows from right [`DataFrame`](DataFrame.md), mismatches from left [`DataFrame`](DataFrame.md) filled with `null`
* `Full` — all rows from left and right [`DataFrame`](DataFrame.md) objects, any mismatches filled with `null`
* `Exclude` — only mismatched rows from left [`DataFrame`](DataFrame.md)

For every join type there is a shortcut operation:

```kotlin
df.innerJoin(otherDf) [ { joinColumns } ]
df.filterJoin(otherDf) [ { joinColumns } ]
df.leftJoin(otherDf) [ { joinColumns } ]
df.rightJoin(otherDf) [ { joinColumns } ]
df.fullJoin(otherDf) [ { joinColumns } ]
df.excludeJoin(otherDf) [ { joinColumns } ]
```


### Examples {id="examples_1"}

<!---FUN notebook_test_join_13-->

```kotlin
dfLeft
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_13.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_14-->

```kotlin
dfRight
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_14.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_15-->

```kotlin
dfLeft.innerJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_15.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_16-->

```kotlin
dfLeft.filterJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_16.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_17-->

```kotlin
dfLeft.leftJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_17.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_18-->

```kotlin
dfLeft.rightJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_18.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_19-->

```kotlin
dfLeft.fullJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_19.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_20-->

```kotlin
dfLeft.excludeJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_20.html" width="100%" height="500px"></inline-frame>

