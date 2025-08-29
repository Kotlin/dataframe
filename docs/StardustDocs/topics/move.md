[//]: # (title: move)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Moves one or several columns within [`DataFrame`](DataFrame.md).

```kotlin
move { columns }
    .into { pathSelector } | .under { parentColumn } | .after { column } | .to(position) | .toTop() | .toStart() | .toEnd()

pathSelector: DataFrame.(DataColumn) -> ColumnPath
```

**Related operations**: [](moveRename.md)

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

Can be used to change column hierarchy by providing `ColumnPath` for every moved column.

<!---FUN move-->

```kotlin
df.move { age }.toStart()

df.move { weight }.to(1)

// age -> info.age
// weight -> info.weight
df.move { age and weight }.into { pathOf("info", it.name()) }
df.move { age and weight }.into { "info"[it.name()] }
df.move { age and weight }.under("info")

// name.firstName -> fullName.first
// name.lastName -> fullName.last
df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name().dropLast(4)) }

// a|b|c -> a.b.c
// a|d|e -> a.d.e
dataFrameOf("a|b|c", "a|d|e")(0, 0)
    .move { all() }.into { it.name().split("|").toPath() }

// name.firstName -> firstName
// name.lastName -> lastName
df.move { name.allCols() }.toTop()

// a.b.e -> be
// c.d.e -> de
df.move { colsAtAnyDepth().nameContains("e") }.toTop { it.parentName + it.name() }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.move.html" width="100%"/>
<!---END-->

Special cases of `move`:
* [`group`](group.md) — groups columns into [`ColumnGroups`](DataColumn.md#columngroup)
* [`ungroup`](ungroup.md) — ungroups [`ColumnGroups`](DataColumn.md#columngroup)
* [`flatten`](flatten.md) — removes all column groupings
