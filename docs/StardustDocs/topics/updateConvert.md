[//]: # (title: Update / convert values)

Both [`update`](update.md) and [`convert`](convert.md) can be used to change column values in a [`DataFrame`](DataFrame.md).

Difference between these operations:
* [`convert`](convert.md) allows changing the type of the column, [`update`](update.md) doesn't
* [`parse`](parse.md) is a special case of [`convert`](convert.md) to convert String into different types automatically based on its contents
* [`unfold`](unfold.md) is a special case of [`convert`](convert.md) to convert [`DataColumn`](DataColumn.md) of objects into [`ColumnGroup`](DataColumn.md#columngroup) based on properties of objects.
* [`update`](update.md) allows filtering cells to be updated, [`convert`](convert.md) doesn't
* [`fill`](fill.md) is a special case of [`update`](update.md) to replace missing values
