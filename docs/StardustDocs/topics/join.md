[//]: # (title: join)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Join-->

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

<!---FUN joinWithMatch-->
<tabs>
<tab title="Properties">

```kotlin
df.join(other) { name match right.fullName }
```

</tab>
<tab title="Strings">

```kotlin
df.join(other) { "name" match "fullName" }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Join.joinWithMatch.html" width="100%"/>
<!---END-->

If mapped columns have the same name, just select join columns from the left [`DataFrame`](DataFrame.md): 

<!---FUN join-->
<tabs>
<tab title="Properties">

```kotlin
df.join(other) { name and city }
```

</tab>
<tab title="Strings">

```kotlin
df.join(other, "name", "city")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Join.join.html" width="100%"/>
<!---END-->

If `joinColumns` is not specified, columns with the same name from both [`DataFrame`](DataFrame.md) objects will be used as join columns:

<!---FUN joinDefault-->

```kotlin
df.join(other)
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Join.joinDefault.html" width="100%"/>
<!---END-->

### Join types

Supported join types:
* `Inner` (default) — only matched rows from left and right [`DataFrame`](DataFrame.md) objects
* `Filter` — only matched rows from left [`DataFrame`](DataFrame.md)
* `Left` — all rows from left [`DataFrame`](DataFrame.md), mismatches from right [`DataFrame`](DataFrame.md) filled with `null`
* `Right` — all rows from right [`DataFrame`](DataFrame.md), mismatches from left [`DataFrame`](DataFrame.md) filled with `null`
* `Full` — all rows from left and right [`DataFrame`](DataFrame.md) objects, any mismatches filled with `null`
* `Exclude` — only mismatched rows from left [`DataFrame`](DataFrame.md)

For every join type there is a shortcut operation:

<!---FUN joinSpecial-->
<tabs>
<tab title="Properties">

```kotlin
df.innerJoin(other) { name and city }
df.leftJoin(other) { name and city }
df.rightJoin(other) { name and city }
df.fullJoin(other) { name and city }
df.excludeJoin(other) { name and city }
```

</tab>
<tab title="Strings">

```kotlin
df.innerJoin(other, "name", "city")
df.leftJoin(other, "name", "city")
df.rightJoin(other, "name", "city")
df.fullJoin(other, "name", "city")
df.excludeJoin(other, "name", "city")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Join.joinSpecial.html" width="100%"/>
<!---END-->
