# last


<web-summary>
Discover `last` operation in Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `last` operation in Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `last` operation in Kotlin DataFrame.
</link-summary>


Returns the last value in this [`DataColumn`](DataColumn.md). If the [`DataColumn`](DataColumn.md) is empty, throws an exception.

If a predicate is specified, returns the last value in this [`DataColumn`](DataColumn.md) that matches the predicate.
Throws an exception if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.

## lastOrNull

Returns the last value in this [`DataColumn`](DataColumn.md). If the [`DataColumn`](DataColumn.md) is empty, returns `null`.

If a predicate is specified, returns the last value in this [`DataColumn`](DataColumn.md) that matches the predicate,
or `null` if the [`DataColumn`](DataColumn.md) contains no elements matching the predicate.
