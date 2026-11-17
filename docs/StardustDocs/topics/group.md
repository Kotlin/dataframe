[//]: # (title: group)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Group columns into [`ColumnGroups`](DataColumn.md#columngroup). 

**API reference**: [`group`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/group.html)

```text
group { columns }
    .into(groupName) | .into { groupNameExpression }

groupNameExpression = DataColumn.(DataColumn) -> String
```

**Reverse operation:** [`ungroup`](ungroup.md), [`flatten`](flatten.md)

It is a special case of [`move`](move.md) operation.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN group-->

```kotlin
df.group { age and city }.into("info")

df.group { all() }.into { it.type().toString() }.print()
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.group.html" width="100%"/>
<!---END-->
