[//]: # (title: add)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) which contains all columns from the original [`DataFrame`](DataFrame.md) followed by newly added columns. 
Original [`DataFrame`](DataFrame.md) is not modified.

`add` appends columns to the end of the dataframe by default.
If you want to add a single column to a specific position in the dataframe, use [insert](insert.md).

**Related operations**: [](addRemove.md)

## Create a new column and add it to [`DataFrame`](DataFrame.md)

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
<tab title="Strings">

```kotlin
df.add("year of birth") { 2021 - "age"<Int>() }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.add.html" width="100%"/>
<!---END-->

See [row expressions](DataRow.md#row-expressions)

You can use the `newValue()` function to access value that was already calculated for the preceding row.
It is helpful for recurrent computations:

<!---FUN addRecurrent-->

```kotlin
df.add("fibonacci") {
    if (index() < 2) 1
    else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
}
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.addRecurrent.html" width="100%"/>
<!---END-->

## Create and add several columns to [`DataFrame`](DataFrame.md)

```kotlin
add { 
    columnMapping
    columnMapping
    ...
}

columnMapping = column into columnName 
    | columnName from column 
    | columnName from { rowExpression }
    | columnGroupName { 
        columnMapping
        columnMapping
        ...
    }
```

<!---FUN addMany-->
<tabs>
<tab title="Properties">

```kotlin
df.add {
    "year of birth" from 2021 - age
    age gt 18 into "is adult"
    "details" {
        name.lastName.map { it.length } into "last name length"
        "full name" from { name.firstName + " " + name.lastName }
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
        "name"["lastName"]<String>().map { it.length } into "last name length"
        "full name" from { "name"["firstName"]<String>() + " " + "name"["lastName"]<String>() }
    }
}
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.addMany.html" width="100%"/>
<!---END-->

### Create columns using intermediate result

Consider this API:

<!---FUN addCalculatedApi-->

```kotlin
class CityInfo(val city: String?, val population: Int, val location: String)

fun queryCityInfo(city: String?): CityInfo = CityInfo(city, city?.length ?: 0, "35.5 32.2")
```

<!---END-->

Use the following approach to add multiple columns by calling the given API only once per row:

<!---FUN addCalculated-->
<tabs>
<tab title="Properties">

```kotlin
val personWithCityInfo = df.add {
    val cityInfo = city.map { queryCityInfo(it) }
    "cityInfo" {
        cityInfo.map { it.location } into CityInfo::location
        cityInfo.map { it.population } into "population"
    }
}
```

</tab>
<tab title="Strings">

```kotlin
val personWithCityInfo = df.add {
    val cityInfo = "city"<String?>().map { queryCityInfo(it) }
    "cityInfo" {
        cityInfo.map { it.location } into CityInfo::location
        cityInfo.map { it.population } into "population"
    }
}
```

</tab></tabs>
<!---END-->

## Add existing column to [`DataFrame`](DataFrame.md)

<!---FUN addExisting-->

```kotlin
val score by columnOf(4, 3, 5, 2, 1, 3, 5)

df.add(score)
df + score
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.addExisting.html" width="100%"/>
<!---END-->

## Add all columns from another [`DataFrame`](DataFrame.md)

<!---FUN addDataFrames-->

```kotlin
df.add(df1, df2)
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.addDataFrames.html" width="100%"/>
<!---END-->

## addId

Adds a column with sequential values 0, 1, 2,...
The new column will be added in the beginning of the column list
and will become the first column in [`DataFrame`](DataFrame.md).

```
addId(name: String = "id")
```

**Parameters:**
* `name: String = "id"` - name of the new column.
