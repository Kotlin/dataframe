# cumProd

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.CumProdSamples-->

Computes cumulative products in a [`DataColumn`](DataColumn.md), in row order.
This is useful for multiplying growth factors, such as `1 + fractionalReturn`, to obtain compounded growth.
The result preserves the name, row count, and declared nullability. Empty columns remain empty.

| Input element type | Result element type |
|---|---|
| `Byte` / `Byte?` | `Int` / `Int?` |
| `Short` / `Short?` | `Int` / `Int?` |
| `Int` / `Int?` | `Int` / `Int?` |
| `Long` / `Long?` | `Long` / `Long?` |
| `Float` / `Float?` | `Float` / `Float?` |
| `Double` / `Double?` | `Double` / `Double?` |

Default-value metadata is preserved, with byte and short defaults promoted to `Int`.
Mixed `Number` columns, arbitrary-precision numbers, and unsigned numbers are outside this API.

With `skipNA = true` (the default), input `null` and `NaN` remain in their original positions and do not
affect subsequent products. Missing cells are not filled.

<!---FUN cumProdOnColumn-->
<!---END-->

With `skipNA = false`, the first missing input representation propagates through the remaining rows:
`null` propagates as `null`, and `NaN` propagates as `NaN`.
Nullable floating-point columns preserve `null`, unlike `cumSum`, which converts floating-point nulls to `NaN`.

<!---FUN cumProdWithoutSkipping-->
<!---END-->

Byte and short inputs are promoted before multiplication.

<!---FUN cumProdBytePromotion-->
<!---END-->

Growth factors multiply directly; subtract one from the result if a fractional cumulative return is needed.

<!---FUN cumProdGrowthFactors-->
<!---END-->

Use [`convert`](convert.md) column transformations to apply it to a dataframe.

<!---FUN cumProdWithColumnConversion-->
<!---END-->

Use [`updateGroups`](groupBy.md#transformation) to reset the running product per group.
Concatenating results collects rows in group order.

<!---FUN cumProdWithGroups-->
<!---END-->

Integer products use ordinary Kotlin multiplication and wrap on overflow; arithmetic is not checked.
Floating-point multiplication follows IEEE 754, including rounding, underflow, infinity, and signed zeros.
If multiplication itself produces `NaN`, for example infinity times zero, that accumulated `NaN` is not
skipped by `skipNA`: it affects subsequent products even when missing input values are skipped.

Dedicated dataframe/group selector overloads and arbitrary-precision products are outside this initial column API.
See also [`cumSum`](cumSum.md) for cumulative sums.
