# associateBy

<web-summary>
Discover `associateBy` operation for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `associateBy` operation for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `associateBy` operation for Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop.AssociateBySamples-->

The `associateBy` function builds a `Map` from a [`DataFrame`](DataFrame.md) 
by selecting a key for each row using a [row expression](DataRow.md#row-expressions).  
The rows themselves (or values derived from them) become the map values.

If multiple rows produce the same key, only the last row for that key is kept. 
This matches the behavior of Kotlin’s standard 
[`kotlin.collections.associateBy`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/associate-by.html) 
function.

The keys are in the same order as the rows. A key that occurs in several rows appears
at the position of its first row, with the row of its last one.

```kotlin
df.associateBy { keySelector }

keySelector: (DataRow) -> Key
```

### Related functions
- [`toMap`](toMap.md) — converts a [`DataFrame`](DataFrame.md) into a `Map` by using column names as keys and their values as map values.
- [`associate`](associate.md) — builds a map from key–value pairs produced by transforming each row.

### Example

<!---FUN notebook_test_associateBy_1-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_associateBy_1.html" width="100%" height="500px"></inline-frame>

Create a map with names as keys:

<!---FUN notebook_test_associateBy_2-->

```kotlin
df.associateBy { "${name.firstName} ${name.lastName}" }
```

Output:

```text
{
  Alice Cooper: { name:{ firstName:Alice, lastName:Cooper }, age:15, city:London, weight:54, isHappy:true },
  Bob Dylan: { name:{ firstName:Bob, lastName:Dylan }, age:45, city:Dubai, weight:87, isHappy:true },
  Charlie Daniels: { name:{ firstName:Charlie, lastName:Daniels }, age:20, city:Moscow, isHappy:false },
  Charlie Chaplin: { name:{ firstName:Charlie, lastName:Chaplin }, age:40, city:Milan, isHappy:true },
  Bob Marley: { name:{ firstName:Bob, lastName:Marley }, age:30, city:Tokyo, weight:68, isHappy:true },
  Alice Wolf: { name:{ firstName:Alice, lastName:Wolf }, age:20, weight:55, isHappy:false },
  Charlie Byrd: { name:{ firstName:Charlie, lastName:Byrd }, age:30, city:Moscow, weight:90, isHappy:true },
  Alice Smith: { name:{ firstName:Alice, lastName:Smith }, age:30, city:Milan, weight:52, isHappy:false },
  Bob Brown: { name:{ firstName:Bob, lastName:Brown }, age:15, city:London, weight:60, isHappy:false },
  Charlie Johnson: { name:{ firstName:Charlie, lastName:Johnson }, age:18, city:Dubai, weight:70, isHappy:false }
}
```

<!---END-->

Both rules are visible at once if the city is taken as the key:
`London` comes first, because the first person from London is in the first row,
but it holds the last one. A row without a city gives a `null` key:

<!---FUN notebook_test_associateBy_3-->

```kotlin
df.associateBy { city }
```

Output:

```text
{
  London: { name:{ firstName:Bob, lastName:Brown }, age:15, city:London, weight:60, isHappy:false },
  Dubai: { name:{ firstName:Charlie, lastName:Johnson }, age:18, city:Dubai, weight:70, isHappy:false },
  Moscow: { name:{ firstName:Charlie, lastName:Byrd }, age:30, city:Moscow, weight:90, isHappy:true },
  Milan: { name:{ firstName:Alice, lastName:Smith }, age:30, city:Milan, weight:52, isHappy:false },
  Tokyo: { name:{ firstName:Bob, lastName:Marley }, age:30, city:Tokyo, weight:68, isHappy:true },
  null: { name:{ firstName:Alice, lastName:Wolf }, age:20, weight:55, isHappy:false }
}
```

<!---END-->
