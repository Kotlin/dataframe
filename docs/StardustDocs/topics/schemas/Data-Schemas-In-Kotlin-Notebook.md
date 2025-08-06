[//]: # (title: Data Schemas in Kotlin Notebook)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

After execution of a cell

<!---FUN createDfNullable-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", null,
)
```

<!---END-->

the following actions take place:

1. Columns in `df` are analyzed to extract data schema
2. Empty interface with [`DataSchema`](schema.md) annotation is generated:

```kotlin
@DataSchema
interface DataFrameType
```

3. Extension properties for this [`DataSchema`](schema.md) are generated:

```kotlin
val ColumnsContainer<DataFrameType>.age: DataColumn<Int?> @JvmName("DataFrameType_age") get() = this["age"] as DataColumn<Int?>
val DataRow<DataFrameType>.age: Int? @JvmName("DataFrameType_age") get() = this["age"] as Int?
val ColumnsContainer<DataFrameType>.name: DataColumn<String> @JvmName("DataFrameType_name") get() = this["name"] as DataColumn<String>
val DataRow<DataFrameType>.name: String @JvmName("DataFrameType_name") get() = this["name"] as String
```

Every column produces two extension properties:

* Property for `ColumnsContainer<DataFrameType>` returns column
* Property for `DataRow<DataFrameType>` returns cell value

4. `df` variable is typed by schema interface:

```kotlin
val temp = df
```

```kotlin
val df = temp.cast<DataFrameType>()
```

> _Note, that object instance after casting remains the same. See [cast](cast.md).

To log all these additional code executions, use cell magic

```
%trackExecution -all
```

## Custom Data Schemas

You can define your own [`DataSchema`](schema.md) interfaces and use them in functions and classes to represent [`DataFrame`](DataFrame.md) with
a specific set of columns:

```kotlin
@DataSchema
interface Person {
    val name: String
    val age: Int
}
```

After execution of this cell in notebook or annotation processing in IDEA, extension properties for data access will be
generated. Now we can use these properties to create functions for typed [`DataFrame`](DataFrame.md):

```kotlin
fun DataFrame<Person>.splitName() = split { name }.by(",").into("firstName", "lastName")
fun DataFrame<Person>.adults() = filter { age > 18 }
```

In Kotlin Notebook these functions will work automatically for any [`DataFrame`](DataFrame.md) that matches `Person` schema:

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

## Use external Data Schemas

Sometimes it is convenient to extract reusable code from Kotlin Notebook into the Kotlin JVM library.
Schema interfaces should also be extracted if this code uses [Custom Data Schemas](#custom-data-schemas).

In order to enable support them in Kotlin, you should register them in
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

After loading this library into the notebook, schema interfaces for all [`DataFrame`](DataFrame.md) variables that match `Person`
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
