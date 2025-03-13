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
<tab title="Accessors">

```kotlin
val name by columnGroup()
val weight by column<Int?>()

df.remove { name and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.remove("name", "weight")
```

</tab></tabs>
<!---END-->
