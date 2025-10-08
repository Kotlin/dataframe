[//]: # (title: Select columns)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Two ways to create [`DataFrame`](DataFrame.md) with a subset of columns:

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
