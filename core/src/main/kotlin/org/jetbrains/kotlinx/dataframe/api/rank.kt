package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.rankImpl

/** Determines which numerical ranks are assigned to values that compare as equal. */
public enum class RankMethod {
    /** Assign each tied value the mean of the positions occupied by the tie. */
    Average,

    /** Assign each tied value the earliest position occupied by the tie. */
    Min,

    /** Assign each tied value the last position occupied by the tie. */
    Max,

    /** Assign distinct positions to tied values in their original row order. */
    First,

    /** Give each comparison group a consecutive rank, leaving no gaps between groups. */
    Dense,
}

/** Determines whether input null and NaN are excluded from ranking or placed in a shared comparison group. */
public enum class RankNAOption {
    /** Leave missing input cells with null ranks and exclude them from normalization. */
    Keep,

    /** Place the missing group before all non-missing values, in either sort direction. */
    Top,

    /** Place the missing group after all non-missing values, in either sort direction. */
    Bottom,
}

// Adapted from the column-statistics KDoc pattern in cumSum.kt.

/**
 * Computes one-based ranks for the values in this [DataColumn] without changing row order.
 * The result retains the name and size and has declared type `Double?`, including empty columns.
 * Input default-value metadata is not carried into the rank column.
 * Sorting requires O(n log n) time and O(n) additional storage.
 *
 * Values must be self-comparable; comparison ties are defined by `compareTo` returning zero.
 * No numeric conversion is performed. Floating-point signed zeros follow [Comparable] ordering,
 * so negative and positive zero occupy different comparison groups.
 *
 * [method\] controls ties; [RankMethod.First] always uses original row order within a tie,
 * even when [ascending\] is `false`. With [RankNAOption.Keep], input `null` and `NaN` produce null ranks.
 * Otherwise, all missing input cells belong to one group at the [top][RankNAOption.Top] or
 * [bottom][RankNAOption.Bottom], independently of [ascending\].
 *
 * With [pct\] enabled, ranks are divided by the number of ranked cells, or by the number of
 * ranked comparison groups for [RankMethod.Dense]. These are fractions in `(0, 1]`, not values
 * multiplied by 100 or SQL `percent_rank`. Excluded missing cells do not count in the denominator.
 * All-missing input with [RankNAOption.Keep] remains all-null.
 *
 * @param [method\] How to handle comparison ties (default: [RankMethod.Average]).
 * @param [ascending\] Whether smaller non-missing values receive earlier ranks (default: `true`).
 * @param [naOption\] How to rank input `null` and `NaN` (default: [RankNAOption.Keep]).
 * @param [pct\] Whether to normalize the ranks to fractions (default: `false`).
 * @return A new column of nullable Double ranks in original row order.
 * @see [DataFrame.sortBy]
 * @see {@include [DocumentationUrls.RankOnColumn]}
 */
@ExcludeFromSources
private interface RankDocs

/** @include [RankDocs] */
public fun <T : Comparable<T>> DataColumn<T?>.rank(
    method: RankMethod = RankMethod.Average,
    ascending: Boolean = true,
    naOption: RankNAOption = RankNAOption.Keep,
    pct: Boolean = false,
): DataColumn<Double?> = rankImpl(method, ascending, naOption, pct)
