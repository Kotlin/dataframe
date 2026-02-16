package org.jetbrains.kotlinx.dataframe.jupyter

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
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
import org.jetbrains.kotlinx.dataframe.api.getRows
import org.jetbrains.kotlinx.dataframe.api.into
import org.jetbrains.kotlinx.dataframe.api.isFrameColumn
import org.jetbrains.kotlinx.dataframe.api.isList
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.api.values
import org.jetbrains.kotlinx.dataframe.api.valuesAreComparable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.impl.ColumnNameGenerator
import java.util.Arrays

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
        DisableRowsLimitWrapper(df[startIdx..<endIdx], addHtml = false)

    /**
     * Sorts a dataframe-like object by multiple columns.
     * If a column type is not comparable, sorting by string representation is applied instead.
     * Sorts DataFrames by their size because looking at the smallest / biggest groups after groupBy is very popular.
     *
     * Returns "lazily materialized" dataframe, which means get, getRows, take operation must be applied to turn it to a valid sorted dataframe.
     * "lazily materialized" - after sorting 1 million of rows and given the page size = 100, a dataframe with only 100 rows is created.
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

        if (sortKeys.size == 1) {
            val column = df.getColumn(sortKeys[0])

            // Not sure how to have generic logic that would produce Comparator<Int> and Comparator<DataRow> without overhead
            // For now Comparator<DataRow> is needed for fallback case of sorting multiple columns. Although it's now impossible in UI
            // Please make sure to change both this and createColumnComparator
            val comparator: Comparator<Int> = when {
                column.valuesAreComparable() -> compareBy(nullsLast()) {
                    column[it] as Comparable<Any>?
                }

                column.isFrameColumn() -> compareBy { column[it].rowsCount() }

                column.isList() -> compareBy { (column[it] as? List<*>)?.size ?: 0 }

                else -> compareBy { column[it]?.toString() ?: "" }
            }

            val finalComparator = if (isDesc[0]) comparator.reversed() else comparator

            val permutation = Array(column.size()) { it }
            Arrays.parallelSort(permutation, finalComparator)
            return SortedDataFrameView(df, permutation.asList())
        }

        val comparator = createComparator(df, sortKeys, isDesc)

        return df.sortWithLazy(comparator)
    }

    private fun createComparator(
        df: AnyFrame,
        sortKeys: List<ColumnPath>,
        isDesc: List<Boolean>,
    ): Comparator<DataRow<*>> {
        val columnComparators = sortKeys.zip(isDesc).map { (key, desc) ->
            val column = df.getColumn(key)
            createColumnComparator(column, desc)
        }

        return when (columnComparators.size) {
            1 -> columnComparators.single()

            else -> Comparator { row1, row2 ->
                for (comparator in columnComparators) {
                    val result = comparator.compare(row1, row2)
                    // If a comparison result is non-zero, we have resolved the ordering
                    if (result != 0) return@Comparator result
                }
                // All comparisons are equal
                0
            }
        }
    }

    private fun createColumnComparator(column: AnyCol, desc: Boolean): Comparator<DataRow<*>> {
        val comparator: Comparator<DataRow<*>> = when {
            column.valuesAreComparable() -> compareBy(nullsLast()) {
                column[it] as Comparable<Any?>?
            }

            // Comparator shows a slight improvement in performance for this case
            column.isFrameColumn() -> Comparator { r1, r2 ->
                column[r1].rowsCount().compareTo(column[r2].rowsCount())
            }

            column.isList() -> compareBy { (column[it] as? List<*>)?.size ?: 0 }

            else -> compareBy { column[it]?.toString() ?: "" }
        }
        return if (desc) comparator.reversed() else comparator
    }

    private fun <T> DataFrame<T>.sortWithLazy(comparator: Comparator<DataRow<T>>): DataFrame<T> {
        val permutation = rows().sortedWith(comparator).map { it.index() }
        return SortedDataFrameView(this, permutation)
    }

    private class SortedDataFrameView<T>(private val source: DataFrame<T>, private val permutation: List<Int>) :
        DataFrame<T> by source {

        override operator fun get(index: Int): DataRow<T> = source[permutation[index]]

        override operator fun get(range: IntRange): DataFrame<T> {
            val indices = range.map { permutation[it] }
            return source.getRows(indices)
        }

        override operator fun get(indices: Iterable<Int>): DataFrame<T> {
            val mappedIndices = indices.map { permutation[it] }
            return source.getRows(mappedIndices)
        }

        override fun get(columnName: String): AnyCol = super.get(columnName)[permutation]
    }

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
