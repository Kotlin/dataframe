# associateBy

<web-summary>
Discover `associateBy` interop for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `associateBy` interop for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `associateBy` interop for Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop.AssociateBySamples-->

Build a Map from a DataFrame by selecting a key for each row.

Returns Map<K, Row/Value> depending on variant used.

```kotlin
df.associateBy { keySelector }

keySelector := (DataRow) -> Boolean 
```

Related: [](associate.md), [](toMap.md)

### Examples

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
<!---END-->

Output:

```
{
  Alice Cooper: { name:{ firstName:Alice, lastName:Cooper }, age:15, city:London, weight:54, isHappy:true },
  Bob Dylan: { name:{ firstName:Bob, lastName:Dylan }, age:45, city:Dubai, weight:87, isHappy:true },
  Charlie Daniels: { name:{ firstName:Charlie, lastName:Daniels }, age:20, city:Moscow, isHappy:false },
  Charlie Chaplin: { name:{ firstName:Charlie, lastName:Chaplin }, age:40, city:Milan, isHappy:true },
  Bob Marley: { name:{ firstName:Bob, lastName:Marley }, age:30, city:Tokyo, weight:68, isHappy:true },
  Alice Wolf: { name:{ firstName:Alice, lastName:Wolf }, age:20, weight:55, isHappy:false },
  Charlie Byrd: { name:{ firstName:Charlie, lastName:Byrd }, age:30, city:Moscow, weight:90, isHappy:true }
}
```
