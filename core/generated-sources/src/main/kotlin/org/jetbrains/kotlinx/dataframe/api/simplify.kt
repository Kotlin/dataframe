package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.simplify
import org.jetbrains.kotlinx.dataframe.impl.columns.transform

// region ColumnsSelectionDsl

/**
 * ## Simplify [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface SimplifyColumnsSelectionDsl {

    /**
     * ## Simplify [ColumnSet] Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`simplify`**][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify]**`()`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** __`.`__[**`simplify`**][ColumnsSelectionDsl.simplify] */
        public typealias ColumnSetName = Nothing
    }

    /**
     * ## Simplify [ColumnSet]
     *
     * Given a [this] [ColumnSet], [simplify] simplifies the structure by removing columns that are already present in
     * column groups, returning only these groups plus columns not belonging in any of the groups.
     *
     * In other words, this means that if a column in [this] is inside another column group in [this],
     * it will not be included in the result.
     *
     * ### Check out: [Grammar]
     *
     * ## For example:
     *
     * [cols][ColumnsSelectionDsl.cols]`(a, a.b, d.c).`[simplify][SimplifyColumnsSelectionDsl.simplify]`() == `[cols][ColumnsSelectionDsl.cols]`(a, d.c)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`  { "e"  `[in][String.contains]` it.`[name][DataColumn.name]` }.`[simplify][ColumnSet.simplify]`() }`
     *
     * @return A [ColumnSet][ColumnSet]`<`[C][C]`>` containing only the columns that are not inside any column group in [this].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.simplify(): ColumnSet<C> = simplifyInternal() as ColumnSet<C>
}

/**
 * Simplifies structure by removing columns that are already present in
 * column groups in [this].
 *
 * A.k.a. it gets a sub-list of columns that are roots of the trees of columns.
 */
internal fun ColumnsResolver<*>.simplifyInternal(): ColumnSet<*> = allColumnsInternal().transform { it.simplify() }

// endregion
