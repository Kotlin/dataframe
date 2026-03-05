# Known limitations and workarounds

### Compiler plugin in lambdas with receiver marked as DslMarker

Problem: Property calls on a dataframe type created inside @Composable `Column { }` lambda cannot be resolved.
[Issue 1604](https://github.com/Kotlin/dataframe/issues/1604)

The lambda of `Column` has a receiver parameter `content: @Composable ColumnScope.() -> Unit`.

Here's the declaration from Compose. Receiver parameter types with annotations similar to this one will conflict with the plugin.
```kotlin
@LayoutScopeMarker
interface ColumnScope

@DslMarker
annotation class LayoutScopeMarker
```

Repro: The snippet below shows a dataframe variable initialized with a local DataFrame type inside a `Column` lambda. `ageComposableLambdaScope` cannot be resolved.

```kotlin
@DataSchema
data class Person(val age: Int, val name: String)

@Composable
fun DataFrameScreen(df: DataFrame<Person>) {
    Column {
        val filteredDf = remember(df) {
            df
                .add("ageComposableLambdaScope") { age }
                .filter { ageComposableLambdaScope >= 20 }
        }
        filteredDf.ageComposableLambdaScope // error
    }
}
```

Error message:
```
val ColumnsScope<Person_59I>.ageComposableLambdaScope: Int' 
cannot be called in this context with an implicit receiver. 
Use an explicit receiver if necessary
```

Workaround:
Initialize your dataframe properties outside lambdas with DslMarker receiver parameters.

```kotlin
@DataSchema
data class Person(val age: Int, val name: String)

@Composable
fun DataFrameScreen(df: DataFrame<Person>) {
    val filteredDf = remember(df) {
        df
            .add("ageValidScope") { age }
            .filter { ageValidScope >= 20 }
    }
    filteredDf.ageValidScope // OK

    Column {
        Text(filteredDf.ageValidScope.toString())
    }
}
```
