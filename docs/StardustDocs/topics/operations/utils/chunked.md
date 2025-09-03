# chunked


<web-summary>
Discover `chunked` operation in Kotlin Dataframe.
</web-summary>

<card-summary>
Discover `chunked` operation in Kotlin Dataframe.
</card-summary>

<link-summary>
Discover `chunked` operation in Kotlin Dataframe.
</link-summary>


Splits a [`DataFrame`](DataFrame.md) into consecutive sub-dataframes (chunks) and returns them as a
[`FrameColumn`](DataColumn.md#framecolumn). Chunks are formed in order and do not overlap.

Each chunk contains at most the specified number of rows.
The resulting FrameColumn’s name can be customized; by default, it is "groups."

`DataFrame` can be split into chunks in two ways :
- By fixed size: split into chunks of up to the given size.
- By start indices: split using custom zero-based start indices for each chunk; 
each chunk ends right before the next start index or the end of the DataFrame.


```kotlin
df.chunked(size: Int, name: String)
df.chunked(startIndices: Iterable<Int>, name: String)
```
