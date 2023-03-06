package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.api.filter

/**
 * A class with utility methods for Kotlin Notebook Plugin integration.
 * Kotlin Notebook Plugin is acts as a client of Kotlin Jupyter kernel and use this functionality
 * for dynamic pagination when rendering dataframes.
 * The plugin sends Kotlin following code to the kernel to evaluate
 * DISPLAY(KotlinNotebooksPluginUtils.getRowsSubsetForRendering(Out[x], 0, 20), "")
 */
public class KotlinNotebookPluginUtils {
    public companion object {
        /**
         * Returns a subset of rows from the given dataframe for rendering.
         * It's used for example for dynamic pagination in Kotlin Notebook Plugin.
         */
        public fun getRowsSubsetForRendering(
            dataFrameLike: Any?,
            startIdx: Int,
            endIdx: Int
        ): DisableRowsLimitWrapper = when (dataFrameLike) {
            null -> throw IllegalArgumentException("Dataframe is null")
            else -> getRowsSubsetForRendering(convertToDataFrame(dataFrameLike), startIdx, endIdx)
        }

        /**
         * Returns a subset of rows from the given dataframe for rendering.
         * It's used for example for dynamic pagination in Kotlin Notebook Plugin.
         */
        public fun getRowsSubsetForRendering(df: AnyFrame, startIdx: Int, endIdx: Int): DisableRowsLimitWrapper =
            DisableRowsLimitWrapper(df.filter { it.index() in startIdx until endIdx })
    }
}
