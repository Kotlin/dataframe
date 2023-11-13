package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormatClause
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupClause
import org.jetbrains.kotlinx.dataframe.api.InsertClause
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.MoveClause
import org.jetbrains.kotlinx.dataframe.api.Pivot
import org.jetbrains.kotlinx.dataframe.api.PivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedGroupBy
import org.jetbrains.kotlinx.dataframe.api.ReducedPivot
import org.jetbrains.kotlinx.dataframe.api.ReducedPivotGroupBy
import org.jetbrains.kotlinx.dataframe.api.RenameClause
import org.jetbrains.kotlinx.dataframe.api.ReplaceClause
import org.jetbrains.kotlinx.dataframe.api.Split
import org.jetbrains.kotlinx.dataframe.api.SplitWithTransform
import org.jetbrains.kotlinx.dataframe.api.Update
import org.jetbrains.kotlinx.dataframe.api.at
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import kotlin.random.Random


/**
 * A class with utility methods for Kotlin Notebook Plugin integration.
 * Kotlin Notebook Plugin is acts as a client of Kotlin Jupyter kernel and use this functionality
 * for dynamic pagination when rendering dataframes.
 * The plugin sends Kotlin following code to the kernel to evaluate
 * DISPLAY(KotlinNotebooksPluginUtils.getRowsSubsetForRendering(Out[x], 0, 20), "")
 */
public object KotlinNotebookPluginUtils {
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
    /**
     * Converts [dataframeLike] to [AnyFrame].
     * If [dataframeLike] is already [AnyFrame] then it is returned as is.
     * If it's not possible to convert [dataframeLike] to [AnyFrame] then [IllegalArgumentException] is thrown.
     */
    public fun convertToDataFrame(dataframeLike: Any): AnyFrame =
        when (dataframeLike) {
            is Pivot<*> -> dataframeLike.frames().toDataFrame()
            is ReducedGroupBy<*, *> -> dataframeLike.values()
            is ReducedPivot<*> -> dataframeLike.values().toDataFrame()
            is PivotGroupBy<*> -> dataframeLike.frames()
            is ReducedPivotGroupBy<*> -> dataframeLike.values()
            is SplitWithTransform<*, *, *> -> dataframeLike.into()
            is Split<*, *> -> dataframeLike.toDataFrame()
            is Merge<*, *, *> -> dataframeLike.into(generateRandomVariationOfString("merged"))
            is Gather<*, *, *, *> -> dataframeLike.into(
                generateRandomVariationOfString("key"),
                generateRandomVariationOfString("value")
            )
            is Update<*, *> -> dataframeLike.df
            is Convert<*, *> -> dataframeLike.df
            is FormattedFrame<*> -> dataframeLike.df
            is AnyCol -> dataFrameOf(dataframeLike)
            is AnyRow -> dataframeLike.toDataFrame()
            is GroupBy<*, *> -> dataframeLike.toDataFrame()
            is AnyFrame -> dataframeLike
            is DisableRowsLimitWrapper -> dataframeLike.value
            is MoveClause<*, *> -> dataframeLike.df
            is RenameClause<*, *> -> dataframeLike.df
            is ReplaceClause<*, *> -> dataframeLike.df
            is GroupClause<*, *> -> dataframeLike.into(generateRandomVariationOfString("untitled"))
            is InsertClause<*> -> dataframeLike.at(0)
            is FormatClause<*, *> -> dataframeLike.df
            else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
        }

    /**
     * Generates a random variation of the given string by appending a unique hash to it.
     *
     * @param str the original string to generate variation from
     * @return a random variation of the original string
     */
    public fun generateRandomVariationOfString(str: String): String {
        val timeStamp = System.currentTimeMillis()
        val random = Random.Default.nextInt()
        val hash = "${timeStamp}_$random".hashCode()

        return "${str}_${String.format("%08X", hash)}" // get only 8 symbols from hash
    }
}
