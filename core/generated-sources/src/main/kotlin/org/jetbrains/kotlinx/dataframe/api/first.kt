package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns the first value in this [DataColumn].
 *
 * @param T The type of the values in the [DataColumn].
 *
 * @throws [IndexOutOfBoundsException] if the [DataColumn] is empty.
 */
public fun <T> DataColumn<T>.first(): T = get(0)

/**
 * Returns the first value in this [DataColumn]. If the [DataColumn] is empty, returns `null`.
 *
 * @param T The type of the values in the [DataColumn].
 */
public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null

/**
 * Returns the first value in this [DataColumn] that matches the given [predicate].
 *
 * ### Example
 * ```kotlin
 * // Select from the column "age" the first value where the age is greater than 17
 * df.age.first { it > 17 }
 * ```
 *
 * @param T The type of the values in the [DataColumn].
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @throws [NoSuchElementException] if the [DataColumn] contains no element matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 */
public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)

/**
 * Returns the first value in this [DataColumn] that matches the given [predicate].
 * Returns `null` if the [DataColumn] contains no element matching the [predicate]
 * (including the case when the [DataColumn] is empty).
 *
 * ### Example
 * ```kotlin
 * // Select from the column "age" the first value where the age is greater than 17,
 * // or null if there is no such value
 * df.age.firstOrNull { it > 17 }
 * ```
 *
 * @param T The type of the values in the [DataColumn].
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataColumn] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 */
public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)

// endregion

// region DataFrame

/**
 * Returns the first row in this [DataFrame].
 *
 * @param T The type of the [DataFrame].
 *
 * @throws NoSuchElementException if the [DataFrame] contains no rows.
 */
public fun <T> DataFrame<T>.first(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `firstOrNull`.")
    }
    return get(0)
}

/**
 * Returns the first row in this [DataFrame]. If the [DataFrame] does not contain any rows, returns `null`.
 *
 * @param T The type of the [DataFrame].
 */
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null

/**
 * Returns the first row in this [DataFrame] that satisfies the given [predicate].
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // Select the first row where the value in the "age" column is greater than 17
 * // and the "name/firstName" column starts with 'A'
 * df.first { age > 17 && name.firstName.startsWith("A") }
 * ```
 *
 * @param T The type of the [DataFrame].
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataFrame] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return A [DataRow] containing the first row that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataFrame] contains no rows matching the [predicate].
 */
public inline fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> =
    rows().first {
        predicate(it, it)
    }

/**
 * Returns the first row in this [DataFrame] that satisfies the given [predicate].
 * Returns `null` if the [DataFrame] contains no rows matching the [predicate]
 * (including the case when the [DataFrame] is empty).
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // Select the first row where the value in the "age" column is greater than 17
 * // and the "name/firstName" column starts with 'A'
 * df.firstOrNull { age > 17 && name.firstName.startsWith("A") }
 * ```
 *
 * @param T The type of the [DataFrame].
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [DataFrame] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return A [DataRow] containing the first row that matches the given [predicate],
 * or `null` if the [DataFrame] contains no rows matching the [predicate]
 */
public inline fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rows().firstOrNull {
        predicate(it, it)
    }

// endregion

// region GroupBy

/**
 * Selects the first row from each group of the given [GroupBy]
 * and returns a [ReducedGroupBy] containing these rows
 * (one row per group, each row is the first row in its group).
 *
 * ### Example
 * ```kotlin
 * // Select the first employee from each group formed by the job title
 * employees.groupBy { jobTitle }.first()
 * ```
 *
 * @param T The type of the values in the [GroupBy].
 * @param G The type of the groups in the [GroupBy].
 *
 * @return A [ReducedGroupBy] containing the first row from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

/**
 * Selects from each group of the given [GroupBy] the first row satisfying the given [predicate],
 * and returns a [ReducedGroupBy] containing these rows (one row per group,
 * each row is the first row in its group that satisfies the [predicate]).
 *
 * If the group in [GroupBy] contains no matching rows,
 * the corresponding row in [ReducedGroupBy] will contain `null` values for all columns in the group.
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // Select the first employee older than 25 from each group formed by the job title
 * employees.groupBy { jobTitle }.first { age > 25 }
 * ```
 *
 * @param T The type of the values in the [GroupBy].
 * @param G The type of the groups in the [GroupBy].
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 * This predicate takes a value from the [GroupBy] as an input
 * and returns `true` if the value satisfies the condition or `false` otherwise.
 *
 * @return A [ReducedGroupBy] containing the first row matching the [predicate]
 * (or a row with `null` values, except values in the column with the grouping key), from each group.
 */
@Interpretable("GroupByReducePredicate")
public fun <T, G> GroupBy<T, G>.first(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { firstOrNull(predicate) }

// endregion

// region Pivot

/**
 * Reduces this [Pivot] by selecting the first row from each group.
 *
 * Returns a [ReducedPivot] where:
 * - each column corresponds to a [pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the first row from that group.
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the first row from that group.
 *
 * Equivalent to `reduce { firstOrNull() }`.
 *
 * See also:
 * - [pivot];
 * - common [reduce][Pivot.reduce].
 *
 * ### Example
 * ```kotlin
 * // Select the first row for each city.
 * // Returns a ReducedPivot with one column per city and the first row from the group in each column.
 * df.pivot { city }.first()
 * ```
 *
 * @return A [ReducedPivot] containing in each column the first row from the corresponding group.
 */
public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

/**
 * Reduces this [Pivot] by selecting from each group the first row satisfying the given [predicate].
 *
 * Returns a [ReducedPivot] where:
 * - each column corresponds to a [pivot] group — if multiple pivot keys were used,
 *   the result will contain column groups for each pivot key, with columns inside
 *   corresponding to the values of that key;
 * - each value contains the first row from that group that satisfies the [predicate],
 * or a row with `null` values if no rows in this group match the [predicate].
 *
 * The original [Pivot] column structure is preserved.
 * If the [Pivot] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the structure remains unchanged — only the contents of each group
 * are replaced with the first row from that group that satisfies the [predicate].
 *
 * Equivalent to `reduce { firstOrNull(predicate) }`.
 *
 * See also:
 * - [pivot];
 * - common [reduce][Pivot.reduce].
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // Select the first row for each city where the population is greater than 100 000.
 * df.pivot { city }.first { population > 100000 }
 * ```
 *
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 *
 * @return A [ReducedPivot] containing in each column the first row
 * that satisfies the [predicate], from the corresponding group (or a row with `null` values)
 */
public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

// endregion

// region PivotGroupBy

/**
 * Reduces this [PivotGroupBy] by selecting the first row from each combined [pivot] + [groupBy] group.
 *
 * Returns a [ReducedPivotGroupBy] containing the following matrix:
 * - one row per [groupBy] key (or keys set);
 * - one column group per [pivot] key, where each inner column corresponds to a value of that key;
 * - each combination of a [groupBy] key and a [pivot] key contains either the first row of the corresponding
 * dataframe formed by this pivot–group pair, or a row with `null` values if this dataframe is empty.
 *
 * The original [PivotGroupBy] column structure is preserved.
 * If the [PivotGroupBy] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values of the corresponding key.
 *
 * Equivalent to `reduce { firstOrNull() }`.
 *
 * See also:
 * - [pivot], [Pivot.groupBy] and [GroupBy.pivot];
 * - common [reduce][PivotGroupBy.reduce].
 *
 * ### Example
 * ```kotlin
 * // Select the first student from each combination of faculty and enrollment year.
 * students.pivot { faculty }.groupBy { enrollmentYear }.first()
 * ```
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the first row of the corresponding dataframe formed by this pivot–group pair,
 * or a row with `null` values if this dataframe is empty.
 */
public fun <T> PivotGroupBy<T>.first(): ReducedPivotGroupBy<T> = reduce { firstOrNull() }

/**
 * Reduces this [PivotGroupBy] by selecting from each combined [pivot] + [groupBy] group
 * the first row satisfying the given [predicate].
 *
 * Returns a [ReducedPivotGroupBy] containing the following matrix:
 * - one row per [groupBy] key (or keys set);
 * - one column group per [pivot] key, where each inner column corresponds to a value of that key;
 * - each combination of a [groupBy] key and a [pivot] key contains either the first matching the [predicate] row
 * of the corresponding dataframe formed by this pivot–group pair,
 * or a row with `null` values if this dataframe does not contain any rows matching the [predicate].
 *
 * The original [PivotGroupBy] column structure is preserved.
 * If the [PivotGroupBy] was created using multiple or nested keys
 * (e.g., via [and][PivotDsl.and] or [then][PivotDsl.then]),
 * the result will contain nested column groups reflecting that key structure,
 * with each group containing columns for the values of the corresponding key.
 *
 * Equivalent to `reduce { firstOrNull(predicate) }`.
 *
 * See also:
 * - [pivot], [Pivot.groupBy] and [GroupBy.pivot];
 * - common [reduce][PivotGroupBy.reduce].
 *
 * The [predicate] is a [RowFilter][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [extension properties][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs] for convenient and type-safe access.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * ### Example
 * ```kotlin
 * // From each combination of faculty and enrollment year select the first student older than 21.
 * students.pivot { faculty }.groupBy { enrollmentYear }.first { age > 21 }
 * ```
 *
 * @param predicate A lambda expression used to select a value
 * that satisfies a condition specified in this expression.
 *
 * @return A [ReducedPivotGroupBy] containing in each combination of a [groupBy] key and a [pivot] key either
 * the first matching the [predicate] row of the corresponding dataframe formed by this pivot–group pair,
 * or a row with `null` values if this dataframe does not contain any rows matching the [predicate].
 */
public fun <T> PivotGroupBy<T>.first(predicate: RowFilter<T>): ReducedPivotGroupBy<T> =
    reduce { firstOrNull(predicate) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## First (Col) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FirstColumnsSelectionDsl {

    /**
     * ## First (Col) Grammar
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
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
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
     *  [**`first`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`first`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`firstCol`**][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [**`first`**][ColumnsSelectionDsl.first] */
        public interface PlainDslName

        /** __`.`__[**`first`**][ColumnsSelectionDsl.first] */
        public interface ColumnSetName

        /** __`.`__[**`firstCol`**][ColumnsSelectionDsl.firstCol] */
        public interface ColumnGroupName
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @param [condition] The optional [ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    private interface CommonFirstDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[first][ColumnSet.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[first][ColumnSet.first]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("First0")
    public fun <C> ColumnSet<C>.first(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
        (allColumnsInternal() as TransformableColumnSet<C>)
            .transform { listOf(it.first(condition)) }
            .singleOrNullWithTransformerImpl()

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[first][ColumnsSelectionDsl.first]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First1")
    public fun ColumnsSelectionDsl<*>.first(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        asSingleColumn().firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[firstCol][SingleColumn.firstCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Interpretable("First2")
    public fun SingleColumn<DataRow<*>>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().first(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[firstCol][String.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun String.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[firstCol][SingleColumn.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[firstCol][KProperty.firstCol]`() }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)

    /**
     * ## First (Col)
     *
     * Returns the first column from [this] that adheres to the optional given [condition].
     * If no column adheres to the given [condition], [NoSuchElementException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `first` is named `firstCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[first][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("order") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[firstCol][kotlin.String.firstCol]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[firstCol][ColumnPath.firstCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @param [condition] The optional [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] condition that the column must adhere to.
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the first column
     *   that adheres to the given [condition].
     * @throws [NoSuchElementException] if no column adheres to the given [condition].
     * @see [ColumnsSelectionDsl.last]
     */
    public fun ColumnPath.firstCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).firstCol(condition)
}

// endregion
