[//]: # (title: join)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Join-->

Joins two DataFrames by join columns.

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
<tab title="Accessors">

```kotlin
val name by columnGroup()
val fullName by columnGroup()

df.join(other) { name match fullName }
```

</tab>
<tab title="Strings">

```kotlin
df.join(other) { "name" match "fullName" }
```

</tab></tabs>
<!---END-->

If mapped columns have the same name, just select join columns from the left `DataFrame`: 

<!---FUN join-->

```kotlin
df.join(other) { name and city }
```

<!---END-->

If `joinColumns` is not specified, columns with the same name from both DataFrames will be used as key columns:

<!---FUN joinDefault-->

```kotlin
df.join(other)
```

<!---END-->

