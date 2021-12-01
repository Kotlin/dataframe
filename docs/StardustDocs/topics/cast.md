[//]: # (title: cast)
<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes type argument of `DataFrame` without changing its contents.

```kotlin
cast<T>(verify = false)
```

**Parameters:**
* `verify: Boolean = false` - when `true`, throws exception if `DataFrame` doesn't match given schema. Otherwise, just changes format type without actual data check.

Use this operation to change formal type of `DataFrame` to match expected schema and enable generated [extension properties](extensionPropertiesApi.md) for it.

```kotlin
@DataSchema
interface Person {
    val age: Int
    val name: String
}

df.cast<Person>()
```

To convert `DataFrame` columns to mtach given schema, use [`convertTo`](convertTo.md) operation.
