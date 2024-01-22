package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * See [Usage].
 */
public interface ColumnRangeColumnsSelectionDsl {

    /**
     * ## Range of Columns Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `column: `[ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]` | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<*> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef] [**..**][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.rangeTo] [column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]
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
        /** [**..**][ColumnsSelectionDsl.rangeTo] */
        public interface PlainDslName
    }

    /**
     * ## Range of Columns
     * Creates a [ColumnSet] containing all columns from [this\] up to (and including) [endInclusive\].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `{@getArg [CommonRangeOfColumnsDocs.Example]}` }`
     *
     * @param [endInclusive\] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet] containing all columns from [this\] to [endInclusive\].
     * @throws [IllegalArgumentException\] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore\]
     * @see [ColumnsSelectionDsl.allAfter\]
     * @see [ColumnsSelectionDsl.allFrom\]
     * @see [ColumnsSelectionDsl.allUpTo\]
     */
    private interface CommonRangeOfColumnsDocs {

        /** Examples key */
        interface Example
    }

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn"[`..`][String.rangeTo]"toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun String.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn"[`..`][String.rangeTo]Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun String.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `"fromColumn"[`..`][String.rangeTo]toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun String.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn[`..`][KProperty.rangeTo]"toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn[`..`][KProperty.rangeTo]Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `Type::fromColumn[`..`][KProperty.rangeTo]toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun KProperty<*>.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn[`..`][ColumnReference.rangeTo]"toColumn"` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: String): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn[`..`][ColumnReference.rangeTo]Type::toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside of column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Usage]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `fromColumn[`..`][ColumnReference.rangeTo]toColumn` }`
     *
     * @param [endInclusive] The last column in the subset.
     * @receiver The first column in the subset.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] to [endInclusive].
     * @throws [IllegalArgumentException] if the columns have different parents or the end column is before the
     *   start column.
     * @see [ColumnsSelectionDsl.allBefore]
     * @see [ColumnsSelectionDsl.allAfter]
     * @see [ColumnsSelectionDsl.allFrom]
     * @see [ColumnsSelectionDsl.allUpTo]
     *
     */
    public operator fun AnyColumnReference.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        createColumnSet { context ->
            val startPath = this@rangeTo.resolveSingle(context)!!.path
            val endPath = endInclusive.resolveSingle(context)!!.path
            val parentPath = startPath.parent()
            val parentEndPath = endPath.parent()
            require(parentPath == parentEndPath) {
                "Start and end columns have different parent column paths: $parentPath and $parentEndPath"
            }
            val parentCol = context.df.getColumnGroup(parentPath!!)
            val startIndex = parentCol.getColumnIndex(startPath.name)
            val endIndex = parentCol.getColumnIndex(endPath.name)

            require(startIndex <= endIndex) { "End column is before start column" }

            (startIndex..endIndex).map {
                parentCol.getColumn(it).let {
                    it.addPath(parentPath + it.name)
                }
            }
        }
}

// endregion
