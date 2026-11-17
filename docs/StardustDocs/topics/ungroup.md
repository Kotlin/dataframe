[//]: # (title: ungroup)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Replaces [`ColumnGroup`](DataColumn.md#columngroup) with its nested columns. 

**API reference**: [`ungroup`](https://kotlin.github.io/dataframe/api-docs/core/org.jetbrains.kotlinx.dataframe.api/ungroup.html)

```text
ungroup { columns }
```

**Reverse operation:** [`group`](group.md)

See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.

<!---FUN ungroup-->

```kotlin
// name.firstName -> firstName
// name.lastName -> lastName
df.ungroup { name }
```

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.samples.api.Modify.ungroup.html" width="100%"/>
<!---END-->
