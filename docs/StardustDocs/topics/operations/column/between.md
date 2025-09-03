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

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.column.BetweenSamples-->

Returns a [`DataColumn`](DataColumn.md) of `Boolean` values indicating whether each element in this column
lies between the given lower and upper boundaries.

If `includeBoundaries` is `true` (default), values equal to the lower or upper boundary are also considered in range.

```kotlin
col.between(left, right, includeBoundaries)
```

### Examples

<!---FUN notebook_test_between_1-->
```kotlin
df
```
<!---END-->
<inline-frame src="./resources/notebook_test_between_1.html" width="100%" height="500px"></inline-frame>

Check ages are between 18 and 25 inclusive:
<!---FUN notebook_test_between_2-->
```kotlin
// Create a Boolean column indicating whether ages are between 18 and 25 (inclusive)
val inRange = df["age"].cast<Int>().between(left = 18, right = 25)
```
<!---END-->
<inline-frame src="./resources/notebook_test_between_2.html" width="100%" height="500px"></inline-frame>

Strictly between 18 and 25 (exclude boundaries):
<!---FUN notebook_test_between_3-->
```kotlin
// Exclude boundaries: strictly between 18 and 25 (i.e., 19..24)
val strictly = df["age"].cast<Int>().between(left = 18, right = 25, includeBoundaries = false)
```
<!---END-->
<inline-frame src="./resources/notebook_test_between_3.html" width="100%" height="500px"></inline-frame>

