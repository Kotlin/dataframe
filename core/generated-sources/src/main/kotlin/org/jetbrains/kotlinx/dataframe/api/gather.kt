package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region gather

/**
 * Reshapes the [<code>DataFrame</code>][DataFrame] by gathering specified [<code>columns</code>][columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [<code>Gather</code>][Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([<code>where</code>][Gather.where], [<code>notNull</code>][Gather.notNull]);
 * - explode list-columns ([<code>explodeLists</code>][Gather.explodeLists]);
 * - transform keys ([<code>mapKeys</code>][Gather.mapKeys]) or values ([<code>mapValues</code>][Gather.mapValues]);
 * - cast the value type ([<code>cast</code>][Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [<code>into</code>][Gather.into]
 * - [<code>keysInto</code>][Gather.keysInto]
 * - [<code>valuesInto</code>][Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [<code>DataFrame</code>][DataFrame].
 *
 * This operation is the reverse of [<code>pivot</code>][pivot].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [<code>Grammar</code>][Grammar].
 */
internal interface GatherDocs {

    /**
     * ## [<code>gather</code>][gather] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`gather`**</code>][gather]**`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`explodeLists`**</code>][Gather.explodeLists]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`cast`**</code>][Gather.cast]**`<T>() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][Gather.cast]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][Gather.where]**`  {  `**`filter: `[<code>`RowValueFilter`</code>][RowValueFilter]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`mapKeys`**</code>][Gather.mapKeys]**`  {  `**`transform: (`[<code>`String`</code>][String]**`) -> K } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`mapValues`**</code>][Gather.mapValues]**`  {  `**`transform: (`**`C`**`) -> R`**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][Gather.into]**`(`**`keyColumn: `[<code>`String`</code>][String]**`, `**`valueColumn: `[<code>`String`</code>][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`keysInto`**</code>][Gather.keysInto]**`(`**`keyColumn: `[<code>`String`</code>][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`valuesInto`**</code>][Gather.valuesInto]**`(`**`valueColumn: `[<code>`String`</code>][String]**`)`**
     */
    typealias Grammar = Nothing
}

/**
 * Reshapes the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by gathering specified [<code>columns</code>][columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [<code>Gather</code>][org.jetbrains.kotlinx.dataframe.api.Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Gather.where], [<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Gather.notNull]);
 * - explode list-columns ([<code>explodeLists</code>][org.jetbrains.kotlinx.dataframe.api.Gather.explodeLists]);
 * - transform keys ([<code>mapKeys</code>][org.jetbrains.kotlinx.dataframe.api.Gather.mapKeys]) or values ([<code>mapValues</code>][org.jetbrains.kotlinx.dataframe.api.Gather.mapValues]);
 * - cast the value type ([<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.Gather.into]
 * - [<code>keysInto</code>][org.jetbrains.kotlinx.dataframe.api.Gather.keysInto]
 * - [<code>valuesInto</code>][org.jetbrains.kotlinx.dataframe.api.Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar].
 * ### This Gather Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key derived from the last letter
 * // of the corresponding original column name (i.e., 'A' or 'B').
 * df.gather { resultA and resultB }.mapKeys { it.last() }.into("series", "value")
 *
 * // Gather values of all `String` columns
 * // into a single "tag" column, omitting the key column.
 * df.gather { colsOf<String>() }.valuesInto("tag")
 * ```
 * @param [selector] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to gather.
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
 * Reshapes the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by gathering specified [<code>columns</code>][columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [<code>Gather</code>][org.jetbrains.kotlinx.dataframe.api.Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Gather.where], [<code>notNull</code>][org.jetbrains.kotlinx.dataframe.api.Gather.notNull]);
 * - explode list-columns ([<code>explodeLists</code>][org.jetbrains.kotlinx.dataframe.api.Gather.explodeLists]);
 * - transform keys ([<code>mapKeys</code>][org.jetbrains.kotlinx.dataframe.api.Gather.mapKeys]) or values ([<code>mapValues</code>][org.jetbrains.kotlinx.dataframe.api.Gather.mapValues]);
 * - cast the value type ([<code>cast</code>][org.jetbrains.kotlinx.dataframe.api.Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.Gather.into]
 * - [<code>keysInto</code>][org.jetbrains.kotlinx.dataframe.api.Gather.keysInto]
 * - [<code>valuesInto</code>][org.jetbrains.kotlinx.dataframe.api.Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [<code>pivot</code>][org.jetbrains.kotlinx.dataframe.api.pivot].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar].
 * ### This Gather Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * df.gather("resultA", "resultB").mapKeys { it.last() }.into("series", "value")
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to gather.
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
 * Filter values in columns previously selected by [<code>gather</code>][gather] using a [<code>filter</code>][RowValueFilter].
 *
 * [<code>RowValueFilter</code>][RowValueFilter] provides each value as a lambda argument, allowing you
 * to filter rows using a [<code>Boolean</code>][Boolean] condition.
 *
 * It's an intermediate step; returns a new [<code>Gather</code>][Gather] with filtered value columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [<code>Grammar</code>][Grammar].
 *
 * ### Examples
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Only values greater than `pValue` are included.
 * df.gather { resultA and resultB }.where { it >= pValue }.into("series", "value")
 *
 * // Gather values of all `String` columns
 * // into a single "tag" column, omitting the key column.
 * // Only non-empty strings are included.
 * df.gather { colsOf<String>() }.where { it.isNotEmpty() }.valuesInto("tag")
 * ```
 *
 * @param filter The [<code>RowValueFilter</code>][RowValueFilter] used to specify the filtering condition for gathered values.
 * @return A new [<code>Gather</code>][Gather] with the filtered rows.
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
 * Filters out `null` values from the columns previously selected by [<code>gather</code>][gather],
 * keeping only non-null entries.
 *
 * A special case of [<code>Gather.where</code>][Gather.where].
 *
 * It's an intermediate step; returns a new [<code>Gather</code>][Gather] with filtered value columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Assuming these columns contain nullable `Double` values, `notNull` filters out nulls,
 * // allowing subsequent transformations like `mapValues` to treat values as non-null `Double`.
 * df.gather { resultA and resultB }
 *   .notNull()
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 * @return A new [<code>Gather</code>][Gather] instance with only non-null values retained.
 */
@Interpretable("GatherChangeType")
public fun <T, C, K, R> Gather<T, C?, K, R>.notNull(): Gather<T, C, K, R> = where { it != null } as Gather<T, C, K, R>

/**
 * Explodes [<code>List</code>][List] values — i.e., splits each list into individual elements,
 * creating a separate row for each element, and duplicating all other columns —
 * in the columns previously selected by [<code>gather</code>][gather].
 *
 * If not all values are lists (for example, if one column contains `Double` values and
 * another contains `List<Double>`), only the list values will be exploded — non-list values remain unchanged.
 *
 * After explosion, operations like [<code>where</code>][where], [<code>notNull</code>][notNull], and [<code>mapValues</code>][mapValues] are applied to individual list elements
 * rather than to the lists themselves. To enable this, the resulting type should be explicitly specified using [<code>cast</code>][cast].
 *
 * This is an intermediate step; returns a new [<code>Gather</code>][Gather] with exploded values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Assuming `resultA` contains `Double` values and `
 * // resultB` contains `List<Double>` values,
 * // `explodeLists` will apply only to values from `resultB`,
 * // resulting in all gathered values being of type `Double`.
 * df.gather { resultA and resultB }
 *   .explodeLists()
 *   .cast<Double>()
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 *
 * @see [explode]
 * @return A new [<code>Gather</code>][Gather] instance with exploded list values.
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

/**
 * Explodes [<code>List</code>][List] values in the columns previously selected by [<code>gather</code>][gather].
 *
 * After explosion, operations like [<code>where</code>][where], [<code>notNull</code>][notNull], and [<code>mapValues</code>][mapValues] are applied to individual list elements
 * instead of the lists themselves.
 *
 * This is an intermediate step; returns a new [<code>Gather</code>][Gather] with exploded values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Assuming `resultA` and `resultB` contain `List<Double>` values,
 * // `explodeLists` will produce individual `Double` elements.
 * df.gather { resultA and resultB }
 *   .explodeLists()
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 *
 * @see [explode]
 * @return A new [<code>Gather</code>][Gather] instance with exploded list values.
 */
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

/**
 * Applies [<code>transform</code>][transform] to the gathering keys —
 * that is, the names of the columns previously selected by [<code>gather</code>][gather].
 *
 * This is an intermediate step; returns a new [<code>Gather</code>][Gather] with transformed keys.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key derived from the last letter
 * // of each original column name (i.e., 'A' or 'B').
 * df.gather { resultA and resultB }
 *   .mapKeys { it.last() }
 *   .into("series", "value")
 * ```
 * @return A new [<code>Gather</code>][Gather] instance with transformed keys.
 */
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

/**
 * Applies [<code>transform</code>][transform] to the values from the columns previously selected by [<code>gather</code>][gather].
 *
 * This is an intermediate step; returns a new [<code>Gather</code>][Gather] with transformed values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key.
 * // Assuming `resultA` and `resultB` contain `Double` values,
 * // `mapValues` transforms each value using the provided expression.
 * df.gather { resultA and resultB }
 *   .mapValues { (it + 0.5).toFloat() }
 *   .into("series", "value")
 * ```
 * @return A new [<code>Gather</code>][Gather] instance with transformed values.
 */
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

/**
 * An intermediate class used in the [<code>gather</code>][gather] operation.
 *
 * This class itself does not perform the reshaping — it serves as a transitional step
 * before specifying how to structure the gathered data.
 * It must be followed by one of the reshaping methods to produce a new [<code>DataFrame</code>][DataFrame] with the transformed layout.
 *
 * Use the following methods to complete the gathering:
 * - [<code>into</code>][into] – reshapes into both key and value columns.
 * - [<code>keysInto</code>][keysInto] – reshapes into a single key column.
 * - [<code>valuesInto</code>][valuesInto] – reshapes into a single value column.
 *
 * You can also configure the transformation using:
 * - [<code>where</code>][where] / [<code>notNull</code>][notNull] – to filter gathered values.
 * - [<code>explodeLists</code>][explodeLists] – to flatten list values.
 * - [<code>mapKeys</code>][mapKeys] – to transform the generated keys.
 * - [<code>mapValues</code>][mapValues] – to transform the gathered values.
 * - [<code>cast</code>][cast] – to specify the resulting value type.
 *
 * This operation is the reverse of [<code>pivot</code>][pivot].
 *
 * See [<code>Grammar</code>][GatherDocs.Grammar] for more details.
 */
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
    /**
     * Casts the type of values in the columns previously selected by [<code>gather</code>][gather]
     * without modifying the values themselves.
     *
     * This is useful when the type cannot be automatically inferred and needs to be explicitly specified
     * for further [<code>Gather</code>][Gather] operations such as [<code>filter</code>][Gather.where], [<code>notNull</code>][Gather.notNull],
     * or [<code>mapValues</code>][Gather.mapValues].
     * It does not affect the actual content of the values —
     * only the type used for compile-time safety and transformation configuration.
     *
     * This is an intermediate step; returns a new [<code>Gather</code>][Gather] instance with an updated value type parameter.
     *
     * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
     *
     * See also: [<code>Grammar</code>][Grammar].
     *
     * ### Example
     * ```kotlin
     * // Gather all subcolumns in the "results" column group into a single "value" column,
     * // with the "series" column containing a key.
     * // After `cast`, values are treated as Float in `filter` and `mapValues`.
     * df.gather { results.cols() }
     *   .cast<Float>()
     *   .filter { it > 0.05 }
     *   .mapValues { (it + 0.5f).toDouble() }
     *   .into("series", "value")
     * ```
     * @return A new [<code>Gather</code>][Gather] instance with the specified value type.
     */
    @Interpretable("GatherChangeType")
    public fun <P> cast(): Gather<T, P, K, P> {
        // TODO: introduce GatherWithTransform to avoid this error
        require(valueTransform == null) { "Cast is not allowed to be called after `mapValues`" }
        return this as Gather<T, P, K, P>
    }
}

// region into

/**
 * Reshapes the columns previously selected by [<code>gather</code>][gather] into two new columns:
 * [<code>keyColumn</code>][keyColumn], containing the original column names, and [<code>valueColumn</code>][valueColumn], containing the corresponding cell values.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * Key and value values can be adjusted beforehand
 * using [<code>mapKeys</code>][mapKeys] and [<code>mapValues</code>][mapValues], respectively.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather `resultA` and `resultB` columns into a single "value" column,
 * // with the "series" column containing a key derived from the last letter
 * // of the original column names (i.e., 'A' or 'B').
 * df.gather { resultA and resultB }
 *   .mapKeys { it.last() }
 *   .into("series", "value")
 * ```
 *
 * @param keyColumn The name of the column to store keys (original column names by default).
 * @param valueColumn The name of the column to store gathered values.
 * @return A new [<code>DataFrame</code>][DataFrame] with reshaped columns.
 */
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

/**
 * Reshapes the columns previously selected by [<code>gather</code>][gather] into a new [<code>keyColumn</code>][keyColumn],
 * containing the original column names. The value column is omitted.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key.
 *
 * Resulting key values can be adjusted using [<code>mapKeys</code>][mapKeys].
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather names of all columns containing "series" in their name
 * // into a single "seriesType" column, omitting the value column.
 * df.gather { cols { it.name().contains("series") } }
 *   .keysInto("seriesType")
 * ```
 * @param keyColumn The name of the column to store keys (original column names by default).
 * @return A new [<code>DataFrame</code>][DataFrame] with reshaped columns.
 * @see [valuesInto]
 */
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

/**
 * Reshapes the columns previously selected by [<code>gather</code>][gather] into a new [<code>valueColumn</code>][valueColumn],
 * containing the original column values. The key column is omitted.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated value.
 *
 * Resulting values can be adjusted using [<code>mapValues</code>][mapValues].
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [<code>Grammar</code>][Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather values of all `String` columns
 * // into a single "tag" column, omitting the key column.
 * df.gather { colsOf<String>() }
 *   .valuesInto("tag")
 * ```
 *
 * @param valueColumn The name of the column to store gathered values.
 * @return A new [<code>DataFrame</code>][DataFrame] with reshaped columns.
 * @see [keysInto]
 */
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
