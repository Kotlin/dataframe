[//]: # (title: move)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Moves one or several columns within `DataFrame`.

```kotlin
df.move { column }.into(columnPath)
df.move { columns }.into { columnPathExpression }
df.move { columns }.under(parentPath)
df.move { columns }.under { parentPathExpression }
df.move { columns }.toTop()
df.move { columns }.toTop { columnNameExpression }
df.move { columns }.to(position)
df.move { columns }.toLeft()
df.move { columns }.toRight()
df.move { columns }.after { column }
```

See [Column Selectors](ColumnSelectors.md)

Can be used to change hierarchical order of columns in `DataFrame` by providing a new `ColumnPath` for every column

<!---FUN move-->

```kotlin
df.move { age }.toLeft()

df.move { weight }.to(1)

// name -> info.name
df.move { name }.into("info", "name")

// name -> info.name
df.move { age }.into { pathOf("info", it.name) }

// firstName -> fullName.firstName
// lastName -> fullName.lastName
df.move { age and weight }.under("info")

// name.firstName -> fullName.first
// name.lastName -> fullName.last
df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name.dropLast(4)) }

dataFrameOf("a.b.c", "a.d.e")(1, 2)
    .move { all() }.into { it.name.split(".").toPath() }

// name.firstName -> firstName
// name.lastName -> lastName
df.move { name.cols() }.toTop()

// group1.default.name -> defaultData
// group2.field.name -> fieldData
df.move { dfs { it.name == "data" } }.toTop { it.parent!!.name + "Data" }
```

<!---END-->
