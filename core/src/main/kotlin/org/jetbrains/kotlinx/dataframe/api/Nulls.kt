package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Update.UpdateOperationArg
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.*
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KProperty

// region fillNulls

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNulls` Operation Usage][Usage].
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNulls]}
 */
internal interface FillNulls {

    /** @include [Update.Usage] {@arg [UpdateOperationArg] [fillNulls][fillNulls]} */
    interface Usage
}

/** {@arg [SelectingColumns.OperationArg] [fillNulls][fillNulls]} */
private interface SetFillNullsOperationArg

/**
 * @include [FillNulls] {@comment Description of the fillNulls operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * ## This Fill Nulls Overload
 */
private interface CommonFillNullsFunctionDoc

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.DslParam]
 */
public fun <T, C> DataFrame<T>.fillNulls(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it == null }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNulls(vararg columns: String): Update<T, Any?> =
    fillNulls { columns.toColumns() }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.KPropertiesParam]
 */
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: KProperty<C>): Update<T, C?> =
    fillNulls { columns.toColumns() }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.fillNulls(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

/**
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 * In Dataframe we have helper functions to check for `NaNs`, such as [Any?.isNaN][Any.isNaN] and
 * [column.canHaveNaN][DataColumn.canHaveNaN].
 * You can also use [fillNaNs][fillNaNs] to replace `NaNs` in certain columns with a given value or expression.
 *
 * @see NA
 */
internal interface NaN

/**
 * `NA` in Dataframe can be seen as "[NaN] or `null`".
 *
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * In Dataframe we have helper functions to check for `NAs`, such as [Any?.isNA][Any.isNA] and
 * [column.canHaveNA][DataColumn.canHaveNA].
 * You can also use [fillNA][fillNA] to replace `NAs` in certain columns with a given value or expression.
 * @see NaN
 */
internal interface NA

internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

internal inline val Any?.isNA: Boolean
    get() = when (this) {
        null -> true
        is Double -> isNaN()
        is Float -> isNaN()
        is AnyRow -> allNA()
        is AnyFrame -> isEmpty()
        else -> false
    }

internal inline val AnyCol.canHaveNaN: Boolean get() = typeClass.let { it == Double::class || it == Float::class }

internal inline val AnyCol.canHaveNA: Boolean get() = hasNulls() || canHaveNaN || kind != ColumnKind.Value

internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][NaN] values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNaNs` Operation Usage][Usage].
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNaNs]}
 */
internal interface FillNaNs {

    /** @include [Update.Usage] {@arg [Update.UpdateOperationArg] [fillNaNs][fillNaNs]} */
    interface Usage
}

/** {@arg [SelectingColumns.OperationArg] [fillNaNs][fillNaNs]} */
internal interface SetFillNaNsOperationArg

/**
 * @include [FillNaNs] {@comment Description of the fillNaNs operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * ## This Fill NaNs Overload
 */
private interface CommonFillNaNsFunctionDoc

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.DslParam]
 */
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNaNs(vararg columns: String): Update<T, Any?> =
    fillNaNs { columns.toColumns() }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.KPropertiesParam]
 */
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: KProperty<C>): Update<T, C> =
    fillNaNs { columns.toColumns() }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: ColumnReference<C>): Update<T, C> =
    fillNaNs { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.fillNaNs(columns: Iterable<ColumnReference<C>>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

// endregion

// region fillNA

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][NA] values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNA` Operation Usage][Usage].
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNA]}
 */
internal interface FillNA {

    /** @include [Update.Usage] {@arg [Update.UpdateOperationArg] [fillNA][fillNA]} */
    interface Usage
}

/** {@arg [SelectingColumns.OperationArg] [fillNA][fillNA]} */
internal interface SetFillNAOperationArg

/**
 * @include [FillNA] {@comment Description of the fillNA operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * ## This Fill NA Overload
 */
private interface CommonFillNAFunctionDoc

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.DslParam]
 */
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNA(vararg columns: String): Update<T, Any?> =
    fillNA { columns.toColumns() }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.KPropertiesParam]
 */
public fun <T, C> DataFrame<T>.fillNA(vararg columns: KProperty<C>): Update<T, C?> =
    fillNA { columns.toColumns() }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
public fun <T, C> DataFrame<T>.fillNA(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNA { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.fillNA(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

// endregion

// region dropNulls

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = this[selector]
    return if (whereAllNull) drop { row -> columns.all { col -> col[row] == null } }
    else drop { row -> columns.any { col -> col[row] == null } }
}

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { all() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: Column, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(
    columns: Iterable<Column>,
    whereAllNull: Boolean = false
): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> =
    (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = this[selector]

    return if (whereAllNA) drop { columns.all { this[it].isNA } }
    else drop { columns.any { this[it].isNA } }
}

public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(vararg columns: Column, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(columns: Iterable<Column>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { all() }

public fun <T> DataColumn<T?>.dropNA(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNA }.cast()
        else -> (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>
    }

// endregion
