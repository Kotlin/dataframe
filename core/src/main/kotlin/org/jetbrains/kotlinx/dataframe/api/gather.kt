package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar
import org.jetbrains.kotlinx.dataframe.api.Update.UPDATE_OPERATION
import org.jetbrains.kotlinx.dataframe.api.notNull
import org.jetbrains.kotlinx.dataframe.api.where
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.documentation.*
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region gather

/**
 * Reshapes the [DataFrame] by gathering specified [\columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([where][Gather.where], [notNull][Gather.notNull]);
 * - explode list-columns ([explodeLists][Gather.explodeLists]);
 * - transform keys ([mapKeys][Gather.mapKeys]) or values ([mapValues][Gather.mapValues]);
 * - cast the value type ([cast][Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [into][Gather.into]
 * - [keysInto][Gather.keysInto]
 * - [valuesInto][Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [DataFrame].
 *
 * This operation is the reverse of [pivot].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * For more information: {@include [DocumentationUrls.Gather]}
 *
 * See [Grammar].
 */
internal interface GatherDocs {

    /**
     * ## {@get [GATHER_OPERATION]} Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`gather`][gather]****`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`explodeLists`**][Gather.explodeLists]**`() `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`cast`**][Gather.cast]**`<T>() `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`notNull`**][Gather.cast]**`() `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`where`**][Gather.where]**`  {  `**`filter: `[`RowValueFilter`][RowValueFilter]**` } `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`mapKeys`**][Gather.mapKeys]**`  {  `**`transform: (`[`String`][String]**`) -> K } `**`]`
     *
     * {@include [Indent]}
     * `\[ `__`.`__[**`mapValues`**][Gather.mapValues]**`  {  `**`transform: (`**`C`**`) -> R`**` } `**`]`
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][Gather.into]**`(`**`keyColumn: `[`String`][String]**`, `**`valueColumn: `[`String`][String]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`keysInto`**][Gather.keysInto]**`(`**`keyColumn: `[`String`][String]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`valuesInto`**][Gather.valuesInto]**`(`**`valueColumn: `[`String`][String]**`)`**
     * {@set [GATHER_OPERATION] [**`gather`**][gather]}{@comment The default name of the `update` operation function name.}
     */
    interface Grammar

    /*
     * This argument providing the (clickable) name of the update-like function.
     * Note: If clickable, make sure to [alias][your type].
     */
    @Suppress("ClassName")
    @ExcludeFromSources
    interface GATHER_OPERATION
}

/** {@set [SelectingColumns.OPERATION] [gather][gather]} */
@ExcludeFromSources
private interface SetGatherOperationArg

/**
 * {@include [GatherDocs]}
 * ### This Gather Overload
 */
@ExcludeFromSources
private interface CommonGatherDocs

/**
 * @include [CommonGatherDocs]
 * @include [SelectingColumns.Dsl] {@include [SetGatherOperationArg]}
 * ### Examples:
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key derived from the last letter
 * // of the corresponding original column name (i.e., 'A' or 'B').
 * df.gather { resultA and resultB }.mapKeys { it.last() }.into("series", "value")
 *
 * // Gather values of all `String` columns (at any depth)
 * // into a single "tag" column, omitting the key column.
 * df.gather { colsOf<String>() }.valuesInto("tag")
 * ```
 * @param [selector] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Gather0")
public fun <T, C> DataFrame<T>.gather(selector: ColumnsSelector<T, C>): Gather<T, C, String, C> =
    Gather(
        df = this,
        columns = selector,
        filter = null,
        keyType = typeOf<String>(),
        keyTransform = { it },
        valueTransform = null,
    )

/**
 * @include [CommonGatherDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetGatherOperationArg]}
 * ### Example:
 * ```kotlin
 * df.gather("resultA", "resultB").mapKeys { it.last() }.into("series", "value")
 * ```
 * @param [columns] The [Column Names][String] used to select the columns of this [DataFrame] to gather.
 */
public fun <T> DataFrame<T>.gather(vararg columns: String): Gather<T, Any?, String, Any?> =
    gather { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.gather(vararg columns: ColumnReference<C>): Gather<T, C, String, C> =
    gather { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.gather(vararg columns: KProperty<C>): Gather<T, C, String, C> =
    gather { columns.toColumnSet() }

// endregion

/**
 * Filter values in columns previously selected by [gather] using a [filter][RowValueFilter].
 *
 * [RowValueFilter] provides each value as a lambda argument, allowing you
 * to filter rows using a Boolean condition.
 *
 * It's an intermediate step; returns a new [Gather] with filtered value columns.
 *
 * For more information: {@include [DocumentationUrls.Gather]}
 *
 * See [Grammar].
 *
 * ### Examples
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Only values greater than `pValue` are included.
 * df.gather { resultA and resultB }.where { it >= pValue }.into("series", "value")
 *
 * // Gather values of all `String` columns (at any nesting level)
 * // into a single "tag" column, omitting the key column.
 * // Only non-empty strings are included.
 * df.gather { colsOf<String>() }.where { it.isNotEmpty() }.valuesInto("tag")
 * ```
 *
 * @param filter The [RowValueFilter] used to specify the filtering condition for gathered values.
 * @return A new [Gather] with the filtered rows.
 */
@Interpretable("GatherWhere")
public fun <T, C, K, R> Gather<T, C, K, R>.where(filter: RowValueFilter<T, C>): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = this.filter and filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = valueTransform,
        explode = explode,
    )

/**
 * Filters out `null` values from the columns previously selected by [gather],
 * keeping only non-null entries.
 *
 * A special case of [where].
 *
 * It's an intermediate step; returns a new [Gather] with filtered value columns.
 *
 * For more information: {@include [DocumentationUrls.Gather]}
 *
 * See [Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // If these columns contain nullable `Double` values, `notNull` filters out nulls,
 * // allowing subsequent transformations like `mapValues` to treat values as non-null `Double`.
 * df.gather { resultA and resultB }
 *   .notNull()
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 * @return A new [Gather] instance with only non-null values retained.
 */
@Interpretable("GatherChangeType")
public fun <T, C, K, R> Gather<T, C?, K, R>.notNull(): Gather<T, C, K, R> = where { it != null } as Gather<T, C, K, R>

/**
 * Explode values.
 *
 * A special case of [where].
 *
 * It's an intermediate step; returns a new [Gather] with filtered value columns.
 *
 * For more information: {@include [DocumentationUrls.Gather]}
 *
 * See [Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // If these columns contain nullable `Double` values, `notNull` filters out nulls,
 * // allowing subsequent transformations like `mapValues` to treat values as non-null `Double`.
 * df.gather { resultA and resultB }
 *   .notNull()
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 * @return A new [Gather] instance with only non-null values retained.
 */
@Interpretable("GatherExplodeLists")
public fun <T, C, K, R> Gather<T, C, K, R>.explodeLists(): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = valueTransform,
        explode = true,
    )

@JvmName("explodeListsTyped")
@Interpretable("GatherExplodeLists")
public fun <T, C, K, R> Gather<T, List<C>, K, R>.explodeLists(): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = valueTransform,
        explode = true,
    ) as Gather<T, C, K, R>

@Interpretable("GatherMap")
public inline fun <T, C, reified K, R> Gather<T, C, *, R>.mapKeys(
    noinline transform: (String) -> K,
): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = typeOf<K>(),
        keyTransform = transform,
        valueTransform = valueTransform,
        explode = explode,
    )

@Interpretable("GatherMap")
public fun <T, C, K, R> Gather<T, C, K, *>.mapValues(transform: (C) -> R): Gather<T, C, K, R> =
    Gather(
        df = df,
        columns = columns,
        filter = filter,
        keyType = keyType,
        keyTransform = keyTransform,
        valueTransform = transform,
        explode = explode,
    )

public class Gather<T, C, K, R>(
    @PublishedApi
    internal val df: DataFrame<T>,
    @PublishedApi
    internal val columns: ColumnsSelector<T, C>,
    @PublishedApi
    internal val filter: RowValueFilter<T, C>? = null,
    @PublishedApi
    internal val keyType: KType? = null,
    @PublishedApi
    internal val keyTransform: ((String) -> K),
    @PublishedApi
    internal val valueTransform: ((C) -> R)? = null,
    @PublishedApi
    internal val explode: Boolean = false,
) {
    @Interpretable("GatherChangeType")
    public fun <P> cast(): Gather<T, P, K, P> {
        // TODO: introduce GatherWithTransform to avoid this error
        require(valueTransform == null) { "Cast is not allowed to be called after `mapValues`" }
        return this as Gather<T, P, K, P>
    }
}

// region into

@Refine
@Interpretable("GatherInto")
public fun <T, C, K, R> Gather<T, C, K, R>.into(keyColumn: String, valueColumn: String): DataFrame<T> =
    gatherImpl(keyColumn, valueColumn)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.into(
    keyColumn: ColumnAccessor<K>,
    valueColumn: ColumnAccessor<R>,
): DataFrame<T> = into(keyColumn.name(), valueColumn.name)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.into(keyColumn: KProperty<K>, valueColumn: KProperty<R>): DataFrame<T> =
    into(keyColumn.columnName, valueColumn.columnName)

// endregion

// region keysInto

@Refine
@Interpretable("GatherKeysInto")
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: String): DataFrame<T> = gatherImpl(keyColumn, null)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: ColumnAccessor<K>): DataFrame<T> =
    keysInto(keyColumn.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.keysInto(keyColumn: KProperty<K>): DataFrame<T> =
    keysInto(keyColumn.columnName)

// endregion

// region valuesInto

@Refine
@Interpretable("GatherValuesInto")
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: String): DataFrame<T> = gatherImpl(null, valueColumn)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: ColumnAccessor<K>): DataFrame<T> =
    valuesInto(valueColumn.name())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, K, R> Gather<T, C, K, R>.valuesInto(valueColumn: KProperty<K>): DataFrame<T> =
    valuesInto(valueColumn.columnName)

// endregion
