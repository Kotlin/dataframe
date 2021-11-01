[//]: # (title: move)

Moves one or several columns within `DataFrame`.

```kotlin
df.move { column }.into(columnPath)
df.move { columns }.into { columnPathExpression }
df.move { columns }.under(parentPath)
df.move { columns }.under { parentPathExpression }
df.move { columns }.toTop { columnNameExpression }
df.move { columns }.to(position)
df.move { columns }.toLeft()
df.move { columns }.toRight()
df.move { columns }.after { column }
```

See [Column Selectors](ColumnSelectors.md)

Can be used to change hierarchical order of columns in `DataFrame` by providing a new `ColumnPath` for every column

<!---FUN move-->
