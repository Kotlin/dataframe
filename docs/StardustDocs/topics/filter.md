[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) with rows that satisfy [row condition](DataRow.md#row-conditions)

**Related operations**: [](filterRows.md)

<!---FUN filter-->
<tabs>
<tab title="Properties">

```kotlin
df.filter { age > 18 && name.firstName.startsWith("A") }
```

</tab>
<tab title="Strings">

```kotlin
df.filter { "age"<Int>() > 18 && "name"["firstName"]<String>().startsWith("A") }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/filter_properties.html" width="100%" height="500px"></inline-frame>


## filter on a DataColumn

Returns a [`DataColumn`](DataColumn.md) containing only the values that match the given predicate.

<!---FUN filterColumn-->
<tabs>
<tab title="Properties">

```kotlin
df.age.filter { it > 17 }
```

</tab>
<tab title="Strings">

```kotlin
df.age.filter { it > 17 }
```

</tab></tabs>
<!---END-->
<inline-frame src="./resources/filterColumn_properties.html" width="100%" height="500px"></inline-frame>
