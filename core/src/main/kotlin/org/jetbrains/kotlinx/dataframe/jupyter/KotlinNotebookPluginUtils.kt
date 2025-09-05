package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.api.Convert
import org.jetbrains.kotlinx.dataframe.api.FormatClause
import org.jetbrains.kotlinx.dataframe.api.FormattedFrame
import org.jetbrains.kotlinx.dataframe.api.Gather
import org.jetbrains.kotlinx.dataframe.api.GroupBy
import org.jetbrains.kotlinx.dataframe.api.GroupClause
import org.jetbrains.kotlinx.dataframe.api.InsertClause
import org.jetbrains.kotlinx.dataframe.api.Merge
import org.jetbrains.kotlinx.dataframe.api.MergeWithTransform
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
import org.jetbrains.kotlinx.dataframe.api.getColumn
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.sortWith
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.valuesAreComparable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
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
    @RequiredByIntellijPlugin
    public fun getRowsSubsetForRendering(dataFrameLike: Any?, startIdx: Int, endIdx: Int): DisableRowsLimitWrapper =
        when (dataFrameLike) {
            null -> throw IllegalArgumentException("Dataframe is null")
            else -> getRowsSubsetForRendering(convertToDataFrame(dataFrameLike), startIdx, endIdx)
        }

    /**
     * Returns a subset of rows from the given dataframe for rendering.
     * It's used for example for dynamic pagination in Kotlin Notebook Plugin.
     */
    public fun getRowsSubsetForRendering(df: AnyFrame, startIdx: Int, endIdx: Int): DisableRowsLimitWrapper =
        DisableRowsLimitWrapper(df[startIdx..<endIdx])

    /**
     * Sorts a dataframe-like object by multiple columns.
     * If a column type is not comparable, sorting by string representation is applied instead.
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
    @RequiredByIntellijPlugin
    public fun sortByColumns(dataFrameLike: Any?, columnPaths: List<List<String>>, desc: List<Boolean>): AnyFrame =
        when (dataFrameLike) {
            null -> throw IllegalArgumentException("Dataframe is null")
            else -> sortByColumns(convertToDataFrame(dataFrameLike), columnPaths, desc)
        }

    /**
     * Sorts a dataframe by multiple columns with specified sorting order for each column.
     * If a column type is not comparable, sorting by string representation is applied instead.
     *
     * @param df The dataframe to be sorted.
     * @param columnPaths A list of column paths where each path is a list of strings representing the hierarchical path of the column.
     * @param isDesc A list of boolean values indicating whether each column should be sorted in descending order;
     *               true for descending, false for ascending. The size of this list should match the size of `columnPaths`.
     * @return The sorted dataframe.
     */
    public fun sortByColumns(df: AnyFrame, columnPaths: List<List<String>>, isDesc: List<Boolean>): AnyFrame {
        require(columnPaths.all { it.isNotEmpty() })
        require(columnPaths.size == isDesc.size)

        val sortKeys = columnPaths.map { path ->
            ColumnPath(path)
        }

        val comparator = createComparator(sortKeys, isDesc)

        return df.sortWith(comparator)
    }

    private fun createComparator(sortKeys: List<ColumnPath>, isDesc: List<Boolean>): Comparator<DataRow<*>> {
        return Comparator { row1, row2 ->
            for ((key, desc) in sortKeys.zip(isDesc)) {
                val column = row1.df().getColumn(key)
                val comparisonResult = if (column.valuesAreComparable()) {
                    compareComparableValues(row1, row2, key, desc)
                } else if (column.isFrameColumn()) {
                    val firstValue = column[row1].rowsCount()
                    val secondValue = column[row2].rowsCount()
                    firstValue.compare(secondValue, desc)
                } else if (column.isList()) {
                    compareListSizes(row1, row2, key, desc)
                } else {
                    compareStringValues(row1, row2, key, desc)
                }
                // If a comparison result is non-zero, we have resolved the ordering
                if (comparisonResult != 0) return@Comparator comparisonResult
            }
            // All comparisons are equal
            0
        }
    }

    private fun compareListSizes(
        row1: DataRow<*>,
        row2: DataRow<*>,
        key: ColumnPath,
        desc: Boolean,
    ): Int {
        val firstValue = (row1.getValueOrNull(key) as? List<*>)?.size ?: 0
        val secondValue = (row2.getValueOrNull(key) as? List<*>)?.size ?: 0
        return if (desc) {
            secondValue.compareTo(firstValue)
        } else {
            firstValue.compareTo(secondValue)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun compareComparableValues(
        row1: DataRow<*>,
        row2: DataRow<*>,
        key: ColumnPath,
        desc: Boolean,
    ): Int {
        val firstValue = row1.getValueOrNull(key) as Comparable<Any?>?
        val secondValue = row2.getValueOrNull(key) as Comparable<Any?>?

        return when {
            firstValue == null && secondValue == null -> 0
            firstValue == null -> if (desc) 1 else -1
            secondValue == null -> if (desc) -1 else 1
            desc -> secondValue.compareTo(firstValue)
            else -> firstValue.compareTo(secondValue)
        }
    }

    private fun compareStringValues(
        row1: DataRow<*>,
        row2: DataRow<*>,
        key: ColumnPath,
        desc: Boolean,
    ): Int {
        val firstValue = (row1.getValueOrNull(key)?.toString() ?: "")
        val secondValue = (row2.getValueOrNull(key)?.toString() ?: "")

        return if (desc) {
            secondValue.compareTo(firstValue)
        } else {
            firstValue.compareTo(secondValue)
        }
    }

    private fun <T : Comparable<T>> T.compare(other: T, desc: Boolean) = if (desc) other.compareTo(this) else this.compareTo(other)

    internal fun isDataframeConvertable(dataframeLike: Any?): Boolean =
        when (dataframeLike) {
            is Pivot<*>,
            is ReducedGroupBy<*, *>,
            is ReducedPivot<*>,
            is PivotGroupBy<*>,
            is ReducedPivotGroupBy<*>,
            is SplitWithTransform<*, *, *>,
            is Split<*, *>,
            is Merge<*, *, *>,
            is MergeWithTransform<*, *, *>,
            is Gather<*, *, *, *>,
            is Update<*, *>,
            is Convert<*, *>,
            is FormattedFrame<*>,
            is AnyCol,
            is AnyRow,
            is GroupBy<*, *>,
            is AnyFrame,
            is DisableRowsLimitWrapper,
            is MoveClause<*, *>,
            is RenameClause<*, *>,
            is ReplaceClause<*, *>,
            is GroupClause<*, *>,
            is InsertClause<*>,
            is FormatClause<*, *>,
            -> true

            else -> false
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

            is Merge<*, *, *> -> dataframeLike.into(
                generateRandomVariationOfColumnName(
                    "merged",
                    dataframeLike.df.columnNames(),
                ),
            )

            is MergeWithTransform<*, *, *> -> dataframeLike.into(
                generateRandomVariationOfColumnName(
                    "merged",
                    dataframeLike.df.columnNames(),
                ),
            )

            is Gather<*, *, *, *> -> dataframeLike.into(
                generateRandomVariationOfColumnName("key", dataframeLike.df.columnNames()),
                generateRandomVariationOfColumnName("value", dataframeLike.df.columnNames()),
            )

            is Update<*, *> -> dataframeLike.df

            is Convert<*, *> -> dataframeLike.df

            is FormattedFrame<*> -> dataframeLike.df

            is AnyFrame -> dataframeLike

            is AnyRow -> dataframeLike.toDataFrame()

            is GroupBy<*, *> -> dataframeLike.toDataFrame()

            is AnyCol -> dataFrameOf(dataframeLike)

            is DisableRowsLimitWrapper -> dataframeLike.value

            is MoveClause<*, *> -> dataframeLike.df

            is RenameClause<*, *> -> dataframeLike.df

            is ReplaceClause<*, *> -> dataframeLike.df

            is GroupClause<*, *> -> dataframeLike.into(
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
