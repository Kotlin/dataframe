package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
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
     *  ### In the plain DSL:
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
     * Creates an empty [ColumnsResolver], essentially selecting no columns at all.
     *
     * See [Usage] for how to use [none].
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
