[//]: # (title: inferType)

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.Modify-->

Changes the type of the selected columns based on the runtime values stored in these columns.
The resulting type of the column will be the nearest common supertype of all column values.

```text
inferType [ { columns } ]
```
See [column selectors](ColumnSelectors.md) for how to select the columns for this operation.
