package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.renameImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Renames the specified [columns] keeping their original values and location within the [DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [RenameClause],
 * which serves as an intermediate step.
 * The [RenameClause] object provides methods to rename selected columns using:
 * - [into(name)][RenameClause.into] - renames selected columns to the specified names.
 * - [into { nameExpression }][RenameClause.into] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [toCamelCase()][RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [DataFrame] with the renamed columns.
 *
 * Check out [Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 */
internal interface RenameDocs {

    /**
     *
     * ## Selecting Columns
     * Selecting columns for various operations (including but not limited to
     * [DataFrame.select][org.jetbrains.kotlinx.dataframe.DataFrame.select], [DataFrame.update][org.jetbrains.kotlinx.dataframe.DataFrame.update], [DataFrame.gather][org.jetbrains.kotlinx.dataframe.DataFrame.gather], and [DataFrame.fillNulls][org.jetbrains.kotlinx.dataframe.DataFrame.fillNulls])
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample]
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
     *
     * #### For example:
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]`("length", "age")`
     *
     * ### 3. [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample]
     * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
     * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
     *
     * #### For example:
     *
     * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[rename][org.jetbrains.kotlinx.dataframe.api.rename]`(Person::length, Person::age)`
     *
     */
    interface RenameSelectingOptions

    /**
     * ## Rename Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[`rename`][rename]**`  { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`into`**][RenameClause.into]`(name: `[`String`][String]`)`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`into`**][RenameClause.into]`  { nameExpression: (`[`ColumnWithPath`][ColumnWithPath]`<C>) -> `[String]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`toCamelCase`**][RenameClause.toCamelCase]`()`
     */
    interface Grammar
}

/**
 * Renames columns in the [DataFrame].
 *
 * This function allows renaming multiple columns in a single call by supplying a list of name pairs.
 * Each pair consists of the current column name and the desired new name.
 *
 * Example:
 * ```
 * df.rename("oldName1" to "newName1", "oldName2" to "newName2")
 * ```
 *
 * @param mappings A vararg of pairs where each pair consists of the original column name (`first`)
 * and the new column name (`second`).
 * @return A new [DataFrame] with the renamed columns.
 */
@Refine
@Interpretable("RenameMapping")
public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

/**
 * Renames the specified [columns] keeping their original values and location within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [RenameClause][org.jetbrains.kotlinx.dataframe.api.RenameClause],
 * which serves as an intermediate step.
 * The [RenameClause][org.jetbrains.kotlinx.dataframe.api.RenameClause] object provides methods to rename selected columns using:
 * - [into(name)][org.jetbrains.kotlinx.dataframe.api.RenameClause.into] - renames selected columns to the specified names.
 * - [into { nameExpression }][org.jetbrains.kotlinx.dataframe.api.RenameClause.into] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [toCamelCase()][org.jetbrains.kotlinx.dataframe.api.RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the renamed columns.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.RenameDocs.RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 * ### This Rename Overload
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
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename { col1 and col2 }.into("width", "length")
 *
 * // Rename all columns using their full path, delimited by "->"
 * df.rename { colsAtAnyDepth() }.into { it.path.joinToString("->") }
 *
 * // Renames all numeric columns to "camelCase"
 * df.rename { colsOf<Number>() }.toCamelCase()
 * ```
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Rename")
public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

/**
 * Renames the specified [columns] keeping their original values and location within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [RenameClause][org.jetbrains.kotlinx.dataframe.api.RenameClause],
 * which serves as an intermediate step.
 * The [RenameClause][org.jetbrains.kotlinx.dataframe.api.RenameClause] object provides methods to rename selected columns using:
 * - [into(name)][org.jetbrains.kotlinx.dataframe.api.RenameClause.into] - renames selected columns to the specified names.
 * - [into { nameExpression }][org.jetbrains.kotlinx.dataframe.api.RenameClause.into] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [toCamelCase()][org.jetbrains.kotlinx.dataframe.api.RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the renamed columns.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.RenameDocs.RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 * ### This Rename Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename("col1", "col2").into("width", "length")
 *
 * // Renames "arrival_date" and "passport-ID" columns to "camelCase"
 * df.rename("arrival_date", "passport-ID").toCamelCase()
 * ```
 * @param [columns] The [Columns Names][String] used to select the columns of this [DataFrame] to group.
 */
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

/**
 * An intermediate class used in the [rename] operation.
 *
 * This class itself does not perform any renaming — it is a transitional step
 * before specifying how to rename the selected columns.
 * It must be followed by one of the renaming methods
 * to produce a new [DataFrame] with renamed columns.
 *
 * The resulting columns will keep their original values and positions
 * in the [DataFrame], but their names will be changed.
 *
 * Use the following methods to perform the conversion:
 * - [into(name)][RenameClause.into] — renames selected columns to the specified names.
 * - [into { nameExpression }][RenameClause.into] — renames selected columns using a custom expression,
 *   which takes the column and its path and returns a new name.
 * - [toCamelCase()][RenameClause.toCamelCase] — renames all selected columns to `camelCase`.
 *
 * See [Grammar][RenameDocs.Grammar] for more details.
 */
@HasSchema(schemaArg = 0)
public class RenameClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "RenameClause(df=$df, columns=$columns)"
}

/**
 * Renames all columns in this [DataFrame] to the "camelCase" format.
 *
 * Removes all delimiters between words and capitalizes each word except the first one.
 * Adds an underscore between consecutive numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * [DataFrames][DataFrame] inside [FrameColumns][FrameColumn] are traversed recursively.
 *
 * Returns a [DataFrame] with updated column names.
 *
 * ### Renaming Examples
 * ```
 * "snake_case_name" -> "snakeCaseName"
 * "PascalCaseName" -> "pascalCaseName"
 * "doner-case-name" -> "donerCaseName"
 * "UPPER_CASE_NAME -> upperCaseName"
 * ```
 *
 * @return a [DataFrame] with column names converted to "camelCase" format.
 */
@Refine
@Interpretable("RenameToCamelCase")
public fun <T> DataFrame<T>.renameToCamelCase(): DataFrame<T> =
    // recursively rename all columns written with delimiters or starting with a capital to camel case
    rename {
        colsAtAnyDepth()
    }.toCamelCase()
        // take all frame columns at any depth and call renameToCamelCase() on all dataframes inside
        .update {
            colsAtAnyDepth().colsOf<AnyFrame>()
        }.with {
            it.renameToCamelCase()
        }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

/**
 * Renames the columns selected with [rename] to the specified [newNames],
 * preserving their values and positions within the [DataFrame].
 *
 * The mapping is positional: [newNames] are applied in the order
 * the columns were selected — the first selected column is renamed to the first name,
 * the second to the second, and so on.
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [Grammar][RenameDocs.Grammar].
 *
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename("col1", "col2").into("width", "length")
 *
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename { col1 and col2 }.into("width", "length")
 * ```
 *
 * @param newNames The new names for the selected columns, applied in order of selecting.
 * @return A new [DataFrame] with the columns renamed.
 */
@Refine
@Interpretable("RenameInto")
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = renameImpl(newNames)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

/**
 * Renames the columns selected with [rename] by applying the [transform] expression
 * to each of them. This expression receives the column together with its full path
 * (as [ColumnWithPath]) and must return the new name for that column.
 * The operation preserves the original columns’ values and their positions within the [DataFrame].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [Grammar][RenameDocs.Grammar] for more details.
 *
 * ### Examples:
 * ```kotlin
 * // Rename all columns using their full path, delimited by "->"
 * df.rename { colsAtAnyDepth() }.into { it.path.joinToString("->") }
 *
 * // Rename all `String` columns with uppercase
 * df.rename { colsOf<String>() }.into { it.name.uppercase() }
 * ```
 *
 * @param transform A function that takes a [ColumnWithPath] for each selected column
 * and returns the new column name.
 * @return A new [DataFrame] with the columns renamed.
 */
public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    renameImpl(transform)

/**
 * Renames the columns, previously selected with [rename] to "camelCase" format.
 *
 * All delimiters between words are removed, words are capitalized except for the first one.
 * Places underscore between numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * Returns a [DataFrame] with updated column names.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * ### Examples
 * ```kotlin
 * df.rename("arrival_date", "passport-ID").toCamelCase()
 * ```
 *
 * #### Renaming Examples
 * ```
 * "snake_case_name" -> "snakeCaseName"
 * "PascalCaseName" -> "pascalCaseName"
 * "doner-case-name" -> "donerCaseName"
 * "UPPER_CASE_NAME -> upperCaseName"
 * ```
 *
 * @return a [DataFrame] with column names converted to "camelCase" format.
 */
@Refine
@Interpretable("RenameToCamelCaseClause")
public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> = into { it.renameToCamelCase().name() }

// endregion

// region DataColumn

/**
 *
 * Renames this column to "camelCase" format.
 * All delimiters between words are removed, words are capitalized except for the first one.
 * Places underscore between numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * Returns a [ColumnReference] with updated name.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * #### Renaming Examples
 * ```
 * "snake_case_name" -> "snakeCaseName"
 * "PascalCaseName" -> "pascalCaseName"
 * "doner-case-name" -> "donerCaseName"
 * "UPPER_CASE_NAME -> upperCaseName"
 * ```
 * @return a [ColumnReference] with the name converted to "camelCase" format.
 */
@Suppress("UNCHECKED_CAST")
public fun <T, C : ColumnReference<T>> C.renameToCamelCase(): C =
    rename(
        this.name().toCamelCaseByDelimiters(),
    ) as C

@Suppress("UNCHECKED_CAST")
@AccessApiOverload
@Deprecated(DEPRECATED_ACCESS_API)
public fun <T, C : ColumnReference<T>> C.rename(column: KProperty<T>): C = rename(column.columnName) as C

@Suppress("UNCHECKED_CAST")
@AccessApiOverload
@Deprecated(DEPRECATED_ACCESS_API)
public fun <T, C : ColumnReference<T>> C.rename(column: ColumnAccessor<T>): C = rename(column.name()) as C

// endregion

// region named

/**
 * Returns a new column reference with the original column values but a new [name].
 *
 * This is useful when you want to specify an existing column
 * (for example, in `select`, `update`, or `rename` operations)
 * but give it a different name in the resulting [DataFrame].
 *
 * ### Example:
 * ```kotlin
 * // Select "size" column as "dimensions"
 * df.select { size named "dimensions" }
 * ```
 *
 * @param name The new name to assign to the column.
 * @return A new column with the original structure and values but with the specified [name].
 */
@Suppress("UNCHECKED_CAST")
public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

@AccessApiOverload
@Deprecated(DEPRECATED_ACCESS_API)
public infix fun <T, C : ColumnReference<T>> C.named(name: KProperty<*>): C = rename(name)

@AccessApiOverload
@Deprecated(DEPRECATED_ACCESS_API)
public infix fun <T, C : ColumnReference<T>> C.named(name: ColumnAccessor<*>): C = rename(name)

// endregion

// region ColumnsSelectionDsl

/**
 * ## Rename: `named` / `into` [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface RenameColumnsSelectionDsl {

    /**
     * ## Rename: `named` / `into` Grammar
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
     *  [`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]` `[**named**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]`/`[**into**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]` `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     *  `| `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]__`.`__[**named**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**
     *
     *  `| `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]__`.`__[**into**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**
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
     */
    public interface Grammar {

        /** [**named**][ColumnsSelectionDsl.named] */
        public interface InfixNamedName

        /** [**into**][ColumnsSelectionDsl.into] */
        public interface InfixIntoName

        /** __`.`__[**named**][ColumnsSelectionDsl.named] */
        public interface NamedName

        /** __`.`__[**into**][ColumnsSelectionDsl.into] */
        public interface IntoName
    }

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][ColumnReference.named] or [into][ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]`  { name  `[named][ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][DataFrame.select]`  {  `[expr][expr]`  { 0 }  `[into][ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][DataFrame.select]`  { "colA"  `[named][String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]`  {   `[][.]`  }`
     *
     * @receiver The [] referencing the column to rename.
     * @param [] A [] used to specify the new name of the column.
     * @return A [ColumnReference] to the renamed column.
     */
    @Suppress("ClassName")
    private interface CommonRenameDocs {

        interface RECEIVER

        interface RECEIVER_TYPE

        /** "named" or "into" */
        interface FUNCTION_NAME

        /** "newName" or "nameOf" */
        interface PARAM_NAME

        interface PARAM

        interface PARAM_TYPE

        /**
         */
        interface ColumnReferenceReceiver

        /**
         */
        interface StringReceiver

        /**
         */
        interface KPropertyReceiver

        /**
         */
        interface ColumnReferenceParam

        /**
         */
        interface StringParam

        /**
         */
        interface KPropertyParam

        interface NamedFunctionName

        interface IntoFunctionName
    }

    // region named

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[named][ColumnReference.named]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[named][ColumnReference.named]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[named][ColumnReference.named]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[named][String.named]` "columnB" }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(newName: String): ColumnReference<*> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[named][String.named]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[named][String.named]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[named][KProperty.named]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[named][KProperty.named]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[named][KProperty.named]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.columnName)

    // endregion

    // region into

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[into][ColumnReference.into]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[into][ColumnReference.into]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[into][ColumnReference.into]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[into][String.into]` "columnB" }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(newName: String): ColumnReference<*> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[into][String.into]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[into][String.into]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[into][KProperty.into]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[into][KProperty.into]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[expr][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[into][KProperty.into]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
