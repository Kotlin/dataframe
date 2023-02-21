[//]: # (title: Working with Data Schemas)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Schemas-->

Kotlin Dataframe library provides typed data access via [generation of extension properties](extensionPropertiesApi.md) for
type `DataFrame<T>`, where
`T` is a marker class that represents `DataSchema` of [`DataFrame`](DataFrame.md).

Schema of [`DataFrame`](DataFrame.md) is a mapping from column names to column types of [`DataFrame`](DataFrame.md).
It ignores order of columns in [`DataFrame`](DataFrame.md), but tracks column hierarchy.

In Jupyter environment compile-time [`DataFrame`](DataFrame.md) schema is synchronized with real-time data after every cell execution.

In IDEA projects you can use [gradle plugin](installation.md#data-schema-preprocessor) to extract schema from dataset
and generate extension properties.

## DataSchema workflow in Jupyter

After execution of cell

<!---FUN createDfNullable-->

```kotlin
val df = dataFrameOf("name", "age")(
    "Alice", 15,
    "Bob", null
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

## Schema inheritance

In order to reduce amount of generated code, previously generated [`DataSchema`](schema.md) interfaces are reused and only new
properties are introduced

Let's filter out all `null` values from `age` column and add one more column of type `Boolean`:

```kotlin
val filtered = df.filter { age != null }.add("isAdult") { age!! > 18 }
```

New schema interface for `filtered` variable will be derived from previously generated `DataFrameType`:

```kotlin
@DataSchema
interface DataFrameType1 : DataFrameType
```

Extension properties for data access are generated only for new and overriden members of `DataFrameType1` interface:

```kotlin
val ColumnsContainer<DataFrameType1>.age: DataColumn<Int> get() = this["age"] as DataColumn<Int>
val DataRow<DataFrameType1>.age: Int get() = this["age"] as Int
val ColumnsContainer<DataFrameType1>.isAdult: DataColumn<Boolean> get() = this["isAdult"] as DataColumn<Boolean>
val DataRow<DataFrameType1>.isAdult: String get() = this["isAdult"] as Boolean
```

Then variable `filtered` is cast to new interface:

```kotlin
val temp = filtered
```

```kotlin
val filtered = temp.cast<DataFrameType1>()
```

## Custom data schemas

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
    "Marley, Bob", 20, 73.5
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

## Use external data schemas in Jupyter

Sometimes it is convenient to extract reusable code from Jupyter notebook into Kotlin JVM library. If this code
uses [Custom data schemas](#custom-data-schemas), schema interfaces should also be extracted. In order to enable support
them in Jupyter, you should register them in
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
    "Bob", 20
)
```

<!---END-->

Now `df` is assignable to `DataFrame<Person>` and `countAdults` is available:

```kotlin
df.countAdults()
```

## Import Data Schemas, e.g. from OpenAPI, in Jupyter

Similar to [importing OpenAPI data schemas in Gradle projects](gradle.md#openapi-schemas), you can also
do this in Jupyter notebooks. There is only a slight difference in notation:

Import the schema using any path (`String`), `URL`, or `File`:

```kotlin
val PetStore = importDataSchema("https://petstore3.swagger.io/api/v3/openapi.json")
```

and then from next cell you run and onwards, you can call, for example:

```kotlin
val df = PetStore.Pet.readJson("https://petstore3.swagger.io/api/v3/pet/findByStatus?status=available")
```

So, very similar indeed!

(Note: The type of `PetStore` will be generated as `PetStoreDataSchema`, but this doesn't affect the way you can use
it.)
