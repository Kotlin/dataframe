[//]: # (title: map)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Creates `DataFrame` or `DataColumn` with values computed from rows of original `DataFrame`.

**Map into `DataColumn`:**

```text
map(columnName) { rowExpression }: DataColumn

rowExpression: DataRow.(DataRow) -> Value
```

See [row expressions](DataRow.md#row-expressions)

**Map into `DataFrame`:**

```kotlin
map { 
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
df.map {
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

df.map {
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
df.map {
    "year of birth" from 2021 - "age"<Int>()
    "age"<Int>() gt 18 into "is adult"
    "name"["lastName"]<String>().length() into "last name length"
    "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    +"city"
}
```

</tab></tabs>
<!---END-->
