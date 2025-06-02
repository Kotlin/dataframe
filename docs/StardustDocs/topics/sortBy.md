[//]: # (title: sortBy)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with rows sorted by one or several columns.

By default, columns are sorted in ascending order with `null` values going first. Available modifiers:
* `.desc` — changes column sort order from ascending to descending
* `.nullsLast` — forces `null` values to be placed at the end of the order

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN sortBy-->
<tabs>
<tab title="Properties">

```kotlin
df.sortBy { age }
df.sortBy { age and name.firstName.desc() }
df.sortBy { weight.nullsLast() }
```

</tab>
<tab title="Strings">

```kotlin
df.sortBy("age")
df.sortBy { "age" and "name"["firstName"].desc() }
df.sortBy { "weight".nullsLast() }
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.sortBy.html" width="100%"/>
<!---END-->

## sortByDesc

Returns [`DataFrame`](DataFrame.md) sorted by one or several columns in descending order.

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN sortByDesc-->
<tabs>
<tab title="Properties">

```kotlin
df.sortByDesc { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.sortByDesc("age", "weight")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.sortByDesc.html" width="100%"/>
<!---END-->

## sortWith

Returns [`DataFrame`](DataFrame.md) sorted with comparator.

<!---FUN sortWith-->

```kotlin
df.sortWith { row1, row2 ->
    when {
        row1.age < row2.age -> -1
        row1.age > row2.age -> 1
        else -> row1.name.firstName.compareTo(row2.name.firstName)
    }
}
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.sortWith.html" width="100%"/>
<!---END-->
