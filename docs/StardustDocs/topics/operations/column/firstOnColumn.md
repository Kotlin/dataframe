# first


<web-summary>
Discover `first` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `first` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `first` operation in Kotlin Dataframe.
</link-summary>


Returns the first value in this [`DataColumn`](DataColumn.md). If the [`DataColumn`](DataColumn.md) is empty, throws an exception.

<!---FUN firstOnColumnDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/firstOnColumnDf.html" width="100%" height="500px"></inline-frame>

<!---FUN firstOnColumn-->

```kotlin
df.name.first() // returns "Alice"
```

<!---END-->

If a predicate is specified, returns the first value in this [`DataColumn`](DataColumn.md) that matches the predicate.
Throws an exception if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.

<!---FUN firstOnColumnPredicate-->

```kotlin
df.age.first { it > 17 } // returns 20
```

<!---END-->

## firstOrNull

Returns the first value in this [`DataColumn`](DataColumn.md). If the [`DataColumn`](DataColumn.md) is empty, returns `null`.

<!---FUN firstOrNullOnColumn-->
<tabs>
<tab title="Properties">

```kotlin
df
    .filter { age > 50 } // df is empty after filtering
    .age
    .firstOrNull() // returns null
```

</tab>
<tab title="Strings">

```kotlin
df
    .filter { "age"<Int>() > 50 } // df is empty after filtering
    .age
    .firstOrNull() // returns null
```

</tab></tabs>
<!---END-->

If a predicate is specified, returns the first value in this [`DataColumn`](DataColumn.md) that matches the predicate,
or `null` if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.

<!---FUN firstOrNullOnColumnPredicate-->

```kotlin
df.age.firstOrNull { it > 50 } // returns null
```

<!---END-->
