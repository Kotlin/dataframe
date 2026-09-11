# single


<web-summary>
Discover `single` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `single` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `single` operation in Kotlin Dataframe.
</link-summary>


Returns the single value in this [`DataColumn`](DataColumn.md).
Throws [`NoSuchElementException`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-no-such-element-exception/)
if the [`DataColumn`](DataColumn.md) is empty, and
[`IllegalArgumentException`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-illegal-argument-exception/)
if it contains more than one value.

<!---FUN singleOnColumnDf-->

```kotlin
df
```

<!---END-->
<inline-frame src="./resources/singleOnColumnDf.html" width="100%" height="500px"></inline-frame>

<!---FUN singleOnColumn-->

```kotlin
df
    .filter { name == "Bob" } // one row is left after filtering
    .age
    .single() // returns 20
```

<!---END-->

Use [`first`](firstOnColumn.md) or [`last`](lastOnColumn.md)
when the [`DataColumn`](DataColumn.md) may contain more than one value.
