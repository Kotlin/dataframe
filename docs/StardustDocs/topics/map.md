[//]: # (title: map)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Creates [`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/), [DataFrame](DataFrame.md) or [DataColumn](DataColumn.md) 
with values computed from rows of original [DataFrame](DataFrame.md).

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
<tab title="Accessors">

```kotlin
val age by column<Int>()
val yearOfBirth by column<Int>("year of birth")

df.mapToColumn(yearOfBirth) { 2021 - age }
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
    "year of birth" from 2021 - age
    age gt 18 into "is adult"
    name.lastName.length() into "last name length"
    "full name" from { name.firstName + " " + name.lastName }
    +city
}
```

</tab>
<tab title="Accessors">

```kotlin
val yob = column<Int>("year of birth")
val lastNameLength = column<Int>("last name length")
val age by column<Int>()
val isAdult = column<Boolean>("is adult")
val fullName = column<String>("full name")
val name by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()
val city by column<String?>()

df.mapToFrame {
    yob from 2021 - age
    age gt 18 into isAdult
    lastName.length() into lastNameLength
    fullName from { firstName() + " " + lastName() }
    +city
}
```

</tab>
<tab title="Strings">

```kotlin
df.mapToFrame {
    "year of birth" from 2021 - "age"<Int>()
    "age"<Int>() gt 18 into "is adult"
    "name"["lastName"]<String>().length() into "last name length"
    "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    +"city"
}
```

</tab></tabs>
<!---END-->
