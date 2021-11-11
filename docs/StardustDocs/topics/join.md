[//]: # (title: join)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Joins two DataFrames by join columns.

```kotlin
join(otherDf, type = JoinType.Inner) [ { joinColumns } ]

joinColumns: JoinDsl.(LeftDataFrame) -> Columns

interface JoinDsl: LeftDataFrame {
    
    val right: RightDataFrame
    
    fun DataColumn.match(rightColumn: DataColumn)
}
```

`joinColumns` is a [column selector](ColumnSelectors.md) that defines column mapping for join. 

If mapped columns have the same name, just select join columns from the left `DataFrame`: 

<!---FUN joinWithMatch-->

If `joinColumns` is not specified, columns with the same name from both DataFrames will be used as key columns. 

