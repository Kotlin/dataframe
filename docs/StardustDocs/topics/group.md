[//]: # (title: group)

Group columns into column groups. It is a special case of [move](#move) operation
```
df.group { columns }.into(groupName)
df.group { columns }.into { groupNameExpression }

groupNameExpression = DataColumn.(DataColumn) -> String
```
Examples
```kotlin
df.group { firstName and lastName }.into("name")
df.group { nameContains(":") }.into { name.substringBefore(":") }
```
