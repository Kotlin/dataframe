# Data Schemas/Data Classes Generation

<web-summary>
Generate useful Kotlin definitions based on your DataFrame structure.
</web-summary>

<card-summary>
Generate useful Kotlin definitions based on your DataFrame structure.
</card-summary>

<link-summary>
Generate useful Kotlin definitions based on your DataFrame structure.
</link-summary>

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Generate-->

Special utility functions that generate code of useful Kotlin definitions (returned as a `String`) 
based on the current `DataFrame` schema.


## generateInterfaces

```kotlin
inline fun <reified T> DataFrame<T>.generateInterfaces(): CodeString

fun <T> DataFrame<T>.generateInterfaces(markerName: String): CodeString
```

Generates [`@DataSchema`](schemas.md) interfaces for this `DataFrame` 
(including all nested `DataFrame` columns and column groups) as Kotlin interfaces.

This is useful when working with the [compiler plugin](Compiler-Plugin.md) 
in cases where the schema cannot be inferred automatically from the source.

### Arguments {id="generateInterfaces-arguments"}
* `markerName`: `String` – The base name to use for generated interfaces.  
  If not specified, uses the `T` type argument of `DataFrame` simple name.

### Returns {id="generateInterfaces-returns"}
* `CodeString` – A value class wrapper for `String`, containing  
  the generated Kotlin code of `@DataSchema` interfaces without extension properties.

### Examples {id="generateInterfaces-examples"}


<!---FUN notebook_test_generate_docs_1-->

```kotlin
df
```

<!---END-->

<inline-frame src="./resources/notebook_test_generate_docs_1.html" width="100%" height="500px"></inline-frame>

<!---FUN notebook_test_generate_docs_2-->

```kotlin
df.generateInterfaces()
```

<!---END-->

Output:
```kotlin
@DataSchema(isOpen = false)
interface _DataFrameType11 {
    val amount: kotlin.Double
    val orderId: kotlin.Int
}

@DataSchema
interface _DataFrameType1 {
    val orders: List<_DataFrameType11>
    val user: kotlin.String
}
```

By adding these interfaces to your project with the [compiler plugin](Compiler-Plugin.md) enabled,  
you'll gain full support for the [extension properties API](extensionPropertiesApi.md) and type-safe operations.

Use [`cast`](cast.md) to apply the generated schema to a `DataFrame`:

<!---FUN notebook_test_generate_docs_3-->

```kotlin
df.cast<_DataFrameType1>().filter { orders.all { orderId >= 102 } }
```

<!---END-->

<!--inline-frame src="./resources/notebook_test_generate_docs_3.html" width="100%" height="500px"></inline-frame>-->

## generateDataClasses

```kotlin
inline fun <reified T> DataFrame<T>.generateDataClasses(
    markerName: String? = null,
    extensionProperties: Boolean = false,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
    useFqNames: Boolean = false,
    nameNormalizer: NameNormalizer = NameNormalizer.default,
): CodeString
```

Generates Kotlin data classes corresponding to the `DataFrame` schema 
(including all nested `DataFrame` columns and column groups).

Useful when you want to:

- Work with the data as regular Kotlin data classes.
- Work with data classes serialization.
- Extract structured types for further use in your application.

### Arguments {id="generateDataClasses-arguments"}
* `markerName`: `String?` — The base name to use for generated data classes.  
  If `null`, uses the `T` type argument of `DataFrame` simple name.  
  Default: `null`.
* `extensionProperties`: `Boolean` – Whether to generate extension properties in addition to `data class` declarations.  
  Default: `false`.
* `visibility`: `MarkerVisibility` – Visibility modifier for the generated declarations.  
  Default: `MarkerVisibility.IMPLICIT_PUBLIC`.
* `useFqNames`: `Boolean` – If `true`, fully qualified type names will be used in generated code.  
  Default: `false`.
* `nameNormalizer`: `NameNormalizer` – Strategy for converting column names (with spaces, underscores, etc.) to valid Kotlin identifiers.  
  Default: `NameNormalizer.default`.

### Returns {id="generateDataClasses-returns"}
* `CodeString` – A value class wrapper for `String`, containing  
  the generated Kotlin code of `data class` declarations and optionally extension properties.

### Examples {id="generateDataClasses-examples"}

<!---FUN notebook_test_generate_docs_4-->

```kotlin
df.generateDataClasses("Customer")
```

<!---END-->

Output:
```kotlin
@DataSchema
data class Customer1(
    val amount: Double,
    val orderId: Int
)

@DataSchema
data class Customer(
    val orders: List<Customer1>,
    val user: String
)
```

Add these classes to your project and convert the DataFrame to a list of typed objects:

<!---FUN notebook_test_generate_docs_5-->

```kotlin
val customers: List<Customer> = df.cast<Customer>().toList()
```

<!---END-->

## generateCode

```kotlin
inline fun <reified T> DataFrame<T>.generateCode(
    fields: Boolean = true,
    extensionProperties: Boolean = true,
): CodeString

fun <T> DataFrame<T>.generateCode(
    markerName: String,
    fields: Boolean = true,
    extensionProperties: Boolean = true,
    visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC,
): CodeString
```

Generates a data schema interface as [`generateInterfaces()`](#generateinterfaces),  
along with explicit [extension properties](extensionPropertiesApi.md). 
Useful if you don't use the [compiler plugin](Compiler-Plugin.md).

### Arguments {id="generateCode-arguments"}
* `markerName`: `String` – The base name to use for generated interfaces. 
If not specified, uses the `T` type argument of `DataFrame` simple name.
* `fields`: `Boolean` – Whether to generate fields (`val ...`) inside interfaces. 
Default: `true`.
* `extensionProperties`: `Boolean` – Whether to generate extension properties for the schema.
Default: `true`.
* `visibility`: `MarkerVisibility` – Visibility modifier for the generated declarations.
Default: `MarkerVisibility.IMPLICIT_PUBLIC`.

### Returns {id="generateCode-returns"}
* `CodeString` – A value class wrapper for `String`, containing
the generated Kotlin code of `@DataSchema` interfaces and/or extension properties.

### Examples {id="generateCode-examples"}

<!---FUN notebook_test_generate_docs_6-->

```kotlin
df.generateCode("Customer")
```

<!---END-->

Output:
```kotlin
@DataSchema(isOpen = false)
interface Customer1 {
    val amount: kotlin.Double
    val orderId: kotlin.Int
}

val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer1>.amount: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double> @JvmName("Customer1_amount") get() = this["amount"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Double>
val org.jetbrains.kotlinx.dataframe.DataRow<Customer1>.amount: kotlin.Double @JvmName("Customer1_amount") get() = this["amount"] as kotlin.Double
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer1>.orderId: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int> @JvmName("Customer1_orderId") get() = this["orderId"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.Int>
val org.jetbrains.kotlinx.dataframe.DataRow<Customer1>.orderId: kotlin.Int @JvmName("Customer1_orderId") get() = this["orderId"] as kotlin.Int

@DataSchema
interface Customer {
    val orders: List<Customer1>
    val user: kotlin.String
}

val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.orders: org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>> @JvmName("Customer_orders") get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataColumn<org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>>
val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.orders: org.jetbrains.kotlinx.dataframe.DataFrame<Customer1> @JvmName("Customer_orders") get() = this["orders"] as org.jetbrains.kotlinx.dataframe.DataFrame<Customer1>
val org.jetbrains.kotlinx.dataframe.ColumnsContainer<Customer>.user: org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String> @JvmName("Customer_user") get() = this["user"] as org.jetbrains.kotlinx.dataframe.DataColumn<kotlin.String>
val org.jetbrains.kotlinx.dataframe.DataRow<Customer>.user: kotlin.String @JvmName("Customer_user") get() = this["user"] as kotlin.String
```

By adding this generated code to your project, you can use the [extension properties API](extensionPropertiesApi.md)
for fully type-safe column access and transformations.

Use [`cast`](cast.md) to apply the generated schema to a `DataFrame`:

<!---FUN notebook_test_generate_docs_7-->

```kotlin
df.cast<Customer>()
    .add("ordersTotal") { orders.sumOf { it.amount } }
    .filter { user.startsWith("A") }
    .rename { user }.into("customer")
```

<!---END-->

<!--inline-frame src="./resources/notebook_test_generate_docs_7.html" width="100%" height="500px"></inline-frame>-->

