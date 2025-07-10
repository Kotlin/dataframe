[//]: # (title: Use external Data Schemas in Jupyter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

Sometimes it is convenient to extract reusable code from Jupyter Notebook into the Kotlin JVM library.
Schema interfaces should also be extracted if this code uses [Custom Data Schemas](schemasCustom.md). 

In order to enable support them in Jupyter, you should register them in
library [integration class](https://github.com/Kotlin/kotlin-jupyter/blob/master/docs/libraries.md) with `useSchema`
function:

```kotlin
@DataSchema
interface Person {
    val name: String
    val age: Int
}

fun DataFrame<Person>.countAdults() = count { it[Person::age] > 18 }

@JupyterLibrary
internal class Integration : JupyterIntegration() {

    override fun Builder.onLoaded() {
        onLoaded {
            useSchema<Person>()
        }
    }
}
```

After loading this library into Jupyter notebook, schema interfaces for all [`DataFrame`](DataFrame.md) variables that match `Person`
schema will derive from `Person`

<!---FUN createDf-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", 20,
)
```

<!---END-->

Now `df` is assignable to `DataFrame<Person>` and `countAdults` is available:

```kotlin
df.countAdults()
```
