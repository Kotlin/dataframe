package org.jetbrains.kotlinx.dataframe.columns

import io.kotest.matchers.shouldBe
import org.jetbrains.kotlinx.dataframe.api.indices
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.samples.api.TestBase
import org.jetbrains.kotlinx.dataframe.samples.api.name
import org.junit.Test

/**
 * Pins down how `indices()` resolves on a [ColumnGroup] from a package where neither `indices`
 * extension is declared — the shape of a user's import set.
 *
 * Both extensions used to apply to a column group (`indices` was declared on `BaseColumn` and on
 * `DataFrame`), and neither was more specific, so the call did not compile (#2067). This file must
 * not live in `org.jetbrains.kotlinx.dataframe.api`: there the same-package overload outranks the
 * explicitly imported one and is the only candidate ever considered, so the call compiles even when
 * a user's module cannot. In this package both candidates arrive through explicit imports and compete.
 */
class ColumnGroupIndicesTests : TestBase() {

    @Test
    fun `indices of a column group are reachable from a user's import set and give the row indices`() {
        // the receiver type is the point of the test: a ColumnGroup is both a BaseColumn and a DataFrame
        val group: ColumnGroup<*> = df.name
        // resolves to the DataFrame overload — a ColumnGroup is not a DataColumn
        val indices: IntRange = group.indices()
        indices shouldBe 0..6
    }
}
