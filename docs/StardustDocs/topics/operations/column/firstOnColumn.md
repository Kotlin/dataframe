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

If a predicate is specified, returns the first value in this [`DataColumn`](DataColumn.md) that matches the predicate.
Throws an exception if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.

## firstOrNull

Returns the first value in this [`DataColumn`](DataColumn.md). If the [`DataColumn`](DataColumn.md) is empty, returns `null`.

If a predicate is specified, returns the first value in this [`DataColumn`](DataColumn.md) that matches the predicate,
or `null` if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.
