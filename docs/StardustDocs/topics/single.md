[//]: # (title: single)

Returns the single [row](DataRow.md) of this [`DataFrame`](DataFrame.md).
Throws an exception if the [`DataFrame`](DataFrame.md) is empty or has more than one row.

If a [condition](DataRow.md#row-conditions) is specified,
returns the single [row](DataRow.md) that matches it,
or throws exception if there is no or more than one matching row.

## singleOrNull

Returns the single [row](DataRow.md) of this [`DataFrame`](DataFrame.md),
or `null` if the [`DataFrame`](DataFrame.md) is empty or has more than one row.

If a [condition](DataRow.md#row-conditions) is specified,
returns the single [row](DataRow.md) that matches it,
or `null` if there is no or more than one matching row.
