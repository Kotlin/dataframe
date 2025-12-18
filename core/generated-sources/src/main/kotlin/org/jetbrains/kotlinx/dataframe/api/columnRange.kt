package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columns.addPath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Range of Columns [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColumnRangeColumnsSelectionDsl {

    /**
     * ## Range of Columns Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]` `[**`..`**][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.rangeTo]` `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
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
    public interface Grammar {

        /** [**`..`**][ColumnsSelectionDsl.rangeTo] */
        public interface PlainDslName
    }

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`"fromColumn"`[`..`][String.rangeTo]`"toColumn"`</code>` }`
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
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`"fromColumn"`[`..`][String.rangeTo]`Type::toColumn`</code>` }`
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
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public operator fun String.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`"fromColumn"`[`..`][String.rangeTo]`toColumn`</code>` }`
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
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`Type::fromColumn`[`..`][KProperty.rangeTo]`"toColumn"`</code>` }`
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
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public operator fun KProperty<*>.rangeTo(endInclusive: String): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`Type::fromColumn`[`..`][KProperty.rangeTo]`Type::toColumn`</code>` }`
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
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public operator fun KProperty<*>.rangeTo(endInclusive: KProperty<*>): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive.toColumnAccessor())

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`Type::fromColumn`[`..`][KProperty.rangeTo]`toColumn`</code>` }`
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
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public operator fun KProperty<*>.rangeTo(endInclusive: AnyColumnReference): ColumnSet<*> =
        toColumnAccessor().rangeTo(endInclusive)

    /**
     * ## Range of Columns
     * Creates a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns from [this] up to (and including) [endInclusive].
     *
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`fromColumn`[`..`][ColumnReference.rangeTo]`"toColumn"`</code>` }`
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
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`fromColumn`[`..`][ColumnReference.rangeTo]`Type::toColumn`</code>` }`
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
     * Columns inside column groups are also supported (as long as they share the same direct parent),
     * as well as any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `<code>`fromColumn`[`..`][ColumnReference.rangeTo]`toColumn`</code>` }`
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
    @Interpretable("ColumnRange")
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
