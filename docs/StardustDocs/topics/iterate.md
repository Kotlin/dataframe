[//]: # (title: Iterating)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.IterateSamples-->

`forEach` calls a function for every row of a [`DataFrame`](DataFrame.md), every value of a
[`DataColumn`](DataColumn.md), or every key–group pair of a [`GroupBy`](groupBy.md).
It returns `Unit`, so it is only useful for its side effects, such as printing values or collecting them.
To get a result instead, use [`map`](map.md).

| Operation | Goes over | The function gets |
|-----------|-----------|-------------------|
| [`forEach`](#iterate-over-rows) | rows of a [`DataFrame`](DataFrame.md) | the row, as the receiver and as the argument |
| [`forEach`](#foreach-on-datacolumn) | values of a [`DataColumn`](DataColumn.md) | the value |
| [`forEachIndexed`](#foreach-on-datacolumn) | values of a [`DataColumn`](DataColumn.md) | the position of the value, starting at `0`, and the value |
| [`forEach`](#foreach-on-groupby) | key–group pairs of a [`GroupBy`](groupBy.md) | the pair, as `(key, group)` |

Every example on this page uses the same [`DataFrame`](DataFrame.md):

<!---FUN iterateDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/iterateDf.html" width="100%" height="500px"></inline-frame>

## Iterate over rows

```text
forEach { rowExpression }

rowExpression: DataRow.(DataRow) -> Unit
```

The function is called for every row, from the first one to the last one.
It gets the row both as its receiver and as its argument, so inside it `age` and `it.age` mean the same thing.
A `for` loop and `rows()` go over the same rows in the same order:

<!---FUN iterateRows-->
<tabs>
<tab title="Properties">

```kotlin
for (row in df) {
    println(row.age)
}

df.forEach {
    println(it.age)
}

df.rows().forEach {
    println(it.age)
}
```

</tab>
<tab title="Strings">

```kotlin
for (row in df) {
    println(row["age"])
}

df.forEach {
    println(it["age"])
}

df.rows().forEach {
    println(it["age"])
}
```

</tab></tabs>
<!---END-->

See [row expressions](DataRow.md#row-expressions).

## Iterate over columns

<!---FUN iterateColumns-->

```kotlin
df.columns().forEach {
    println(it.name())
}
```

<!---END-->

## Iterate over cells

<!---FUN iterateCells-->

```kotlin
// from top to bottom, then from left to right
df.values().forEach {
    println(it)
}

// from left to right, then from top to bottom
df.values(byRows = true).forEach {
    println(it)
}
```

<!---END-->

## forEach on DataColumn

```text
forEach { value -> }
forEachIndexed { index, value -> }
```

`forEach` calls the function for every value of a [`DataColumn`](DataColumn.md), from the first one to the last one.
`forEachIndexed` does the same and also gives the position of every value; the position of the first value is `0`.
To get a new [`DataColumn`](DataColumn.md) of computed values instead, use
[`map` or `mapIndexed`](map.md#map-on-datacolumn).

<!---FUN forEachOnColumn-->

```kotlin
// Prints the ages, from the first value to the last one: 15, 45, 20, 40, 30, 20, 30
df.age.forEach { println(it) }
```

<!---END-->

<!---FUN forEachIndexedOnColumn-->

```kotlin
// Prints the first names, numbered: "1. Alice", "2. Bob", ...
df.name.firstName.forEachIndexed { i, firstName -> println("${i + 1}. $firstName") }
```

<!---END-->

## forEach on GroupBy

```text
forEach { (key, group) -> }
```

`forEach` calls the function for every key–group pair of a [`GroupBy`](groupBy.md),
in the order in which the pairs appear in it.
The function gets each pair as a `GroupBy.Entry`: `key` is a [`DataRow`](DataRow.md) with the key values,
and `group` is a [`DataFrame`](DataFrame.md) with the rows of that group.
The pair is the argument of the function, so destructure it as `(key, group)` or use `it.key` and `it.group`.

<!---FUN forEachOnGroupBy-->
<tabs>
<tab title="Properties">

```kotlin
// Prints the number of people per city: "London: 1", "Dubai: 1", "Moscow: 2", ...
df.groupBy { city }.forEach { (key, group) -> println("${key.city}: ${group.rowsCount()}") }
```

</tab>
<tab title="Strings">

```kotlin
// Prints the number of people per city: "London: 1", "Dubai: 1", "Moscow: 2", ...
df.groupBy("city").forEach { (key, group) -> println("${key["city"]}: ${group.rowsCount()}") }
```

</tab></tabs>
<!---END-->
