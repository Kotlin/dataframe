[//]: # (title: insert)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Inserts new column at specific position in [`DataFrame`](DataFrame.md). 

```text
insert 
    (columnName) { rowExpression } | (column)
    .under { parentColumn } | .after { column } | .at(position)

rowExpression: DataRow.(DataRow) -> Value
```

Similar to [add](add.md), but supports column positioning.

Create new column based on existing columns and insert it into [`DataFrame`](DataFrame.md):

**Related operations**: [](insertReplace.md)

<!---FUN insert-->
<tabs>
<tab title="Properties">

```kotlin
df.insert("year of birth") { 2021 - age }.after { age }
```

</tab>
<tab title="Strings">

```kotlin
df.insert("year of birth") { 2021 - "age"<Int>() }.after("age")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.insert.html" width="100%"/>
<!---END-->

Insert previously created column:

<!---FUN insertColumn-->

```kotlin
val score by columnOf(4, 5, 3, 5, 4, 5, 3)
df.insert(score).at(2)
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.insertColumn.html" width="100%"/>
<!---END-->
