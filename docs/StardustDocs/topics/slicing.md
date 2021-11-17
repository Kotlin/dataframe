[//]: # (title: Slicing)

Slicing means cutting a portion of `DataFrame` by continuous range of rows or columns.

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Access-->

## Slice rows

by row indices (including boundaries):

<!---FUN getSeveralRowsByRanges-->

```kotlin
df[1..2]
df[0..2, 4..5]
```

<!---END-->

See [slice rows](sliceRows.md) for other ways to select subset of rows.

## Slice columns

by column indices (including boundaries):

<!---FUN sliceColumnsByIndex-->

```kotlin
df.select { cols(1..3) }
```

<!---END-->

by column names:

<!---FUN sliceColumns-->
<tabs>
<tab title="Properties">

```kotlin
df.select { age..weight }
```

</tab>
<tab title="Accessors">

```kotlin
val age by column<Int>()
val weight by column<Int?>()

df.select { age..weight }
```

</tab>
<tab title="Strings">

```kotlin
df.select { "age".."weight" }
```

</tab></tabs>
<!---END-->

See [Column Selectors](ColumnSelectors.md) for other ways to select subset of columns. 
