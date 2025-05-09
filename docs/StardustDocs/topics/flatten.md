[//]: # (title: flatten)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) without column groupings under selected columns.

```text
flatten  [ { columns } ]
```

Columns after flattening will keep their original names. Potential column name clashes are resolved by adding minimal possible name prefix from ancestor columns.

<!---FUN flatten-->
<tabs>
<tab title="Properties">

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.flatten { name }
```

</tab>
<tab title="Strings">

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.flatten("name")
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.flatten.html"/>
<!---END-->

To remove all column groupings in [`DataFrame`](DataFrame.md), invoke `flatten` without parameters:

<!---FUN flattenAll-->

```kotlin
df.flatten()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.flattenAll.html"/>
<!---END-->
