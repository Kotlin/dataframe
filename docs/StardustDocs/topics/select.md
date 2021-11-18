[//]: # (title: Select columns)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Two ways to create `DataFrame` with a subset of columns:

* [`indexing`](indexing.md)

<!---FUN getColumnsByName-->
<tabs>
<tab title="Properties">

```kotlin
df[df.age, df.weight]
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df[age, weight]
```

</tab>
<tab title="Strings">

```kotlin
df["age", "weight"]
```

</tab></tabs>
<!---END-->

* [`selecting`](ColumnSelectors.md)

<!---FUN select-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age and weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df.select { age and weight }
df.select(age, weight)
```

</tab>
<tab title="Strings">

```kotlin
df.select { "age" and "weight" }
df.select("age", "weight")
```

</tab></tabs>
<!---END-->

See [column selectors](ColumnSelectors.md)
