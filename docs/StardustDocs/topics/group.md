[//]: # (title: group)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Group columns into [`ColumnsGroups`](DataColumn.md#columngroup). 

It is a special case of [`move`](move.md) operation.

```kotlin
group { columns }
    .into(groupName) | .into { groupNameExpression }

groupNameExpression = DataColumn.(DataColumn) -> String
```

<!---FUN group-->

```kotlin
df.group { age and city }.into("info")

df.group { all() }.into { it.type().toString() }.print()
```

<!---END-->

To ungroup grouped columns use [`ungroup`](ungroup.md) operation.
