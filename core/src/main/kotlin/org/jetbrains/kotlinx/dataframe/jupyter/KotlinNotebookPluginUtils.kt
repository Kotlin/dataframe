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
import org.jetbrains.kotlinx.dataframe.api.frames
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.sortBy
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator

/**
 * A class with utility methods for Kotlin Notebook Plugin integration.
 * Kotlin Notebook Plugin acts as a client of Kotlin Jupyter kernel and uses this functionality
 * for dynamic pagination when rendering dataframes.
 * The plugin sends the following code to the kernel to evaluate:
 * DISPLAY(KotlinNotebooksPluginUtils.getRowsSubsetForRendering(Out[...], 0, 20), "")
 */
public object KotlinNotebookPluginUtils {
    private const val KTNB_IDE_BUILD_PROP = "KTNB_IDE_BUILD_NUMBER"

    /**
     * Returns a subset of rows from the given dataframe for rendering.
     * It's used for example for dynamic pagination in Kotlin Notebook Plugin.
     */
    public fun getRowsSubsetForRendering(
        dataFrameLike: Any?,
        startIdx: Int,
        endIdx: Int,
    ): DisableRowsLimitWrapper =
        when (dataFrameLike) {
            null -> throw IllegalArgumentException("Dataframe is null")
            else -> getRowsSubsetForRendering(convertToDataFrame(dataFrameLike), startIdx, endIdx)
        }

    /**
     * Returns a subset of rows from the given dataframe for rendering.
     * It's used for example for dynamic pagination in Kotlin Notebook Plugin.
     */
    public fun getRowsSubsetForRendering(
        df: AnyFrame,
        startIdx: Int,
        endIdx: Int,
    ): DisableRowsLimitWrapper = DisableRowsLimitWrapper(df[startIdx..<endIdx])

    /**
     * Sorts a dataframe-like object by multiple columns.
     *
     * @param dataFrameLike The dataframe-like object to sort.
     * @param columnPaths The list of columns to sort by. Each element in the list represents a column path
     * @param desc The list of booleans indicating whether each column should be sorted in descending order.
     *             The size of this list should be the same as the size of the `columns` list.
     *
     * @throws IllegalArgumentException if `dataFrameLike` is `null`.
     *
     * @return The sorted dataframe.
     */
    public fun sortByColumns(
        dataFrameLike: Any?,
        columnPaths: List<List<String>>,
        desc: List<Boolean>,
    ): AnyFrame =
        when (dataFrameLike) {
            null -> throw IllegalArgumentException("Dataframe is null")
            else -> sortByColumns(convertToDataFrame(dataFrameLike), columnPaths, desc)
        }

    /**
     * Sorts the given data frame by the specified columns.
     *
     * @param df The data frame to be sorted.
     * @param columnPaths The paths of the columns to be sorted. Each path is represented as a list of strings.
     * @param isDesc A list of booleans indicating whether each column should be sorted in descending order.
     *        The size of this list must be equal to the size of the columnPaths list.
     * @return The sorted data frame.
     */
    public fun sortByColumns(
        df: AnyFrame,
        columnPaths: List<List<String>>,
        isDesc: List<Boolean>,
    ): AnyFrame =
        df.sortBy {
            require(columnPaths.all { it.isNotEmpty() })
            require(columnPaths.size == isDesc.size)

            val sortKeys =
                columnPaths.map { path ->
                    ColumnPath(path)
                }

            (sortKeys zip isDesc)
                .map { (key, desc) ->
                    if (desc) key.desc() else key
                }.toColumnSet()
        }

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

            is Merge<*, *, *> ->
                dataframeLike.into(
                    generateRandomVariationOfColumnName(
                        "merged",
                        dataframeLike.df.columnNames(),
                    ),
                )

            is Gather<*, *, *, *> ->
                dataframeLike.into(
                    generateRandomVariationOfColumnName("key", dataframeLike.df.columnNames()),
                    generateRandomVariationOfColumnName("value", dataframeLike.df.columnNames()),
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

            is GroupClause<*, *> ->
                dataframeLike.into(
                    generateRandomVariationOfColumnName(
                        "untitled",
                        dataframeLike.df.columnNames(),
                    ),
                )

            is InsertClause<*> -> dataframeLike.at(0)

            is FormatClause<*, *> -> dataframeLike.df

            else -> throw IllegalArgumentException("Unsupported type: ${dataframeLike::class}")
        }

    /**
     * Generates a random variation of a column name that is unique among the provided used names.
     *
     * @param preferredName The preferred name for the column.
     * @param usedNames The list of already used column names.
     * @return A unique random variation of the preferred name.
     */
    public fun generateRandomVariationOfColumnName(
        preferredName: String,
        usedNames: List<String> = emptyList(),
    ): String = ColumnNameGenerator(usedNames).addUnique(preferredName)

    /**
     * Retrieves the build number of the Kotlin Notebook IDE.
     *
     * @return The build number of the Kotlin Notebook IDE as an instance of [IdeBuildNumber],
     * or null if the build number is not available.
     */
    public fun getKotlinNotebookIDEBuildNumber(): IdeBuildNumber? {
        val value = System.getProperty(KTNB_IDE_BUILD_PROP, null) ?: return null
        return IdeBuildNumber.fromString(value)
    }

    public data class IdeBuildNumber(val ideName: String, val majorVersion: Int, val buildId: Int) {
        public companion object {
            public fun fromString(buildNumber: String): IdeBuildNumber? {
                val parts = buildNumber.split(";")
                return if (parts.size >= 3) constructIdeBuildNumber(parts) else null
            }

            private fun constructIdeBuildNumber(parts: List<String>): IdeBuildNumber? {
                val ideName = parts[0]
                val majorVersion = parts[1].toIntOrNull()
                val buildId = parts[2].toIntOrNull()

                return if (majorVersion != null && buildId != null) {
                    IdeBuildNumber(ideName, majorVersion, buildId)
                } else {
                    null
                }
            }
        }
    }
}
