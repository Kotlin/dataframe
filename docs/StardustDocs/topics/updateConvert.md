[//]: # (title: Update / convert values)

Both [`update`](update.md) and [`convert`](convert.md) can be used to change column values in a `DataFrame`.

Difference between these operations:
* `convert` allows changing the type of the column, `update` doesn't
* `parse` is a special case of `convert` to convert String into different types automatically based on its contents
* `unfold` is a special case of `convert` to convert `DataColumn` of objects into `ColumnGroup` based on properties of objects.
* `update` allows filtering cells to be updated, `convert` doesn't
* [`fill`](fill.md) is a special case of `update` to replace missing values
