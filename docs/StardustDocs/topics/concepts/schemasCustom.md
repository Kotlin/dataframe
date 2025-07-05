[//]: # (title: Custom Data Schemas)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

You can define your own [`DataSchema`](schema.md) interfaces and use them in functions and classes to represent [`DataFrame`](DataFrame.md) with
specific set of columns:

```kotlin
@DataSchema
interface Person {
    val name: String
    val age: Int
}
```

After execution of this cell in Jupyter or annotation processing in IDEA, extension properties for data access will be
generated. Now we can use these properties to create functions for typed [`DataFrame`](DataFrame.md):

```kotlin
fun DataFrame<Person>.splitName() = split { name }.by(",").into("firstName", "lastName")
fun DataFrame<Person>.adults() = filter { age > 18 }
```

In Jupyter these functions will work automatically for any [`DataFrame`](DataFrame.md) that matches `Person` schema:

<!---FUN extendedDf-->

```kotlin
val df = dataFrameOf("name", "age", "weight")(
    "Merton, Alice", 15, 60.0,
    "Marley, Bob", 20, 73.5,
)
```

<!---END-->

Schema of `df` is compatible with `Person`, so auto-generated schema interface will inherit from it:

```kotlin
@DataSchema(isOpen = false)
interface DataFrameType : Person

val ColumnsContainer<DataFrameType>.weight: DataColumn<Double> get() = this["weight"] as DataColumn<Double>
val DataRow<DataFrameType>.weight: Double get() = this["weight"] as Double
```

Despite `df` has additional column `weight`, previously defined functions for `DataFrame<Person>` will work for it:

<!---FUN splitNameWorks-->

```kotlin
df.splitName()
```

<!---END-->

```text
firstName lastName age weight
   Merton    Alice  15 60.000
   Marley      Bob  20 73.125
```

<!---FUN adultsWorks-->

```kotlin
df.adults()
```

<!---END-->

```text
name        age weight
Marley, Bob  20   73.5
```

In JVM project you will have to [cast](cast.md) [`DataFrame`](DataFrame.md) explicitly to the target interface:

```kotlin
df.cast<Person>().splitName()
```
