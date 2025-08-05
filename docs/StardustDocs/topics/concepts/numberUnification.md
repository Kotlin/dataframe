[//]: # (title: Number Unification)

Unifying numbers means converting them to a common number type without losing information.

This is currently an internal part of the library, 
but its logic implementation can be encountered in multiple places, such as
[statistics](summaryStatistics.md), and [reading JSON](read.md#read-from-json). 

The following graph shows the hierarchy of number types in Kotlin DataFrame.

<inline-frame src="resources/org.jetbrains.kotlinx.dataframe.documentation.UnifyingNumbers.Graph.html" width="100%"/>

The order is top-down from the most complex type to the simplest one.

For each number type in the graph, it holds that a number of that type can be expressed lossless by
a number of a more complex type (any of its parents).
This is either because the more complex type has a larger range or higher precision (in terms of bits).

Nullability, while not displayed in the graph, is also taken into account.
This means that `Int?` and `Float` will be unified to `Double?`.

`Nothing` is at the bottom of the graph and is the starting point in unification.
This can be interpreted as "no type" and can have no instance, while `Nothing?` can only be `null`.

> There may be parts of the library that "unify" numbers, such as [`readCsv`](read.md#column-type-inference-from-csv),
> or [`readExcel`](read.md#read-from-excel).
> However, because they rely on another library (like [Deephaven CSV](https://github.com/deephaven/deephaven-csv))
> this may behave slightly differently.

### Unified Number Type Options

There are variants of this graph that exclude some types, such as `BigDecimal` and `BigInteger`, or
allow some slightly lossy conversions, like from `Long` to `Double`.

This follows either `UnifiedNumberTypeOptions.PRIMITIVES_ONLY` or
`UnifiedNumberTypeOptions.DEFAULT`.

For `PRIMITIVES_ONLY`, used by [statistics](summaryStatistics.md), big numbers are excluded from the graph.
Additionally, `Double` is considered the most complex type,
meaning `Long`/`ULong` and `Double` can be joined to `Double`,
potentially losing a little precision(!).

For `DEFAULT`, used by [`readJson`](read.md#read-from-json), big numbers can appear.
`BigDecimal` is considered the most complex type, meaning that `Long`/`ULong` and `Double` will be joined
to `BigDecimal` instead.

