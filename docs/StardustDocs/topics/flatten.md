[//]: # (title: flatten)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns `DataFrame` without column groupings under selected columns

```kotlin
flatten  [ { columns } ]
```

<!---FUN flatten-->

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.flatten { name }
```

<!---END-->

Potential column name clashes are resolved by adding minimal required prefix from ancestor column names.

To remove all column groupings in `DataFrame`, invoke `flatten` without parameters:

<!---FUN flattenAll-->

```kotlin
df.flatten()
```

<!---END-->
