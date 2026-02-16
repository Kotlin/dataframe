[//]: # (title: map)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Creates [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/), [DataFrame](DataFrame.md) or [DataColumn](DataColumn.md) 
with values computed from rows of original [DataFrame](DataFrame.md).

**Related operations**: [](addRemove.md)

**Map into `List`:**

```text
map { rowExpression }: List<T>

rowExpression: DataRow.(DataRow) -> Value
```

<!---FUN map-->

```kotlin
df.map { 2021 - it.age }
```

<!---END-->

**Map into `DataColumn`:**

```text
mapToColumn(columnName) { rowExpression }: DataColumn

rowExpression: DataRow.(DataRow) -> Value
```

<!---FUN mapToColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.mapToColumn("year of birth") { 2021 - age }
```

</tab>
<tab title="Strings">

```kotlin
df.mapToColumn("year of birth") { 2021 - "age"<Int>() }
```

</tab></tabs>
<!---END-->

See [row expressions](DataRow.md#row-expressions)

**Map into [`DataFrame`](DataFrame.md):**

```kotlin
mapToFrame { 
    columnMapping
    columnMapping
    ...
} : DataFrame

columnMapping = column into columnName | columnName from column | columnName from { rowExpression } | +column  
```

<!---FUN mapMany-->
<tabs>
<tab title="Properties">

```kotlin
df.mapToFrame {
    "year of birth" from { 2021 - age }
    expr { age > 18 } into "is adult"
    name.lastName.map { it.length } into "last name length"
    "full name" from { name.firstName + " " + name.lastName }
    +city
}
```

</tab>
<tab title="Strings">

```kotlin
df.mapToFrame {
    "year of birth" from { 2021 - "age"<Int>() }
    expr { "age"<Int>() > 18 } into "is adult"
    "name"["lastName"]<String>().map { it.length } into "last name length"
    "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    +"city"
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.mapMany.html" width="100%"/>
<!---END-->
