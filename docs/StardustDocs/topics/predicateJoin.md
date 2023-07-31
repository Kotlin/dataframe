[//]: # (title: predicateJoin)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Join-->

Joins two [`DataFrames`](DataFrame.md) by a join expression. 

```kotlin
predicateJoin(otherDf, type = JoinType.Inner) [ { joinExpression } ]

joinExpression: JoinedDataRow.(JoinedDataRow) -> Boolean

interface JoinedDataRow: LeftDataRow {
    
    val right: RightDataRow
    
}
```

This function is a [join](join.md) variant that lets you match data using any expression that returns Boolean, 
which also gives opportunity to compute heuristics that require values from both matching rows.
Can be helpful if data you want to join wasn't designed be relational and requires heuristics to tell if rows are matching,
or has relations other than `equals`.

For example, you can match rows based on:
* **Order relations** such as `>`, `<`, `in` for numerical or DateTime values
* **Numerical relations**
* **Spatial relations**, like distance within a certain range if your data includes spatial or geographical values
* **String equivalence** using more complex comparison techniques, such as regular expressions, Levenshtein Distance.

### Join types

Supported join types:
* `Inner` (default) — only matched rows from left and right [`DataFrames`](DataFrame.md)
* `Filter` — only matched rows from left [`DataFrame`](DataFrame.md)
* `Left` — all rows from left [`DataFrame`](DataFrame.md), mismatches from right [`DataFrame`](DataFrame.md) filled with `null`
* `Right` — all rows from right [`DataFrame`](DataFrame.md), mismatches from left [`DataFrame`](DataFrame.md) filled with `null`
* `Full` — all rows from left and right [`DataFrames`](DataFrame.md), any mismatches filled with `null`
* `Exclude` — only mismatched rows from left

For every join type there is a shortcut operation:

#### Inner join

<!---FUN predicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.predicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.predicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.predicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.predicateJoin.html"/>
<!---END-->

#### Filter join

Special case of inner join when you only need the data from the left table.

<!---FUN filterPredicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.filterPredicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.filterPredicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.filterPredicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.filterPredicateJoin.html"/>
<!---END-->

#### Left join

<!---FUN leftPredicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.leftPredicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.leftPredicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.leftPredicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.leftPredicateJoin.html"/>
<!---END-->

#### Right join

<!---FUN rightPredicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.rightPredicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.rightPredicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.rightPredicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.rightPredicateJoin.html"/>
<!---END-->

#### Full join

<!---FUN fullPredicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.fullPredicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.fullPredicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.fullPredicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.fullPredicateJoin.html"/>
<!---END-->

#### Exclude join

Can be viewed as **filterJoin** with logically opposite predicate 

<!---FUN excludePredicateJoin-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.excludePredicateJoin(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.excludePredicateJoin(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.excludePredicateJoin(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.excludePredicateJoin.html"/>
<!---END-->

#### Cross join

Can also be called cross product of two dataframes

<!---FUN crossProduct-->

```kotlin
campaigns.predicateJoin(visits) { true }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Join.crossProduct.html"/>
<!---END-->

