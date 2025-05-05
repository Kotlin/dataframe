[//]: # (title: countDistinct)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns number of distinct combinations of values in selected columns of [`DataFrame`](DataFrame.md).

<!---FUN countDistinctColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.countDistinct { age and name }
```

</tab>
<tab title="Strings">

```kotlin
df.countDistinct("age", "name")
```

</tab></tabs>
<!---END-->

When `columns` are not specified, returns number of distinct rows in [`DataFrame`](DataFrame.md).

<!---FUN countDistinct-->

```kotlin
df.countDistinct()
```

<!---END-->
