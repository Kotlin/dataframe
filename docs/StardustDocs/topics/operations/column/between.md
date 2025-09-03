# between


<web-summary>
Return a Boolean DataColumn indicating whether each value lies between two bounds.
</web-summary>

<card-summary>
Return a Boolean DataColumn indicating whether each value lies between two bounds.
</card-summary>

<link-summary>
Return a Boolean DataColumn indicating whether each value lies between two bounds.
</link-summary>


Returns a [`DataColumn`](DataColumn.md) of `Boolean` values indicating whether each element in this column
lies between the given lower and upper boundaries.

If `includeBoundaries` is `true` (default), values equal to the lower or upper boundary are also considered in range.

```kotlin
col.between(left, right, includeBoundaries)
```
