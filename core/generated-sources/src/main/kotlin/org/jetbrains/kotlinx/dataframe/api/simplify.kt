package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.simplify
import org.jetbrains.kotlinx.dataframe.impl.columns.transform

// region ColumnsSelectionDsl

/**
 * ## Simplify [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface SimplifyColumnsSelectionDsl {

    /**
     * ## Simplify [<code>ColumnSet</code>][ColumnSet] Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`simplify`**</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify]**`()`**
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

        /** __`.`__[<code>**`simplify`**</code>][ColumnsSelectionDsl.simplify] */
        public typealias ColumnSetName = Nothing
    }

    /**
     * ## Simplify [<code>ColumnSet</code>][ColumnSet]
     *
     * Given a [<code>this</code>][this] [<code>ColumnSet</code>][ColumnSet], [<code>simplify</code>][simplify] simplifies the structure by removing columns that are already present in
     * column groups, returning only these groups plus columns not belonging in any of the groups.
     *
     * In other words, this means that if a column in [<code>this</code>][this] is inside another column group in [<code>this</code>][this],
     * it will not be included in the result.
     *
     * For more information: [See `simplify` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#simplify)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * ## For example:
     *
     * [<code>cols</code>][ColumnsSelectionDsl.cols]`(a, a.b, d.c).`[<code>simplify</code>][SimplifyColumnsSelectionDsl.simplify]`() == `[<code>cols</code>][ColumnsSelectionDsl.cols]`(a, d.c)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][ColumnsSelectionDsl.colsAtAnyDepth]`  { "e"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][DataColumn.name]` }.`[<code>simplify</code>][ColumnSet.simplify]`() }`
     *
     * @return A [<code>ColumnSet</code>][ColumnSet]`<`[<code>C</code>][C]`>` containing only the columns that are not inside any column group in [<code>this</code>][this].
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.simplify(): ColumnSet<C> = simplifyInternal() as ColumnSet<C>
}

/**
 * Simplifies structure by removing columns that are already present in
 * column groups in [<code>this</code>][this].
 *
 * A.k.a. it gets a sub-list of columns that are roots of the trees of columns.
 */
internal fun ColumnsResolver<*>.simplifyInternal(): ColumnSet<*> = allColumnsInternal().transform { it.simplify() }

// endregion
