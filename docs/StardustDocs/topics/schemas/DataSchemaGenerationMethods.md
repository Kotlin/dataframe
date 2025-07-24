# Data Schemas Generation From Existing DataFrame

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
- Convert a dataframe to instantiated data classes with `df.toListOf<DataClassType>()`.
- Work with data classes serialization.
- Extract structured types for further use in your application.

### Arguments {id="generateDataClasses-arguments"}

* `markerName`: `String?` — The base name to use for generated data classes.  
  If `null`, uses the `T` type argument of `DataFrame` simple name.  
  Default: `null`.
* `extensionProperties`: `Boolean` – Whether to generate [extension properties](extensionPropertiesApi.md)
  in addition to `interface` declarations.  
  Useful if you don't use the [compiler plugin](Compiler-Plugin.md), otherwise they are not needed;
  the [compiler plugin](Compiler-Plugin.md), [notebooks](gettingStartedKotlinNotebook.md),
  and older [Gradle/KSP plugin](schemasGradle.md) generate them automatically.
  Default: `false`.
* `visibility`: `MarkerVisibility` – Visibility modifier for the generated declarations.  
  Default: `MarkerVisibility.IMPLICIT_PUBLIC`.
* `useFqNames`: `Boolean` – If `true`, fully qualified type names will be used in generated code.  
  Default: `false`.
* `nameNormalizer`: `NameNormalizer` – Strategy for converting column names (with spaces, underscores, etc.) to
  Kotlin-style identifiers.
  Generated properties will still refer to columns by their actual name using the `@ColumnName` annotation.
  Default: `NameNormalizer.default`.

### Returns {id="generateDataClasses-returns"}

* `CodeString` – A value class wrapper for `String`, containing  
  the generated Kotlin code of `data class` declarations and optionally [extension properties](extensionPropertiesApi.md).

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

* `markerName`: `String?` — The base name to use for generated interfaces.  
  If `null`, uses the `T` type argument of `DataFrame` simple name.  
  Default: `null`.
* `extensionProperties`: `Boolean` – Whether to generate [extension properties](extensionPropertiesApi.md)
  in addition to `interface` declarations.  
  Useful if you don't use the [compiler plugin](Compiler-Plugin.md), otherwise they are not needed;
  the [compiler plugin](Compiler-Plugin.md), [notebooks](gettingStartedKotlinNotebook.md),
  and older [Gradle/KSP plugin](schemasGradle.md) generate them automatically.
  Default: `false`.
* `visibility`: `MarkerVisibility` – Visibility modifier for the generated declarations.  
  Default: `MarkerVisibility.IMPLICIT_PUBLIC`.
* `useFqNames`: `Boolean` – If `true`, fully qualified type names will be used in generated code.  
  Default: `false`.
* `nameNormalizer`: `NameNormalizer` – Strategy for converting column names (with spaces, underscores, etc.) to
  Kotlin-style identifiers.
  Generated properties will still refer to columns by their actual name using the `@ColumnName` annotation.
  Default: `NameNormalizer.default`.

### Returns {id="generateInterfaces-returns"}

* `CodeString` – A value class wrapper for `String`, containing  
  the generated Kotlin code of `@DataSchema` interfaces without [extension properties](extensionPropertiesApi.md).

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


