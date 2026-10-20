# rank

<!---IMPORT org.jetbrains.kotlinx.dataframe.samples.api.RankSamples-->

Assigns one-based ranks to values in a [`DataColumn`](DataColumn.md), keeping the original row order.
The result preserves the name and row count and has declared type `Double?`, even when every value is ranked.
The input's default-value metadata is cleared because it describes input values rather than ranks.
The operation sorts row indices in O(n log n) time and uses O(n) additional storage.

Values must be self-comparable. Numbers are compared without conversion, so large integers and precise decimals
are not rounded to floating point before comparison. Strings and dates are supported too.
Ties mean `compareTo` returns zero, which need not agree with `equals` for a custom comparable type.
Floating-point signed zeros follow Kotlin `Comparable` ordering and form separate comparison groups.

<!---FUN rankOnColumn-->
<!---END-->

`RankMethod` controls comparison ties. For input values `20, 10, 20, 30` in ascending order:

| Method | Tie rule | Ranks in input order |
|---|---|---|
| `Average` (default) | Mean of the positions occupied by a tie | `2.5, 1, 2.5, 4` |
| `Min` | First position occupied by a tie | `2, 1, 2, 4` |
| `Max` | Last position occupied by a tie | `3, 1, 3, 4` |
| `First` | Give tied rows distinct positions in input order | `2, 1, 3, 4` |
| `Dense` | Increment by one for each comparison group | `2, 1, 2, 3` |

Set `ascending = false` to rank larger values first. `First` still uses original row order within ties.

<!---FUN rankFirstDescending-->
<!---END-->

`RankNAOption` controls `null` and `NaN`:

| Option | Result for missing cells |
|---|---|
| `Keep` (default) | Null rank; excluded from ranking and normalization |
| `Top` | Included in a shared missing group before non-missing values |
| `Bottom` | Included in a shared missing group after non-missing values |

Top and bottom placement are independent of `ascending`. All missing cells, including both null and NaN,
belong to one comparison group when included; `First` breaks ties in that group by input order.

<!---FUN rankMissingAtTop-->
<!---END-->

With `pct = true`, ranks are divided by the number of ranked cells. For `Dense`, the denominator is instead
the number of ranked comparison groups. Included missing cells/groups count in the denominator; kept missing
cells do not. Results are fractions in `(0, 1]`, not multiplied by 100 and not SQL `percent_rank`.
An empty input remains empty. An all-missing input with `Keep` stays all-null, including with `pct = true`.

<!---FUN rankDenseFractions-->
<!---END-->

Self-comparable non-numeric values can be ranked directly.

<!---FUN rankStrings-->
<!---END-->

Use [`convert`](convert.md) to replace a selected column with its rank column.

<!---FUN rankWithColumnConversion-->
<!---END-->

Use [`updateGroups`](groupBy.md#transformation) to compute ranks and normalization independently per group.
Concatenating results collects rows in group order.

<!---FUN rankWithGroups-->
<!---END-->

Dedicated dataframe/group selector overloads and custom comparators are outside this initial column API.
