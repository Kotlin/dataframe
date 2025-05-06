[//]: # (title: remove)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Returns [`DataFrame`](DataFrame.md) without selected columns.

```text
remove { columns }
```

See [Column Selectors](ColumnSelectors.md)

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
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Modify.remove.html"/>
<!---END-->
