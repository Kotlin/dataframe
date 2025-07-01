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
import org.jetbrains.kotlinx.dataframe.impl.api.gatherImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region gather

/**
 * Reshapes the [DataFrame] by gathering specified [columns] into two columns: keys and values
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
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [Grammar].
 */
internal interface GatherDocs {

    /**
     * ## [gather][gather] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [**`gather`**][gather]**`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`explodeLists`**][Gather.explodeLists]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`cast`**][Gather.cast]**`<T>() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`notNull`**][Gather.cast]**`() `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`where`**][Gather.where]**`  {  `**`filter: `[`RowValueFilter`][RowValueFilter]**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`mapKeys`**][Gather.mapKeys]**`  {  `**`transform: (`[`String`][String]**`) -> K } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`mapValues`**][Gather.mapValues]**`  {  `**`transform: (`**`C`**`) -> R`**` } `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`into`**][Gather.into]**`(`**`keyColumn: `[`String`][String]**`, `**`valueColumn: `[`String`][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`keysInto`**][Gather.keysInto]**`(`**`keyColumn: `[`String`][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`valuesInto`**][Gather.valuesInto]**`(`**`valueColumn: `[`String`][String]**`)`**
     */
    interface Grammar
}

/**
 * Reshapes the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] by gathering specified [columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [Gather][org.jetbrains.kotlinx.dataframe.api.Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([where][org.jetbrains.kotlinx.dataframe.api.Gather.where], [notNull][org.jetbrains.kotlinx.dataframe.api.Gather.notNull]);
 * - explode list-columns ([explodeLists][org.jetbrains.kotlinx.dataframe.api.Gather.explodeLists]);
 * - transform keys ([mapKeys][org.jetbrains.kotlinx.dataframe.api.Gather.mapKeys]) or values ([mapValues][org.jetbrains.kotlinx.dataframe.api.Gather.mapValues]);
 * - cast the value type ([cast][org.jetbrains.kotlinx.dataframe.api.Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [into][org.jetbrains.kotlinx.dataframe.api.Gather.into]
 * - [keysInto][org.jetbrains.kotlinx.dataframe.api.Gather.keysInto]
 * - [valuesInto][org.jetbrains.kotlinx.dataframe.api.Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [pivot][org.jetbrains.kotlinx.dataframe.api.pivot].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [Grammar][org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar].
 * ### This Gather Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
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
 * Reshapes the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] by gathering specified [columns] into two columns: keys and values
 * (or into just one of them).
 *
 * By default, the "key" column contains the names of the gathered columns,
 * and the "value" column holds the corresponding cell values.
 * The original columns selected for gathering are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * This function does not perform the reshaping immediately — it returns
 * a [Gather][org.jetbrains.kotlinx.dataframe.api.Gather] object, which serves as an intermediate step.
 * Before applying the final transformation, you may:
 * - filter values ([where][org.jetbrains.kotlinx.dataframe.api.Gather.where], [notNull][org.jetbrains.kotlinx.dataframe.api.Gather.notNull]);
 * - explode list-columns ([explodeLists][org.jetbrains.kotlinx.dataframe.api.Gather.explodeLists]);
 * - transform keys ([mapKeys][org.jetbrains.kotlinx.dataframe.api.Gather.mapKeys]) or values ([mapValues][org.jetbrains.kotlinx.dataframe.api.Gather.mapValues]);
 * - cast the value type ([cast][org.jetbrains.kotlinx.dataframe.api.Gather.cast]).
 *
 * Finally, reshape the DataFrame using one of the following methods:
 * - [into][org.jetbrains.kotlinx.dataframe.api.Gather.into]
 * - [keysInto][org.jetbrains.kotlinx.dataframe.api.Gather.keysInto]
 * - [valuesInto][org.jetbrains.kotlinx.dataframe.api.Gather.valuesInto]
 *
 * Each of these methods returns a new reshaped [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This operation is the reverse of [pivot][org.jetbrains.kotlinx.dataframe.api.pivot].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [Grammar][org.jetbrains.kotlinx.dataframe.api.GatherDocs.Grammar].
 * ### This Gather Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
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
 * to filter rows using a [Boolean] condition.
 *
 * It's an intermediate step; returns a new [Gather] with filtered value columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
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
 * // Gather values of all `String` columns
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
 * A special case of [Gather.where].
 *
 * It's an intermediate step; returns a new [Gather] with filtered value columns.
 *
 * For more information: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See [Grammar].
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
 * @return A new [Gather] instance with only non-null values retained.
 */
@Interpretable("GatherChangeType")
public fun <T, C, K, R> Gather<T, C?, K, R>.notNull(): Gather<T, C, K, R> = where { it != null } as Gather<T, C, K, R>

/**
 * Explodes [List] values — i.e., splits each list into individual elements,
 * creating a separate row for each element, and duplicating all other columns —
 * in the columns previously selected by [gather].
 *
 * If not all values are lists (for example, if one column contains `Double` values and
 * another contains `List<Double>`), only the list values will be exploded — non-list values remain unchanged.
 *
 * After explosion, operations like [where], [notNull], and [mapValues] are applied to individual list elements
 * rather than to the lists themselves. To enable this, the resulting type should be explicitly specified using [cast].
 *
 * This is an intermediate step; returns a new [Gather] with exploded values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [Gather] instance with exploded list values.
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
 * Explodes [List] values in the columns previously selected by [gather].
 *
 * After explosion, operations like [where], [notNull], and [mapValues] are applied to individual list elements
 * instead of the lists themselves.
 *
 * This is an intermediate step; returns a new [Gather] with exploded values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [Gather] instance with exploded list values.
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
 * Applies [transform] to the gathering keys —
 * that is, the names of the columns previously selected by [gather].
 *
 * This is an intermediate step; returns a new [Gather] with transformed keys.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [Gather] instance with transformed keys.
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
 * Applies [transform] to the values from the columns previously selected by [gather].
 *
 * This is an intermediate step; returns a new [Gather] with transformed values.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [Gather] instance with transformed values.
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
 * An intermediate class used in the [gather] operation.
 *
 * This class itself does not perform the reshaping — it serves as a transitional step
 * before specifying how to structure the gathered data.
 * It must be followed by one of the reshaping methods to produce a new [DataFrame] with the transformed layout.
 *
 * Use the following methods to complete the gathering:
 * - [into] – reshapes into both key and value columns.
 * - [keysInto] – reshapes into a single key column.
 * - [valuesInto] – reshapes into a single value column.
 *
 * You can also configure the transformation using:
 * - [where] / [notNull] – to filter gathered values.
 * - [explodeLists] – to flatten list values.
 * - [mapKeys] – to transform the generated keys.
 * - [mapValues] – to transform the gathered values.
 * - [cast] – to specify the resulting value type.
 *
 * This operation is the reverse of [pivot].
 *
 * See [Grammar][GatherDocs.Grammar] for more details.
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
     * Casts the type of values in the columns previously selected by [gather]
     * without modifying the values themselves.
     *
     * This is useful when the type cannot be automatically inferred and needs to be explicitly specified
     * for further [Gather] operations such as [filter][Gather.where], [notNull][Gather.notNull],
     * or [mapValues][Gather.mapValues].
     * It does not affect the actual content of the values —
     * only the type used for compile-time safety and transformation configuration.
     *
     * This is an intermediate step; returns a new [Gather] instance with an updated value type parameter.
     *
     * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
     *
     * See also: [Grammar].
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
     * @return A new [Gather] instance with the specified value type.
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
 * Reshapes the columns previously selected by [gather] into two new columns:
 * [keyColumn], containing the original column names, and [valueColumn], containing the corresponding cell values.
 *
 * Returns a new [DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key-value pair.
 *
 * Key and value values can be adjusted beforehand
 * using [mapKeys] and [mapValues], respectively.
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [DataFrame] with reshaped columns.
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
 * Reshapes the columns previously selected by [gather] into a new [keyColumn],
 * containing the original column names. The value column is omitted.
 *
 * Returns a new [DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated key.
 *
 * Resulting key values can be adjusted using [mapKeys].
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
 *
 * ### Example
 * ```kotlin
 * // Gather names of all columns containing "series" in their name
 * // into a single "seriesType" column, omitting the value column.
 * df.gather { cols { it.name().contains("series") } }
 *   .keysInto("seriesType")
 * ```
 * @param keyColumn The name of the column to store keys (original column names by default).
 * @return A new [DataFrame] with reshaped columns.
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
 * Reshapes the columns previously selected by [gather] into a new [valueColumn],
 * containing the original column values. The key column is omitted.
 *
 * Returns a new [DataFrame] with the reshaped structure.
 * The original gathered columns are removed from the result,
 * while all other columns remain unchanged —
 * except that their values are duplicated for each generated value.
 *
 * Resulting values can be adjusted using [mapValues].
 *
 * For more information, see: [See `gather` on the documentation website.](https://kotlin.github.io/dataframe/gather.html)
 *
 * See also: [Grammar].
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
 * @return A new [DataFrame] with reshaped columns.
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
