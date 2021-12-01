[//]: # (title: distinctCount)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Returns number of distinct combinations of values in selected columns of `DataFrame`.

<!---FUN countDistinctColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.ndistinct { age and name }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
df.ndistinct { age and name }
```

</tab>
<tab title="Strings">

```kotlin
df.ndistinct("age", "name")
```

</tab></tabs>
<!---END-->

When `columns` are not specified, returns number of distinct rows in `DataFrame`.

<!---FUN countDistinct-->
<tabs>
<tab title="Properties">

```kotlin
df.ndistinct()
```

</tab></tabs>
<!---END-->
