[//]: # (title: Select columns)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Two ways to create [`DataFrame`](DataFrame.md) with a subset of columns:

**API reference**: [`select`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/select.html)

**selecting:**

<!---FUN select-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age and weight }
```

</tab>
<tab title="Strings">

```kotlin
df.select { "age" and "weight" }
df.select("age", "weight")
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.select.html" width="100%"/>
<!---END-->

**Related operations**: [remove](remove.md)

See [column selectors](ColumnSelectors.md)

**indexing:**

<!---FUN getColumnsByName-->

```kotlin
df["age", "weight"]
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.getColumnsByName.html" width="100%"/>
<!---END-->

See [DataFrame indexing](indexing.md)
