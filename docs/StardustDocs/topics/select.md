[//]: # (title: Select columns)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Two ways to create [`DataFrame`](DataFrame.md) with a subset of columns:

**indexing:**

<!---FUN getColumnsByName-->
<tabs>
<tab title="Properties">

```kotlin
df[df.age, df.weight]
```

</tab>
<tab title="Strings">

```kotlin
df["age", "weight"]
```

</tab></tabs>
<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Access.getColumnsByName.html" width="100%"/>
<!---END-->

See [DataFrame indexing](indexing.md)

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

See [column selectors](ColumnSelectors.md)
