# Data schemas and code generation

In Jupyter environment `DataFrame` provides typed data access by automatic inference of `DataSchema` of new `DataFrame` instances and generation of extension properties for data access

## Overview
After execution of cell
```kotlin
val df = dataFrameOf("name", "age")(
            "Alice", 15,
            "Bob", null)
``` 
the following actions are performed:
1. Variable `df` is analyzed and actual schema of `DataFrame` instance is extracted
2. `DataSchema` interface is generated:
```kotlin
@DataSchema
interface DataFrameType {
    val name: String
    val age: Int?
}
```
3. Extension properties for data access through this `DataSchema` are generated:
```kotlin
val DataFrameBase<DataFrameType>.name: DataColumn<String> get() = this["name"] as DataColumn<String>
val DataRowBase<DataFrameType>.name: String get() = this["name"] as String
val DataFrameBase<DataFrameType>.age: DataColumn<Int?> get() = this["age"] as DataColumn<Int?>
val DataRowBase<DataFrameType>.age: Int? get() = this["age"] as Int?
```
Every column of `DataFrame` produces two extension properties:
* Property for `DataFrame` returns `DataColumn`
* Property for `DataRow` returns cell value
4. `df` variable is typed by schema interface:
```kotlin
val temp = df as DataFrame<DataFrameType>
```
```kotlin
val df = temp
```
Note, that object instance remains the same

To log all these additional code executions, enable cell magic
```
%trackExecution -all
```

## Schema inheritance
In order to reduce amount of generated code, previously generated `DataSchema` interfaces are reused and only new properties are introduced:
```kotlin
val filtered = df.filter { age != null }.add("isAdult") { age!! > 18 }
```
New `DataSchema` will be derived from `DataFrameType`:
```kotlin
@DataSchema
interface DataFrameType2: DataFrameType {
    override val age: Int
    val isAdult: Boolean
}
```
Extension properties are generated only for new and overriden members: 
```kotlin
val DataFrameBase<DataFrameType2>.age: DataColumn<Int> get() = this["age"] as DataColumn<Int>
val DataRowBase<DataFrameType2>.age: Int get() = this["age"] as Int
val DataFrameBase<DataFrameType2>.isAdult: DataColumn<Boolean> get() = this["isAdult"] as DataColumn<Boolean>
val DataRowBase<DataFrameType2>.isAdult: String get() = this["isAdult"] as Boolean
```
Variable is typed by new interface:
```kotlin
val temp = filtered
```
```kotlin
val filtered = temp as DataFrame<DataFrameType2>
```

## Custom data schemas
You can define a new data schema:
```kotlin
@DataSchema
interface Person {
    val name: String
    val age: Int 
}
```
After execution of this cell in Jupyter, extension properties for data access will be generated. 

Now you can write functions for `DataFrame` that matches `Person` schema:
```kotlin
fun DataFrame<Person>.splitName() = split { name }.by(",").into("firstName", "lastName")
fun DataFrame<Person>.adults() = filter { age > 18 }
```
```kotlin
val df = dataFrameOf("name", "age", "weight")(
            "Merton, Alice", 15, 60.0,
            "Marley, Bob", 20, 73.5)
```
Schema of `df` is compatible with `Person`, so auto-generated schema interface will inherit from it:
```kotlin
@DataSchema(isOpen = false)
interface DataFrameType : Person{
    val weight: Double
}
```
And now previously defined methods for `DataFrame<Person>` are available for `df`:
```kotlin
df.splitName()
```
|firstName |lastName |age |weight |
|-----------------|----------------|--------|--------------|
|Merton           |Alice           |15      |60.0     |
|Marley           |Bob             |20      |73.5     |
```kotlin
w.adults()
```
|name |age |weight |
|------------|--------|--------------|
|Marley, Bob |20      |73.5     |

## Support external data schemas in Jupyter
Sometimes it is convenient to extract reusable code from Jupyter notebook into Kotlin JVM library. If this code uses [Custom data schema](#custom-data-schemas), it should be also extracted. In order to support data schema from JVM library in Jupyter, register it in [library integration class](#https://github.com/Kotlin/kotlin-jupyter/blob/master/docs/libraries.md#integration-using-kotlin-api) by `useDataSchemas` method:
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
            useDataSchemas(Person::class)
        }
    }
}
```
After library is loaded into Jupyter notebook, schema interfaces for all `DataFrame` variables that match `Person` schema will derive from `Person` and `countAdults` will be available for them

    