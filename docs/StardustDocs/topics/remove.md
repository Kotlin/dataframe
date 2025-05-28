[//]: # (title: remove)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) without selected columns.

```text
remove { columns }
```

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN remove-->
<tabs>
<tab title="Properties">

```kotlin
df.remove { name and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.remove("name", "weight")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.remove.html" width="100%"/>
<!---END-->
