package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.Update.UPDATE_OPERATION
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.NA
import org.jetbrains.kotlinx.dataframe.documentation.NaN
import org.jetbrains.kotlinx.dataframe.documentation.`Selecting Columns`
import org.jetbrains.kotlinx.dataframe.get
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KProperty

// region fillNulls

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNulls.Grammar]
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNulls]}
 */
internal interface FillNulls {

    /** @include [Update.Grammar] {@set [UPDATE_OPERATION] [**fillNulls**][fillNulls]} */
    typealias Grammar = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetFillNullsOperationArg]}
     */
    typealias FillNullsSelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [fillNulls][fillNulls]} */
private typealias SetFillNullsOperationArg = Nothing

/**
 * @include [FillNulls] {@comment Description of the fillNulls operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * {@set [Update.Columns.SELECTING_COLUMNS] [Selecting Columns][FillNulls.FillNullsSelectingOptions]}
 * ### This Fill Nulls Overload
 *
 */
@ExcludeFromSources
private typealias CommonFillNullsFunctionDoc = Nothing

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetFillNullsOperationArg]}
 * @include [Update.DslParam]
 */
@Interpretable("FillNulls0")
public fun <T, C> DataFrame<T>.fillNulls(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it == null }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetFillNullsOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNulls(vararg columns: String): Update<T, Any?> = fillNulls { columns.toColumnSet() }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.KPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: KProperty<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

/**
 * @include [CommonFillNullsFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetFillNullsOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

/** Is only `true` if [this] is [Double.NaN] or [Float.NaN]. */
internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

/**
 * Returns `true` if [this] is considered NA.
 * "NA", in DataFrame, roughly means `null` or `NaN`.
 *
 * Overload of `isNA` with contract support.
 *
 * @see NA
 */
@JvmName("isNaWithContract")
@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
internal inline fun <T : Any?> T.isNA(): Boolean {
    contract { returns(false) implies (this@isNA != null) }
    return isNA
}

/**
 * Is `true` if [this] is considered NA.
 * "NA", in DataFrame, roughly means `null` or `NaN`.
 * @see NA
 */
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

/**
 * Is `true` when [this] column can have [NA] values.
 * @see NA
 */
internal inline val AnyCol.canHaveNA: Boolean get() = hasNulls() || canHaveNaN || kind() != ColumnKind.Value

/**
 * Is `true` when [this] is `null` or [Double.NaN].
 * @see NA
 */
internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

/**
 * Is `true` when [this] is `null` or [Float.NaN].
 * @see NA
 */
internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][NaN] values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNaNs.Grammar]
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNaNs]}
 */
internal interface FillNaNs {

    /** @include [Update.Grammar] {@set [Update.UPDATE_OPERATION] [fillNaNs][fillNaNs]} */
    typealias Grammar = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetFillNaNsOperationArg]}
     */
    typealias FillNaNsSelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [fillNaNs][fillNaNs]} */
@ExcludeFromSources
internal typealias SetFillNaNsOperationArg = Nothing

/**
 * @include [FillNaNs] {@comment Description of the fillNaNs operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * {@set [Update.Columns.SELECTING_COLUMNS] [Selecting Columns][FillNaNs.FillNaNsSelectingOptions]}
 * ### This Fill NaNs Overload
 */
@ExcludeFromSources
private typealias CommonFillNaNsFunctionDoc = Nothing

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetFillNaNsOperationArg]}
 * @include [Update.DslParam]
 */
@Interpretable("FillNaNs0")
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetFillNaNsOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNaNs(vararg columns: String): Update<T, Any?> = fillNaNs { columns.toColumnSet() }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.KPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: KProperty<C>): Update<T, C> = fillNaNs { columns.toColumnSet() }

/**
 * @include [CommonFillNaNsFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetFillNaNsOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: ColumnReference<C>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

// endregion

// region fillNA

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][NA] values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNA.Grammar]
 *
 * For more information: {@include [DocumentationUrls.Fill.FillNA]}
 */
internal interface FillNA {

    /** @include [Update.Grammar] {@set [Update.UPDATE_OPERATION] [fillNA][fillNA]} */
    typealias Grammar = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetFillNAOperationArg]}
     */
    typealias FillNASelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [fillNA][fillNA]} */
@ExcludeFromSources
internal typealias SetFillNAOperationArg = Nothing

/**
 * @include [FillNA] {@comment Description of the fillNA operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * {@set [Update.Columns.SELECTING_COLUMNS] [Selecting Columns][FillNA.FillNASelectingOptions]}
 * ### This Fill NA Overload
 */
@ExcludeFromSources
private typealias CommonFillNAFunctionDoc = Nothing

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetFillNAOperationArg]}
 * @include [Update.DslParam]
 */
@Interpretable("FillNulls0") // fillNA changes schema same as fillNulls
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetFillNAOperationArg]}
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.fillNA(vararg columns: String): Update<T, Any?> = fillNA { columns.toColumnSet() }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.KPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNA(vararg columns: KProperty<C>): Update<T, C?> = fillNA { columns.toColumnSet() }

/**
 * @include [CommonFillNAFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetFillNAOperationArg]}
 * @include [Update.ColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNA(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

// endregion

/** @param columns The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to drop rows in. */
@ExcludeFromSources
private typealias DropDslParam = Nothing

/** @param columns The [KProperties][KProperty] used to select the columns of this [DataFrame] to drop rows in. */
@ExcludeFromSources
private typealias DropKPropertiesParam = Nothing

/** @param columns The [Strings][String] corresponding to the names of columns in this [DataFrame] to drop rows in. */
@ExcludeFromSources
private typealias DropColumnNamesParam = Nothing

/** @param columns The [Column References][ColumnReference] used to select the columns of this [DataFrame] to drop rows in. */
@ExcludeFromSources
private typealias DropColumnAccessorsParam = Nothing
// region dropNulls

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: {@include [DocumentationUrls.Drop.DropNulls]}
 */
@ExcludeFromSources
internal interface DropNulls {

    /**
     * @param whereAllNull `false` by default.
     *   If `true`, rows are dropped if all selected cells are `null`.
     *   If `false`, rows are dropped if any of the selected cells is `null`.
     */
    typealias WhereAllNullParam = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetDropNullsOperationArg]}
     */
    typealias DropNullsSelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [dropNulls][dropNulls]} */
@ExcludeFromSources
private typealias SetDropNullsOperationArg = Nothing

/**
 * @include [DropNulls] {@comment Description of the dropNulls operation.}
 * ### This Drop Nulls Overload
 */
@ExcludeFromSources
private typealias CommonDropNullsFunctionDoc = Nothing

/**
 * @include [CommonDropNullsFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetDropNullsOperationArg]}
 * `df.`[dropNulls][dropNulls]`(whereAllNull = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @include [DropNulls.WhereAllNullParam]
 * @include [DropDslParam]
 */
@Refine
@Interpretable("DropNulls0")
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNull) {
        drop { row -> cols.all { col -> col[row] == null } }
    } else {
        drop { row -> cols.any { col -> col[row] == null } }
    }
}

@Refine
@Interpretable("DropNulls0")
@Deprecated(
    "DataFrame conventional name for filterNot* functions is drop*",
    ReplaceWith("dropNulls(columns = columns)"),
    DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.filterNotNull(columns: ColumnsSelector<T, *>): DataFrame<T> = dropNulls(columns = columns)

/**
 * @include [CommonDropNullsFunctionDoc]
 * This overload operates on all columns in the [DataFrame].
 * @include [DropNulls.WhereAllNullParam]
 */
@Refine
@Interpretable("DropNulls1")
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { all() }

/**
 * @include [CommonDropNullsFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetDropNullsOperationArg]}
 * `df.`[dropNulls][dropNulls]`(Person::length, whereAllNull = true)`
 * @include [DropNulls.WhereAllNullParam]
 * @include [DropKPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * @include [CommonDropNullsFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetDropNullsOperationArg]}
 * `df.`[dropNulls][dropNulls]`("length", whereAllNull = true)`
 * @include [DropNulls.WhereAllNullParam]
 * @include [DropColumnNamesParam]
 */
public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * @include [CommonDropNullsFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetDropNullsOperationArg]}
 * `df.`[dropNulls][dropNulls]`(length, whereAllNull = true)`
 * @include [DropNulls.WhereAllNullParam]
 * @include [DropColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: AnyColumnReference, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes `null` values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> =
    (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][NA] values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][NA].
 *
 * For more information: {@include [DocumentationUrls.Drop.DropNA]}
 */
@ExcludeFromSources
internal interface DropNA {

    /**
     * @param whereAllNA `false` by default.
     *   If `true`, rows are dropped if all selected cells are [`NA`][NA].
     *   If `false`, rows are dropped if any of the selected cells is [`NA`][NA].
     */
    typealias WhereAllNAParam = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetDropNAOperationArg]}
     */
    typealias DropNASelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [dropNA][dropNA]} */
@ExcludeFromSources
private typealias SetDropNAOperationArg = Nothing

/**
 * @include [DropNA] {@comment Description of the dropNA operation.}
 * ### This Drop NA Overload
 */
@ExcludeFromSources
private typealias CommonDropNAFunctionDoc = Nothing

/**
 * @include [CommonDropNAFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetDropNAOperationArg]}
 * `df.`[dropNA][dropNA]`(whereAllNA = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @include [DropNA.WhereAllNAParam]
 * @include [DropDslParam]
 */
@Refine
@Interpretable("DropNa0")
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNA) {
        drop { cols.all { this[it].isNA } }
    } else {
        drop { cols.any { this[it].isNA } }
    }
}

/**
 * @include [CommonDropNAFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetDropNAOperationArg]}
 * `df.`[dropNA][dropNA]`(Person::length, whereAllNA = true)`
 * @include [DropNA.WhereAllNAParam]
 * @include [DropKPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * @include [CommonDropNAFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetDropNAOperationArg]}
 * `df.`[dropNA][dropNA]`("length", whereAllNA = true)`
 * @include [DropNA.WhereAllNAParam]
 * @include [DropColumnNamesParam]
 */
public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * @include [CommonDropNAFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetDropNAOperationArg]}
 * `df.`[dropNA][dropNA]`(length, whereAllNA = true)`
 * @include [DropNA.WhereAllNAParam]
 * @include [DropColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: AnyColumnReference, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * @include [CommonDropNAFunctionDoc]
 * This overload operates on all columns in the [DataFrame].
 * @include [DropNA.WhereAllNAParam]
 */
@Refine
@Interpretable("DropNa1")
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { all() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes [`NA`][NA] values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T?>.dropNA(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNA }.cast()
        else -> (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>
    }

// endregion

// region dropNaNs

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: {@include [DocumentationUrls.Drop.DropNaNs]}
 */
@ExcludeFromSources
internal interface DropNaNs {

    /**
     * @param whereAllNaN `false` by default.
     *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
     *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
     */
    typealias WhereAllNaNParam = Nothing

    /**
     * {@comment Version of [`Selecting Columns`] with correctly filled in examples}
     * @include [`Selecting Columns`] {@include [SetDropNaNsOperationArg]}
     */
    typealias DropNaNsSelectingOptions = Nothing
}

/** {@set [`Selecting Columns`.OPERATION] [dropNaNs][dropNaNs]} */
@ExcludeFromSources
private typealias SetDropNaNsOperationArg = Nothing

/**
 * @include [DropNaNs] {@comment Description of the dropNaNs operation.}
 * ### This Drop NaNs Overload
 */
@ExcludeFromSources
private typealias CommonDropNaNsFunctionDoc = Nothing

/**
 * @include [CommonDropNaNsFunctionDoc]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetDropNaNsOperationArg]}
 * `df.`[dropNaNs][dropNaNs]`(whereAllNaN = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @include [DropNaNs.WhereAllNaNParam]
 * @include [DropDslParam]
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNaN) {
        drop { cols.all { this[it].isNaN } }
    } else {
        drop { cols.any { this[it].isNaN } }
    }
}

/**
 * @include [CommonDropNaNsFunctionDoc]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetDropNaNsOperationArg]}
 * `df.`[dropNaNs][dropNaNs]`(Person::length, whereAllNaN = true)`
 * @include [DropNaNs.WhereAllNaNParam]
 * @include [DropKPropertiesParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: KProperty<*>, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * @include [CommonDropNaNsFunctionDoc]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetDropNaNsOperationArg]}
 * `df.`[dropNaNs][dropNaNs]`("length", whereAllNaN = true)`
 * @include [DropNaNs.WhereAllNaNParam]
 * @include [DropColumnNamesParam]
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: String, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * @include [CommonDropNaNsFunctionDoc]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetDropNaNsOperationArg]}
 * `df.`[dropNaNs][dropNaNs]`(length, whereAllNaN = true)`
 * @include [DropNaNs.WhereAllNaNParam]
 * @include [DropColumnAccessorsParam]
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: AnyColumnReference, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * @include [CommonDropNaNsFunctionDoc]
 * This overload operates on all columns in the [DataFrame].
 * @include [DropNaNs.WhereAllNaNParam]
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false): DataFrame<T> = dropNaNs(whereAllNaN) { all() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes [`NaN`][NaN] values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T>.dropNaNs(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNaN }.cast()
        else -> this
    }

// endregion
