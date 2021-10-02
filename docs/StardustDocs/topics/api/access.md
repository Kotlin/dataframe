[//]: # (title: Access)

<!---IMPORT docs.api.Access-->

This section describes various ways to get a piece of data out from `DataFrame`
## Basics
### Get column
Get single column by column name:

<!---FUN getColumnByName-->
<tabs>
<tab title="Properties">

```kotlin
df.age
df.name.lastName
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val lastName by column<String>(name)

df[age]
df[lastName]
```

</tab>
<tab title="Strings">

```kotlin
df["age"]
df["name"]["firstName"]
```

</tab></tabs>
<!---END-->

Get single column by index (starting from 0):

<!---FUN getColumnByIndex-->

```kotlin
df.col(2)
df.col(0).asGroup().col(1)
```

<!---END-->
Get single column by [condition](columns.md#column-conditions):

<!---FUN getColumnByCondition-->

```kotlin
df.col { it.isNumber() && it.hasNulls() }
```

<!---END-->
### Get row
Get single row by index (starting from 0):
<!---FUN getRowByIndex-->

```kotlin
df[2]
```

<!---END-->
Get single row by [condition](rows.md#row-conditions):

<!---FUN getRowByCondition-->
<tabs>
<tab title="Properties">

```kotlin
df.single { age == 45 }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()

df.single { age() == 45 }
```

</tab>
<tab title="Strings">

```kotlin
df.single { "age"<Int>() == 45 }
```

</tab></tabs>
<!---END-->

### Get cell

<!---FUN getCell-->
<tabs>
<tab title="Properties">

```kotlin
df.age[1]
df[1].age
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<String>()

df[age][1]
df[1][age]
```

</tab>
<tab title="Strings">

```kotlin
df["age"][1]
df[1]["age"]
```

</tab></tabs>
<!---END-->
### Get several columns

<!---FUN getColumnsByName-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age and weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()
df[age, weight]
df.select { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df["age", "weight"]
```

</tab></tabs>
<!---END-->
### Get several rows

The following operations return `DataFrame` with a subset of rows from original `DataFrame`.


#### by row indices


<!---FUN getRowsByIndices-->

```kotlin
df[0, 3, 4]
df[1..2]
```

<!---END-->
To select several top / bottom rows see [take / takeLast / drop / dropLast](#take--takelast--drop--droplast) operations

To select several rows based on [row condition](rows.md#row-conditions) see [filter / drop](#filter-drop) operations

#### without nulls

<!---FUN dropNulls_properties-->

```kotlin
df.dropNulls { weight }
df.dropNulls { city and weight }
df.dropNulls(whereAllNull = true) { city and weight }
```

<!---END-->
### as iterable
`DataFrame` can be interpreted as an `Iterable<DataRow>`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
<!---FUN iterableApi-->

```kotlin
df.forEach { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```

<!---END-->

For compatibility with stdlib, `DataFrame` can be converted to `Iterable` or to `Sequence`:
<!---FUN asIterableOrSequence-->

```kotlin
df.asIterable()
df.asSequence()
```

<!---END-->
### filter / drop
Filter rows by [row condition](rows.md#row-conditions)
`filter` keeps only rows that satisfy condition
`drop` removes all rows that satisfy condition

<!---FUN filterDrop-->
<tabs>
<tab title="Properties">

```kotlin
df.filter { age > 18 && name.firstName.startsWith("A") }
df.drop { weight == null || city == null }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val weight by column<Int?>()
val city by column<String?>()
val firstName by column<String>(name)

df.filter { age() > 18 && firstName().startsWith("A") }
df.drop { weight() == null || city() == null }
// or
df.filter { it[age] > 18 && it[firstName].startsWith("A") }
df.drop { it[weight] == null || it[city] == null }
```

</tab>
<tab title="Strings">

```kotlin
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }.nrow shouldBe 1
df.drop { it["weight"] == null || it["city"] == null }
```

</tab></tabs>
<!---END-->

### distinct
Removes duplicate rows

<!---FUN distinct-->

```kotlin
df.distinct()

// Select only 'age' and 'name' columns with distinct values
df.distinct { age and name }
// is equivalent to
df.select { age and name }.distinct()
```

<!---END-->

#### distinctBy
Returns `DataFrame` containing only rows having distinct values in given columns.
Among rows of the original `DataFrame` with equal keys, only the first one will be present in the resulting `DataFrame`.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.
Resulting `DataFrame` have the same column schema as original `DataFrame`.
```kotlin
df.distinctBy { age and name }
```
#### distinctByExpr
Returns `DataFrame` containing only rows having distinct keys returned by given [row expression](rows.md#row-expressions).
Among rows of the original `DataFrame` with equal keys, only the first one will be present in the resulting `DataFrame`.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.
Resulting `DataFrame` have the same column schema as original `DataFrame`.
```kotlin
df.distinctByExpr { name.take(3).lowercase() }
```
### take / takeLast / drop / dropLast
Returns `DataFrame` containing several top or bottom rows

<!---FUN takeDrop-->

```kotlin
df.take(10) // first 10 rows
df.takeLast(10) // last 10 rows
df.drop(10) // all rows except first 10
df.dropLast(10) // all rows except last 10
```

<!---END-->

### dropNulls / dropNa
`dropNulls` removes rows with `null` values
```kotlin
df.dropNulls() // remove rows containing null value in any column
df.dropNulls(whereAllNull = true) // remove rows with null value in all columns
df.dropNulls { col1 and col2 } // remove rows with null value in col1 or col2 columns
df.dropNulls(whereAllNull = true) { col1 and col2 } // remove rows with null value in col1 and col2 columns
```
`dropNa` removes rows with `null` or `Double.NaN` values
```kotlin
df.dropNa() // remove rows containing null or Double.NaN in any column
df.dropNa(whereAllNa = true) // remove rows with null or Double.NaN in all columns
df.dropNa { col1 and col2 } // remove rows with null or Double.NaN in col1 or col2 columns
df.dropNa(whereAllNa = true) { col1 and col2 } // remove rows with null or Double.NaN in col1 and col2 columns
```
