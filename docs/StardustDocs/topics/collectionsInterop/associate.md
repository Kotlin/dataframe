# associate

<web-summary>
Discover `associate` interop for Kotlin DataFrame.
</web-summary>

<card-summary>
Discover `associate` interop for Kotlin DataFrame.
</card-summary>

<link-summary>
Discover `associate` interop for Kotlin DataFrame.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.collectionsInterop.AssociateSamples-->

Build a Map from key-value selector lambdas.

```kotlin
df.associate { pairSelector }

pairSelector := (DataRow) -> Pair
```

Related: [](associateBy.md), [](toMap.md)

### Examples

<!---FUN notebook_test_associate_1-->
```kotlin
df
```
<!---END-->

<inline-frame src="./resources/notebook_test_associate_1.html" width="100%" height="500px"></inline-frame>

Create a map from name to age using pair selector:

<!---FUN notebook_test_associate_2-->
```kotlin
df.associate { "${name.firstName} ${name.lastName}" to age }
```
<!---END-->

Output:

```
{
  Alice Cooper: 15,
  Bob Dylan: 45,
  Charlie Daniels: 20,
  Charlie Chaplin: 40,
  Bob Marley: 30,
  Alice Wolf: 20,
  Charlie Byrd: 30
}
```
