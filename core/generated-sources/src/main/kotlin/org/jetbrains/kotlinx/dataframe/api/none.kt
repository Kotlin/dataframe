package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList

// region ColumnsSelectionDsl

public interface NoneColumnsSelectionDsl {

    /**
     * ## None Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**none**][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none]**`()`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     *
     *
     *
     */
    public interface Usage {

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
     * ### Check out: [Usage]
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
