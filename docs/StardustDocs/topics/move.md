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
See [Column Selectors](ColumnSelectors.md) for column selection syntax.

Columns in `DataFrame` can be ordered hierarchically and form a tree structure. Therefore column can be addressed by `ColumnPath` that represents a list of column names.

`move` operation allows to change hierarchical order of columns in `DataFrame` by providing a new `ColumnPath` for every column

```kotlin
// name, age, weight -> age, name, weight
df.move { age }.toLeft()

// name, age, weight -> name, weight, age
df.move { weight }.to(1)

// name -> info.name
df.move { name }.into("info", "name")

// name -> info.name
df.move { name }.into { info + "name" } // 'info' column should already exist

// firstName -> fullName.firstName
// lastName -> fullName.lastName
df.move { firstName and lastName }.under("fullName")

// firstName -> fullName.first
// lastName -> fullName.last
df.move { firstName and lastName }.into { path("fullName", it.name.dropLast(4)) }

// a:b:c -> a.b.c
df.move { all() }.into { it.name.split(":") }

// totalCases -> total.cases
// totalRecovered -> total.recovered
df.move { cols { it.name.startsWith("total") } }.into { path("total", it.name.substring(5).decapitalize()) }

// some.path.data1 -> new.column.path.data1
// another.path.data2 -> new.column.path.data2
df.move { dfs { it.parent.name == "path" } }.under { new.column.path } // new.column.path should aready exists

// info.default.data -> default
// some.field.data -> field
df.move { dfs { it.name == "data" } }.toTop { it.parent.name }

// a.b -> b.a
// a.b.c -> a.b.c
df.move { dfs { it.path.length == 2 } }.into { it.path.reverse() }
```
