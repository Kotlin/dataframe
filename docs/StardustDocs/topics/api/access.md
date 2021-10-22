[//]: # (title: Access)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

This section describes various ways to get a piece of data out from `DataFrame`
## Get column
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
val lastName by name.column<String>()

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
df.col(0).asColumnGroup().col(1)
```

<!---END-->
Get single column by [condition](columns.md#column-conditions):

<!---FUN getColumnByCondition-->

```kotlin
df.col { it.isNumber() && it.hasNulls() }
```

<!---END-->

## Get row

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
df.first { weight != null }
df.minBy { age }
df.maxBy { name.firstName.length }
df.maxByOrNull { weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()
val name by columnGroup()
val firstName by name.column<String>()

df.single { age() == 45 }
df.first { weight() != null }
df.minBy(age)
df.maxBy { firstName().length }
df.maxByOrNull { weight() }
```

</tab>
<tab title="Strings">

```kotlin
df.single { "age"<Int>() == 45 }
df.first { it["weight"] != null }
df.minBy("weight")
df.maxBy { "name"["firstName"]<String>().length }
df.maxByOrNull("weight")
```

</tab></tabs>
<!---END-->

## Get cell

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
## Get several columns

Returns `DataFrame` with subset of columns

<!---FUN getColumnsByName-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age and weight }
df[df.age, df.weight]
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df.select { age and weight }
df[age, weight]
```

</tab>
<tab title="Strings">

```kotlin
df.select { "age"() and "weight"() }
df["age", "weight"]
```

</tab></tabs>
<!---END-->

## Get several rows

The following operations return `DataFrame` with a subset of rows from original `DataFrame`.

<!---FUN getSeveralRows-->

```kotlin
df[0, 3, 4]
df[1..2]

df.take(5) // first 5 rows
df.takeLast(5) // last 5 rows
df.drop(5) // all rows except first 5
df.dropLast(5) // all rows except last 5
```

<!---END-->
To select several top / bottom rows see [take / takeLast / drop / dropLast](#take--takelast--drop--droplast) operations

To select several rows based on [row condition](rows.md#row-conditions) see [filter / drop](#filter-drop) operations

#### filter / drop
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
val firstName by name.column<String>()

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

### dropNulls / dropNa
`dropNulls` removes rows with `null` values

<!---FUN dropNulls-->

```kotlin
df.dropNulls() // remove rows with null value in any column
df.dropNulls(whereAllNull = true) // remove rows with null values in all columns
df.dropNulls { city } // remove rows with null value in 'city' column
df.dropNulls { city and weight } // remove rows with null value in 'city' OR 'weight' columns
df.dropNulls(whereAllNull = true) { city and weight } // remove rows with null value in 'city' AND 'weight' columns
```

<!---END-->

If you want to remove not only `null`, but also `Double.NaN` values, use `dropNa` 

<!---FUN dropNa-->

```kotlin
df.dropNa() // remove rows containing null or Double.NaN in any column
df.dropNa(whereAllNa = true) // remove rows with null or Double.NaN in all columns
df.dropNa { weight } // remove rows where 'weight' is null or Double.NaN
df.dropNa { age and weight } // remove rows where either 'age' or 'weight' is null or Double.NaN
df.dropNa(whereAllNa = true) { age and weight } // remove rows where both 'age' and 'weight' are null or Double.NaN
```

<!---END-->

### distinct

Removes duplicate rows.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.

<!---FUN distinct-->

```kotlin
df.distinct()
```

<!---END-->

If columns are specified, resulting `DataFrame` will have only given columns with distinct values.

<!---FUN distinctColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.distinct { age and name } shouldBe df.select { age and name }.distinct()
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
df.distinct { age and name } shouldBe df.select { age and name }.distinct()
```

</tab>
<tab title="Strings">

```kotlin
df.distinct("age", "name") shouldBe df.select("age", "name").distinct()
```

</tab></tabs>
<!---END-->

To keep only the first row for every group of rows, grouped by some condition, use `distinctBy` or `distinctByExpr`
* `distinctBy` returns `DataFrame` with rows having distinct values in given columns.
* `distinctByExpr` returns `DataFrame` with rows having distinct values returned by given [row expression](rows.md#row-expressions).

<!---FUN distinctBy-->
<tabs>
<tab title="Properties">

```kotlin
df.distinctBy { age and name } shouldBe df.groupBy { age and name }.mapToRows { group.first() }

df.distinctBy { expr { name.firstName.take(3).lowercase() } }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
val firstName by name.column<String>()

df.distinctBy { age and name } shouldBe df.groupBy { age and name }.mapToRows { group.first() }

df.distinctBy { expr { firstName().take(3).lowercase() } }
```

</tab>
<tab title="Strings">

```kotlin
df.distinctBy("age", "name") shouldBe df.groupBy("age", "name").mapToRows { group.first() }

df.distinctBy { expr { "name"["firstName"]<String>().take(3).lowercase() } }
```

</tab></tabs>
<!---END-->

## stdlib interop

`DataFrame` can be interpreted as an `Iterable<DataRow>`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
<!---FUN iterableApi-->

```kotlin
df.forEach { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```

<!---END-->

### asIterable / asSequence

`DataFrame` can be converted to `Iterable` or to `Sequence`:
<!---FUN asIterableOrSequence-->

```kotlin
df.asIterable()
df.asSequence()
```

<!---END-->

