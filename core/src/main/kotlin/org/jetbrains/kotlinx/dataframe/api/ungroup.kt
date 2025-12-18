package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Ungroups the specified [column groups][columns\] within the [DataFrame], i.e.,
 * replaces each [ColumnGroup] with its nested columns.
 *
 * See [Selecting Columns][UngroupSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Ungroup]}
 *
 * Reverse operation: [group].
 */
internal interface UngroupDocs {
    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetUngroupOperationArg]}
     */
    interface UngroupSelectingOptions
}

/** {@set [SelectingColumns.OPERATION] [ungroup][ungroup]} */
@ExcludeFromSources
private interface SetUngroupOperationArg

/**
 * {@include [UngroupDocs]}
 * ### This Ungroup Overload
 */
@ExcludeFromSources
private interface CommonUngroupDocs

/**
 * @include [CommonUngroupDocs]
 * @include [SelectingColumns.Dsl] {@include [SetUngroupOperationArg]}
 * ### Examples:
 * ```kotlin
 * df.ungroup { groupA and groupB }
 * df.ungroup { all() }
 * ```
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the column groups of this [DataFrame] to ungroup.
 */
@[Refine Interpretable("Ungroup0")]
public fun <T, C> DataFrame<T>.ungroup(columns: ColumnsSelector<T, C>): DataFrame<T> =
    move { columns.toColumnSet().colsInGroups() }
        .into { it.path.removeAt(it.path.size - 2).toPath() }

/**
 * @include [CommonUngroupDocs]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetUngroupOperationArg]}
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to ungroup.
 */
public fun <T> DataFrame<T>.ungroup(vararg columns: String): DataFrame<T> = ungroup { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.ungroup(vararg columns: AnyColumnReference): DataFrame<T> =
    ungroup { columns.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> DataFrame<T>.ungroup(vararg columns: KProperty<*>): DataFrame<T> = ungroup { columns.toColumnSet() }

// endregion
