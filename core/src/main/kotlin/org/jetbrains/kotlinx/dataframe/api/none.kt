package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.Grammar.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList

// region ColumnsSelectionDsl

/**
 * ## None {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface NoneColumnsSelectionDsl {

    /**
     * ## None Grammar
     *
     * @include [DslGrammarTemplate]
     *
     * {@setArg [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}**`()`**
     * }
     *
     * {@setArg [DslGrammarTemplate.ColumnSetPart]}
     * {@setArg [DslGrammarTemplate.ColumnGroupPart]}
     */
    public interface Grammar {

        /** [**none**][ColumnsSelectionDsl.none] */
        public interface PlainDslName
    }

    /**
     * ## None
     *
     * Creates an empty [ColumnsResolver] / [ColumnSet], essentially selecting no columns at all.
     *
     * This is the opposite of [all][ColumnsSelectionDsl.all].
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { `[none][none]`() }`
     *
     * @return An empty [ColumnsResolver].
     */
    public fun none(): ColumnsResolver<*> = ColumnsList<Any?>(emptyList())
}

// endregion
