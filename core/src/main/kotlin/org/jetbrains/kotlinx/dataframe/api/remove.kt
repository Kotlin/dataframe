package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls.Select
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.OperationArg
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import kotlin.reflect.KProperty

// region DataFrame

// region remove

/**
 * ## The Remove Operation
 *
 * Removes the specified [columns] from the original [DataFrame] and returns a new [DataFrame] without them.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][Select.SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Remove]}
 */
internal interface Remove

/** {@set [SelectingColumns.OperationArg] [remove][remove]} */
@ExcludeFromSources
private interface SetRemoveOperationArg

/**
 * {@include [Remove]}
 * ### This Remove Overload
 */
@ExcludeFromSources
private interface CommonRemoveDocs

/**
 * @include [CommonRemoveDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetRemoveOperationArg]}
 * @param [columns] The [Columns Selector][ColumnsSelector] used to remove the columns of this [DataFrame].
 */
@Refine
@Interpretable("Remove0")
public fun <T> DataFrame<T>.remove(columns: ColumnsSelector<T, *>): DataFrame<T> =
    removeImpl(allowMissingColumns = true, columns = columns).df

/**
 * @include [CommonRemoveDocs]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetRemoveOperationArg]}
 * @param [columns] The [Column Names][String] used to remove the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.remove(vararg columns: String): DataFrame<T> = remove { columns.toColumnSet() }

/**
 * @include [CommonRemoveDocs]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetRemoveOperationArg]}
 * @param [columns] The [Column Accessors][ColumnReference] used to remove the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.remove(vararg columns: AnyColumnReference): DataFrame<T> = remove { columns.toColumnSet() }

/**
 * @include [CommonRemoveDocs]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetRemoveOperationArg]}
 * @param [columns] The [KProperties][KProperty] used to remove the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.remove(vararg columns: KProperty<*>): DataFrame<T> = remove { columns.toColumnSet() }

// endregion

// region minus

/**
 * ## The Minus Operation
 *
 * Removes the specified [columns] from the original [DataFrame] and returns a new [DataFrame] without them.
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][Select.SelectSelectingOptions].
 *
 * Works exactly as [remove]. For more information: {@include [DocumentationUrls.Remove]}
 */
internal interface Minus

/** {@set [SelectingColumns.OperationArg] [minus][minus]} */
@ExcludeFromSources
private interface SetMinusOperationArg

/**
 * {@include [Minus]}
 * ### This Minus Overload
 */
@ExcludeFromSources
private interface CommonMinusDocs

/**
 * @include [CommonMinusDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetMinusOperationArg]}
 * @param [columns] The [Columns Selector][ColumnsSelector] used to remove the columns of this [DataFrame].
 */
public infix operator fun <T> DataFrame<T>.minus(columns: ColumnsSelector<T, *>): DataFrame<T> = remove(columns)

/**
 * @include [CommonMinusDocs]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetMinusOperationArg]}
 * @param [columns] The [Column Names][String] used to remove the columns of this [DataFrame].
 */
public infix operator fun <T> DataFrame<T>.minus(column: String): DataFrame<T> = remove(column)

/**
 * @include [CommonMinusDocs]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetMinusOperationArg]}
 * @param [columns] The [Column Accessors][ColumnReference] used to remove the columns of this [DataFrame].
 */
public infix operator fun <T> DataFrame<T>.minus(column: AnyColumnReference): DataFrame<T> = remove(column)

/**
 * @include [CommonMinusDocs]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetMinusOperationArg]}
 * @param [columns] The [KProperties][KProperty] used to remove the columns of this [DataFrame].
 */
public infix operator fun <T> DataFrame<T>.minus(columns: KProperty<*>): DataFrame<T> = remove(columns)

// endregion

// endregion
