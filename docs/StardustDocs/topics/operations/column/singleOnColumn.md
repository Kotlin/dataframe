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


Returns the only value in this [`DataColumn`](DataColumn.md).
If the [`DataColumn`](DataColumn.md) is empty or contains more than one value, throws an exception.

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
