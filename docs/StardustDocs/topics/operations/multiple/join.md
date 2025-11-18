[//]: # (title: join)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.multiple.JoinSamples-->

Joins two [`DataFrame`](DataFrame.md) objects by join columns.

A *join* creates a new dataframe by combining rows from two input dataframes according to one or more key columns.  
Rows are merged when the values in the join columns match.  
If there is no match, whether the row is included and how missing values are filled depends on the type of join (e.g., inner, left, right, full).

Returns a new [`DataFrame`](DataFrame.md) that contains the merged rows and columns from both inputs.

```kotlin
join(otherDf, type = JoinType.Inner) [ { joinColumns } ]

joinColumns: JoinDsl.(LeftDataFrame) -> Columns

interface JoinDsl: LeftDataFrame {
    
    val right: RightDataFrame
    
    fun DataColumn.match(rightColumn: DataColumn)
}
```

`joinColumns` is a special case of [columns selector](ColumnSelectors.md) that defines column mapping for join.

Related operations: [](multipleDataFrames.md)

## Examples

### Join with explicit keys (with different names) {collapsible="true"}

Use the Join DSL when the key column names differ:

- access the right `DataFrame` via `right`;
- define the join condition with **`match`**.

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
// INNER JOIN on differently named keys:
// Merge a row when dfAges.firstName == dfCities.name.
// With the given data all 3 names match → all rows merge.
dfAges.join(dfCities) { firstName match right.name }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_6.html" width="100%" height="500px"></inline-frame>

### Join with explicit keys (with the same names) {collapsible="true"}

If mapped columns have the same name, just select join columns (one or several) from the left [`DataFrame`](DataFrame.md):

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
// INNER JOIN on "name" only:
// Merge when left.name == right.name.
// Duplicate keys produce multiple merged rows (one per pairing).
dfLeft.join(dfRight) { name }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_11.html" width="100%" height="500px"></inline-frame>

> In this example, the "city" columns from the left and right dataframes do not match to each other.
> After joining, the "city" column from the right dataframe is included in the result dataframe
> with the name **"city1"** to avoid a name conflict.  
> { style = "note" }

### Join with implicit keys (all columns with the same name) {collapsible="true"}

If `joinColumns` is not specified, columns with the same name from both [`DataFrame`](DataFrame.md)
objects will be used as join columns:

<!---FUN dfLeftImplicit-->

```kotlin
dfLeft
```

<!---END-->

<inline-frame src="./resources/dfLeftImplicit.html" width="100%" height="500px"></inline-frame>


<!---FUN dfRightImplicit-->

```kotlin
dfRight
```

<!---END-->

<inline-frame src="./resources/dfRightImplicit.html" width="100%" height="500px"></inline-frame>


<!---FUN notebook_test_join_12-->

```kotlin
// INNER JOIN on all same-named columns ("name" and "city"):
// Merge when BOTH name AND city are equal; otherwise the row is dropped.
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

#### Inner {collapsible="true"}

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
// INNER JOIN:
// Combines columns from the left and right dataframes
// and keep only rows where (name, city) matches on both sides.
dfLeft.innerJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_15.html" width="100%" height="500px"></inline-frame>

#### Filter {collapsible="true"}

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


<!---FUN notebook_test_join_16-->

```kotlin
// FILTER JOIN:
// Keep ONLY left rows that have ANY match on (name, city).
// No right-side columns are added.
dfLeft.filterJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_16.html" width="100%" height="500px"></inline-frame>

#### Left {collapsible="true"}

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


<!---FUN notebook_test_join_17-->

```kotlin
// LEFT JOIN:
// Keep ALL left rows and add columns from the right dataframe.
// If (name, city) matches, attach right columns values from
// the corresponding row in the right dataframe;
// if not (e.g. ("Bob", "Dubai") row), fill them with `null`.
dfLeft.leftJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_17.html" width="100%" height="500px"></inline-frame>

#### Right {collapsible="true"}

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


<!---FUN notebook_test_join_18-->

```kotlin
// RIGHT JOIN:
// Keep ALL right rows and add columns from the left dataframe.
// If (name, city) matches, attach left columns values from
// the corresponding row in the left dataframe;
// if not (e.g. ("Bob", "Tokyo") row), fill them with `null`.
dfLeft.rightJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_18.html" width="100%" height="500px"></inline-frame>

#### Full {collapsible="true"}

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


<!---FUN notebook_test_join_19-->

```kotlin
// FULL JOIN:
// Keep ALL rows from both sides. Where there's no match on (name, city),
// the other side is filled with nulls.
dfLeft.fullJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_19.html" width="100%" height="500px"></inline-frame>

#### Exclude {collapsible="true"}

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


<!---FUN notebook_test_join_20-->

```kotlin
// EXCLUDE JOIN:
// Keep ONLY left rows that have NO match on (name, city).
// Useful to find "unpaired" left rows.
dfLeft.excludeJoin(dfRight) { name and city }
```

<!---END-->

<inline-frame src="./resources/notebook_test_join_20.html" width="100%" height="500px"></inline-frame>

