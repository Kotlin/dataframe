package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList

// region ColumnsSelectionDsl

public interface NoneColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    /**
     * ## None
     * Creates an empty [ColumnsResolver], essentially selecting no columns at all.
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
