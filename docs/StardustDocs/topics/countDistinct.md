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
<tab title="Accessors">

```kotlin
val age by column<Int>()
val name by columnGroup()
df.countDistinct { age and name }
```

</tab>
<tab title="Strings">

```kotlin
df.countDistinct("age", "name")
```

</tab></tabs>
<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.countDistinctColumns.html"/>
<!---END-->

When `columns` are not specified, returns number of distinct rows in [`DataFrame`](DataFrame.md).

<!---FUN countDistinct-->

```kotlin
df.countDistinct()
```

<dataFrame src="org.jetbrains.kotlinx.dataframe.samples.api.Access.countDistinct.html"/>
<!---END-->
