[//]: # (title: Access)

## Access Data

This section describes various ways to get a piece of data from `DataFrame`
### Get single column
#### by column name
<!---docs.api.Access.getColumnByName_strings-->
```kotlin
df["age"]
df["name"]["first"]
```
<!---END-->
#### by column index
<!---docs.api.Access.getColumnByIndex-->
```kotlin
df.col(2)
df.col(0).asGroup().col(1)
```
<!---END-->
#### by column condition
<!---docs.api.Access.getColumnByCondition-->
```kotlin
df.col { it.isNumber() && it.hasNulls() }
```
<!---END-->
### Get single row
#### by row index
<!---docs.api.Access.getRowByIndex-->
```kotlin
df[2]
```
<!---END-->
#### by row condition
<!---docs.api.Access.getRowByCondition_properties-->
```kotlin
df.single { age > 42 }
```
<!---END-->
### Get cell
<!---docs.api.Access.getCell_strings-->
```kotlin
df["age"][1]
df[1]["age"]
```
<!---END-->
### Get several columns
<!---docs.api.Access.getColumnsByName_strings-->
```kotlin
df["age", "weight"]
```
<!---END-->
### Get several rows
#### by row indices
<!---docs.api.Access.getRowsByIndices-->
```kotlin
df[0,3,4]
df[1..2]
df.take(3)
df.skip(2)
df.takeLast(3)
df.skipLast(3)
```
<!---END-->
#### by condition
<!---docs.api.Access.getRowsByCondition_properties-->
```kotlin
df.filter { age > 20 }
```
<!---END-->
#### without nulls
<!---docs.api.Access.dropNulls_properties-->
```kotlin
df.dropNulls { weight }
df.dropNulls { city and weight }
df.dropNulls(whereAllNull = true) { city and weight }
```
<!---END-->
### as iterable
`DataFrame` can be interpreted as an `Iterable` of `DataRow`. Although `DataFrame` doesn't implement `Iterable` interface, it defines most extension functions available for `Iterable`
```kotlin
df.forEach { println(it) }
df.take(5)
df.drop(2)
df.chunked(10)
```
For compatibility with stdlib, `DataFrame` can be converted to `Iterable`
```kotlin
df.asIterable()
```
or to `Sequence`
```kotlin
df.asSequence()
```
### filter / drop
{id="filter"}
Filter rows by row predicate
`filter` keeps rows that satisfy predicate
`drop` removes rows that satisfy predicate (reverse to 'filter')

String API:
```kotlin
df.filter { "age"<Int>() > 10 && "name"<String>().startsWith("A") }
```
Column accessors API:
```kotlin
val age by column<Int>()
val name by column<String>()
df.filter { age() > 10 && name().startsWith("A") }
```
Extension properties API:
```kotlin
df.filter { age > 10 && name.startsWith("A") }
```
More examples:
```kotlin
df.filter { index % 2 == 0} // keep even rows
df.filter { age != prev?.age }
```

### distinct
Removes duplicate rows
```kotlin
df.distinct()
```
If columns are specified, `distinct` returns only selected columns (equivalent to `select { columns }.distint()`):
```kotlin
df.distinct { age and name }
```
#### distinctBy
Returns `DataFrame` containing only rows having distinct values in given columns.
Among rows of the original `DataFrame` with equal keys, only the first one will be present in the resulting `DataFrame`.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.
Resulting `DataFrame` have the same column schema as original `DataFrame`.
```kotlin
df.distinctBy { age and name }
```
#### distinctByExpr
Returns `DataFrame` containing only rows having distinct keys returned by given [row expression](#row-expressions).
Among rows of the original `DataFrame` with equal keys, only the first one will be present in the resulting `DataFrame`.
The rows in the resulting `DataFrame` are in the same order as they were in the original `DataFrame`.
Resulting `DataFrame` have the same column schema as original `DataFrame`.
```kotlin
df.distinctByExpr { name.take(3).lowercase() }
```
### take / takeLast
Returns `DataFrame` containing first/last `n` rows
```kotlin
df.take(10) // first 10 rows
df.takeLast(20) // last 20 rows
```
### drop / dropLast
Returns `DataFrame` containing all rows except first/last `n` rows
```kotlin
df.drop(10)
df.dropLast(20)
```
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
