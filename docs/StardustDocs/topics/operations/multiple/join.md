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

### Examples

<!---FUN notebook_test_join_3-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_3.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_5-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_5.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_6-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_6.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_8-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_8.html" width="100%" height="500px"></inline-frame>


<!---FUN notebook_test_join_10-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_10.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_11-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_11.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_12-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_12.html" width="100%" height="500px"></inline-frame>


If mapped columns have the same name, just select join columns from the left [`DataFrame`](DataFrame.md):

If `joinColumns` is not specified, columns with the same name from both [`DataFrame`](DataFrame.md) objects will be used as join columns:


### Join types

Supported join types:
* `Inner` (default) — only matched rows from left and right [`DataFrame`](DataFrame.md) objects
* `Filter` — only matched rows from left [`DataFrame`](DataFrame.md)
* `Left` — all rows from left [`DataFrame`](DataFrame.md), mismatches from right [`DataFrame`](DataFrame.md) filled with `null`
* `Right` — all rows from right [`DataFrame`](DataFrame.md), mismatches from left [`DataFrame`](DataFrame.md) filled with `null`
* `Full` — all rows from left and right [`DataFrame`](DataFrame.md) objects, any mismatches filled with `null`
* `Exclude` — only mismatched rows from left [`DataFrame`](DataFrame.md)

For every join type there is a shortcut operation:

```kotlin
df.innerJoin(other) { name and city }
df.leftJoin(other) { name and city }
df.rightJoin(other) { name and city }
df.fullJoin(other) { name and city }
df.excludeJoin(other) { name and city }
```

<!---FUN notebook_test_join_13-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_13.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_14-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_14.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_15-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_15.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_16-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_16.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_17-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_17.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_18-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_18.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_19-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_19.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_join_20-->

<!---END-->

<inline-frame src="./resources/notebook_test_join_20.html" width="100%" height="500px"></inline-frame>

