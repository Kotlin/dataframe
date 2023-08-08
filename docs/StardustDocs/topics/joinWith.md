[//]: # (title: joinWith)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.JoinWith-->

Joins two [`DataFrames`](DataFrame.md) by a join expression. 

```kotlin
joinWith(otherDf, type = JoinType.Inner) { joinExpression }

joinExpression: JoinedDataRow.(JoinedDataRow) -> Boolean

interface JoinedDataRow: LeftDataRow {
    
    val right: RightDataRow
    
}
```

This function is a [join](join.md) variant that lets you match data using any expression that returns Boolean, 
which also gives opportunity to perform operations that require values from both matching rows.
Can be helpful if data you want to join wasn't designed relational and requires heuristics to tell if rows are matching,
or has relations other than `equals`.

For example, you can match rows based on:
* **Order relations** such as `>`, `<`, `in` for numerical or DateTime values
* **Spatial relations**, like distance within a certain range if your data includes spatial or geographical values
* **String equivalence** using more complex comparison techniques, such as `contains`, regular expressions, Levenshtein Distance or language models.

### Join types with examples

Supported join types:
* `Inner` (default) — only matched rows from left and right [`DataFrames`](DataFrame.md)
* `Filter` — only matched rows from left [`DataFrame`](DataFrame.md)
* `Left` — all rows from left [`DataFrame`](DataFrame.md), mismatches from right [`DataFrame`](DataFrame.md) filled with `null`
* `Right` — all rows from right [`DataFrame`](DataFrame.md), mismatches from left [`DataFrame`](DataFrame.md) filled with `null`
* `Full` — all rows from left and right [`DataFrames`](DataFrame.md), any mismatches filled with `null`
* `Exclude` — only mismatched rows from left

For every join type there is a shortcut operation:

#### Inner join

<!---FUN joinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.innerJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.innerJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.innerJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.joinWith.html"/>
<!---END-->

#### Filter join

Special case of inner join when you only need the data from the left table.

<!---FUN filterJoinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.filterJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.filterJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.filterJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.filterJoinWith.html"/>
<!---END-->

#### Left join

<!---FUN leftJoinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.leftJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.leftJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.leftJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.leftJoinWith.html"/>
<!---END-->

#### Right join

<!---FUN rightJoinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.rightJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.rightJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.rightJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.rightJoinWith.html"/>
<!---END-->

#### Full join

<!---FUN fullJoinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.fullJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.fullJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.fullJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.fullJoinWith.html"/>
<!---END-->

#### Exclude join

Can be viewed as **filterJoin** with logically opposite predicate 

<!---FUN excludeJoinWith-->
<tabs>
<tab title="Properties">

```kotlin
campaigns.excludeJoinWith(visits) {
    right.date in startDate..endDate
}
```

</tab>
<tab title="Accessors">

```kotlin
val date by column<LocalDate>()
val startDate by column<LocalDate>()
val endDate by column<LocalDate>()

campaigns.excludeJoinWith(visits) {
    right[date] in startDate()..endDate()
}
```

</tab>
<tab title="Strings">

```kotlin
campaigns.excludeJoinWith(visits) {
    right[{ "date"<LocalDate>() }] in "startDate"<LocalDate>().."endDate"<LocalDate>()
}
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.excludeJoinWith.html"/>
<!---END-->

#### Cross join

Can also be called cross product of two dataframes

<!---FUN crossProduct-->

```kotlin
campaigns.joinWith(visits) { true }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.crossProduct.html"/>
<!---END-->

### Difference from join

[join](join.md) tries to take advantage of knowledge that data in matching columns is the same (because `equals` is used) to minimize number of columns in the resulting dataframe.

<!---FUN compareInnerColumns-->

```kotlin
df1.innerJoin(df2, "index", "age")
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.compareInnerColumns.html"/>
<!---END-->

Columns that were used in the condition: `index`, `age` - are present only once. Numerical suffix is used to disambiguate columns that are not used in the condition.
Compare it to an equivalent `joinWith`:

<!---FUN compareInnerValues-->

```kotlin
df1.innerJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.compareInnerValues.html"/>
<!---END-->

Here columns from both dataframes are presented as is. So [join](join.md) is better suited for `equals` relation, and joinWith is for everything else.
Below are two more examples with join types that allow mismatches. Note the difference in `null` values

<!---FUN compareLeft-->

```kotlin
df1.leftJoin(df2, "index", "age")
df1.leftJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.compareLeft.html"/>
<!---END-->

<!---FUN compareRight-->

```kotlin
df1.rightJoin(df2, "index", "age")
df1.rightJoinWith(df2) { it["index"] == right["index"] && it["age"] == right["age"] }
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.JoinWith.compareRight.html"/>
<!---END-->

