[//]: # (title: filter)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns [`DataFrame`](DataFrame.md) with rows that satisfy [row condition](DataRow.md#row-conditions)

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
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.filter.html" width="100%"/>
<!---END-->

## filterBy

Returns [`DataFrame`](DataFrame.md) with rows that have value `true` in the given column of type `Boolean`.

See [column selectors](ColumnSelectors.md) for how to select the column for this operation.

<!---FUN filterBy-->
<tabs>
<tab title="Properties">

```kotlin
df.filterBy { isHappy }
```

</tab>
<tab title="Strings">

```kotlin
df.filterBy("isHappy")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.filterBy.html" width="100%"/>
<!---END-->
