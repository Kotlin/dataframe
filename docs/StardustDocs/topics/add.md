[//]: # (title: add)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) which contains all columns from original [`DataFrame`](DataFrame.md) followed by newly added columns. 
Original [`DataFrame`](DataFrame.md) is not modified.

**Create new column and add it to [`DataFrame`](DataFrame.md):**

```text
add(columnName: String) { rowExpression }

rowExpression: DataRow.(DataRow) -> Value
```

<!---FUN add-->
<tabs>
<tab title="Properties">

```kotlin
df.add("year of birth") { 2021 - age }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val yearOfBirth by column<Int>("year of birth")

df.add(yearOfBirth) { 2021 - age }
```

</tab>
<tab title="Strings">

```kotlin
df.add("year of birth") { 2021 - "age"<Int>() }
```

</tab></tabs>
<!---END-->

See [row expressions](DataRow.md#row-expressions)

You can use `newValue()` function to access value that was already calculated for preceding row. It is helpful for recurrent computations:

<!---FUN addRecurrent-->

```kotlin
df.add("fibonacci") {
    if (index() < 2) 1
    else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
}
```

<!---END-->

**Create and add several columns to [`DataFrame`](DataFrame.md):**

```kotlin
add { 
    columnMapping
    columnMapping
    ...
}

columnMapping = column into columnName | columnName from column | columnName from { rowExpression }
```

<!---FUN addMany-->
<tabs>
<tab title="Properties">

```kotlin
df.add {
    "year of birth" from 2021 - age
    age gt 18 into "is adult"
    "details" {
        name.lastName.length() into "last name length"
        "full name" from { name.firstName + " " + name.lastName }
    }
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
val details by columnGroup()
val firstName by name.column<String>()
val lastName by name.column<String>()

df.add {
    yob from 2021 - age
    age gt 18 into isAdult
    details from {
        lastName.length() into lastNameLength
        fullName from { firstName() + " " + lastName() }
    }
}
```

</tab>
<tab title="Strings">

```kotlin
df.add {
    "year of birth" from 2021 - "age"<Int>()
    "age"<Int>() gt 18 into "is adult"
    "details" {
        "name"["lastName"]<String>().length() into "last name length"
        "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    }
}
```

</tab></tabs>
<!---END-->

**Add existing column to [`DataFrame`](DataFrame.md):**

<!---FUN addExisting-->

```kotlin
val score by columnOf(4, 3, 5, 2, 1, 3, 5)

df.add(score)
df + score
```

<!---END-->

**Add all columns from another [`DataFrame`](DataFrame.md):**

<!---FUN addDfs-->

```kotlin
df.add(df1, df2)
```

<!---END-->

## addId

Adds column with sequential values 0, 1, 2,... New column will be added in the beginning of columns list and will become the first column in [`DataFrame`](DataFrame.md).

```
addId(name: String = "id")
```

**Parameters:**
* `name: String = "id"` - name of the new column.
