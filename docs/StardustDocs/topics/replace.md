[//]: # (title: replace)

Replaces one or several columns with new columns
```
df.replace { columns }.with(newColumns)
df.replace { columns }.with { columnExpression }

columnExpression = DataFrame.(DataColumn) -> DataColumn
```
Examples
```kotlin
df.replace { col1 and col2 }.with(newCol1, newCol2)
df.replace { stringCols() }.with { it.lower() }
df.replace { oldColumn }.with(newColumn.rename("newName"))
df.replace { age }.with { 2021 - age named "year" } // another syntax for renaming columns within replace expression
```
