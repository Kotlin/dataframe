[//]: # (title: Get columns)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

Get single column by column name:

<!---FUN getColumnByName-->
<tabs>
<tab title="Properties">

```kotlin
df.age
df.name.lastName
```

</tab>
<tab title="Strings">

```kotlin
df["age"]
df["name"]["firstName"]
```

</tab></tabs>
<!---END-->

Get single column by index (starting from 0):

<!---FUN getColumnByIndex-->

```kotlin
df.getColumn(2)
df.getColumnGroup(0).getColumn(1)
```

<!---END-->
