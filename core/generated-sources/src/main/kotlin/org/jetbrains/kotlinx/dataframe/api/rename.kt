package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.renameImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.renamedColumn
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.RENAME_INTO
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Renames the specified [columns] keeping their original values and location within the [<code>DataFrame</code>][DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [<code>RenameClause</code>][RenameClause],
 * which serves as an intermediate step.
 * The [<code>RenameClause</code>][RenameClause] object provides methods to rename selected columns using:
 * - [<code>to(name)</code>][RenameClause.to] - renames selected columns to the specified names.
 * - [<code>to { nameExpression }</code>][RenameClause.to] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [<code>toCamelCase()</code>][RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [<code>DataFrame</code>][DataFrame] with the renamed columns.
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * See also [<code>renameToCamelCase</code>][renameToCamelCase] which renames all columns to "camelCase" format.
 */
internal interface RenameDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
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
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>rename</code>][org.jetbrains.kotlinx.dataframe.api.rename]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>rename</code>][org.jetbrains.kotlinx.dataframe.api.rename]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>rename</code>][org.jetbrains.kotlinx.dataframe.api.rename]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>rename</code>][org.jetbrains.kotlinx.dataframe.api.rename]`("length", "age")`
     *
     *
     *
     */
    typealias RenameSelectingOptions = Nothing

    /**
     * ## Rename Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`rename`**</code>][rename]**`  { `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]`  `**`}`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`to`**</code>][RenameClause.to]**`(`**`newNames: `[<code>`String`</code>][String]**`, ..)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`to`**</code>][RenameClause.to]**`  { `**`nameExpression: (`[<code>`ColumnWithPath`</code>][ColumnWithPath]`) -> `[<code>String</code>][String]` `**`}`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toCamelCase`**</code>][RenameClause.toCamelCase]**`()`**
     */
    typealias Grammar = Nothing
}

/**
 * Renames columns in the [<code>DataFrame</code>][DataFrame].
 *
 * This function allows renaming multiple columns in a single call by supplying a list of name pairs.
 * Each pair consists of the current column name and the desired new name.
 *
 * See also [<code>renameToCamelCase</code>][renameToCamelCase] which renames all columns to "camelCase" format.
 *
 * Example:
 * ```kotlin
 * df.rename("oldName1" to "newName1", "oldName2" to "newName2")
 * ```
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * @param mappings A vararg of pairs where each pair consists of the original column name (`first`)
 * and the new column name (`second`).
 * @return A new [<code>DataFrame</code>][DataFrame] with the renamed columns.
 */
@Refine
@Interpretable("RenameMapping")
public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .to(*mappings.map { it.second }.toTypedArray())

/**
 * Renames the specified [columns] keeping their original values and location within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [<code>RenameClause</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause],
 * which serves as an intermediate step.
 * The [<code>RenameClause</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause] object provides methods to rename selected columns using:
 * - [<code>to(name)</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.to] - renames selected columns to the specified names.
 * - [<code>to { nameExpression }</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.to] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [<code>toCamelCase()</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the renamed columns.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.RenameDocs.RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * See also [<code>renameToCamelCase</code>][org.jetbrains.kotlinx.dataframe.api.renameToCamelCase] which renames all columns to "camelCase" format.
 * ### This Rename Overload
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
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename { col1 and col2 }.to("width", "length")
 *
 * // Rename all columns using their full path, delimited by "->"
 * df.rename { colsAtAnyDepth() }.to { it.path.joinToString("->") }
 *
 * // Renames all numeric columns to "camelCase"
 * df.rename { colsOf<Number>() }.toCamelCase()
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to group.
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
 * Renames the specified [columns] keeping their original values and location within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately rename the columns but instead selects columns to rename and
 * returns a [<code>RenameClause</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause],
 * which serves as an intermediate step.
 * The [<code>RenameClause</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause] object provides methods to rename selected columns using:
 * - [<code>to(name)</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.to] - renames selected columns to the specified names.
 * - [<code>to { nameExpression }</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.to] - renames selected columns using a provided
 * expression assuming column with its path and returning a new name.
 * - [<code>toCamelCase()</code>][org.jetbrains.kotlinx.dataframe.api.RenameClause.toCamelCase] - renames all selected columns to "camelCase".
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the renamed columns.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.RenameDocs.RenameSelectingOptions].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * See also [<code>renameToCamelCase</code>][org.jetbrains.kotlinx.dataframe.api.renameToCamelCase] which renames all columns to "camelCase" format.
 * ### This Rename Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename("col1", "col2").to("width", "length")
 *
 * // Renames "arrival_date" and "passport-ID" columns to "camelCase"
 * df.rename("arrival_date", "passport-ID").toCamelCase()
 * ```
 * @param [columns] The [<code>Columns Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to group.
 */
@StringApiInterpretable(interpreter = "Rename", stringArgument = "cols", targetArgument = "columns")
public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

/**
 * An intermediate class used in the [<code>rename</code>][rename] operation.
 *
 * This class itself does not perform any renaming — it is a transitional step
 * before specifying how to rename the selected columns.
 * It must be followed by one of the renaming methods
 * to produce a new [<code>DataFrame</code>][DataFrame] with renamed columns.
 *
 * The resulting columns will keep their original values and positions
 * in the [<code>DataFrame</code>][DataFrame], but their names will be changed.
 *
 * Use the following methods to perform the conversion:
 * - [<code>to(name)</code>][RenameClause.to] — renames selected columns to the specified names.
 * - [<code>to { nameExpression }</code>][RenameClause.to] — renames selected columns using a custom expression,
 *   which takes the column and its path and returns a new name.
 * - [<code>toCamelCase()</code>][RenameClause.toCamelCase] — renames all selected columns to `camelCase`.
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * See [<code>Grammar</code>][RenameDocs.Grammar] for more details.
 */
@HasSchema(schemaArg = 0)
public class RenameClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "RenameClause(df=$df, columns=$columns)"
}

/**
 * Renames all columns in this [<code>DataFrame</code>][DataFrame] to the "camelCase" format.
 *
 * Removes all delimiters between words and capitalizes each word except the first one.
 * Adds an underscore between consecutive numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * [<code>DataFrames</code>][DataFrame] inside [<code>FrameColumns</code>][FrameColumn] are traversed recursively.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] with updated column names.
 *
 * ### Renaming Examples
 * ```
 * "snake_case_name" -> "snakeCaseName"
 * "PascalCaseName" -> "pascalCaseName"
 * "doner-case-name" -> "donerCaseName"
 * "UPPER_CASE_NAME -> upperCaseName"
 * ```
 *
 * For more information: [See `renameToCamelCase` on the documentation website.](https://kotlin.github.io/dataframe/rename.html#renametocamelcase)
 *
 * @see [rename]
 * @return a [<code>DataFrame</code>][DataFrame] with column names converted to "camelCase" format.
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
    to(*newColumns.map { it.name() }.toTypedArray())

/**
 * __NOTE:__ While you can keep using 'into', we recommend using [<code>to</code>][RenameClause.to] for
 * better readability and more natural English.
 *
 * Renames the columns selected with [<code>rename</code>][rename] to the specified [<code>newNames</code>][newNames],
 * preserving their values and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * The mapping is positional: [<code>newNames</code>][newNames] are applied in the order
 * the columns were selected — the first selected column is renamed to the first name,
 * the second to the second, and so on.
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [<code>Grammar</code>][RenameDocs.Grammar].
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
 * @return A new [<code>DataFrame</code>][DataFrame] with the columns renamed.
 */
@Refine
@Interpretable("RenameInto")
@Deprecated(message = RENAME_INTO, replaceWith = ReplaceWith("to(*newNames)"))
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = renameImpl(newNames)

/**
 * Renames the columns selected with [<code>rename</code>][rename] to the specified [<code>newNames</code>][newNames],
 * preserving their values and positions within the [<code>DataFrame</code>][DataFrame].
 *
 * The mapping is positional: [<code>newNames</code>][newNames] are applied in the order
 * the columns were selected — the first selected column is renamed to the first name,
 * the second to the second, and so on.
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [<code>Grammar</code>][RenameDocs.Grammar].
 *
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename("col1", "col2").to("width", "length")
 *
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename { col1 and col2 }.to("width", "length")
 * ```
 *
 * @param newNames The new names for the selected columns, applied in order of selecting.
 * @return A new [<code>DataFrame</code>][DataFrame] with the columns renamed.
 */
@Refine
@Interpretable("RenameInto")
public fun <T, C> RenameClause<T, C>.to(vararg newNames: String): DataFrame<T> = renameImpl(newNames)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    to(*newNames.map { it.name }.toTypedArray())

/**
 * __NOTE:__ While you can keep using 'into', we recommend using [<code>to</code>][RenameClause.to] for
 * better readability and more natural English.
 *
 * Renames the columns selected with [<code>rename</code>][rename] by applying the [<code>transform</code>][transform] expression
 * to each of them. This expression receives the column together with its full path
 * (as [<code>ColumnWithPath</code>][ColumnWithPath]) and must return the new name for that column.
 * The operation preserves the original columns’ values and their positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [<code>Grammar</code>][RenameDocs.Grammar] for more details.
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
 * @param transform A function that takes a [<code>ColumnWithPath</code>][ColumnWithPath] for each selected column
 * and returns the new column name.
 * @return A new [<code>DataFrame</code>][DataFrame] with the columns renamed.
 */
@Refine
@Interpretable("RenameIntoLambda")
@Deprecated(message = RENAME_INTO, replaceWith = ReplaceWith("to(transform)"))
public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    renameImpl(transform)

/**
 * Renames the columns selected with [<code>rename</code>][rename] by applying the [<code>transform</code>][transform] expression
 * to each of them. This expression receives the column together with its full path
 * (as [<code>ColumnWithPath</code>][ColumnWithPath]) and must return the new name for that column.
 * The operation preserves the original columns’ values and their positions within the [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `rename` on the documentation website.](https://kotlin.github.io/dataframe/rename.html)
 *
 * Check out [<code>Grammar</code>][RenameDocs.Grammar] for more details.
 *
 * ### Examples:
 * ```kotlin
 * // Rename all columns using their full path, delimited by "->"
 * df.rename { colsAtAnyDepth() }.to { it.path.joinToString("->") }
 *
 * // Rename all `String` columns with uppercase
 * df.rename { colsOf<String>() }.to { it.name.uppercase() }
 * ```
 *
 * @param transform A function that takes a [<code>ColumnWithPath</code>][ColumnWithPath] for each selected column
 * and returns the new column name.
 * @return A new [<code>DataFrame</code>][DataFrame] with the columns renamed.
 */
@Refine
@Interpretable("RenameIntoLambda")
public fun <T, C> RenameClause<T, C>.to(transform: (ColumnWithPath<C>) -> String): DataFrame<T> = renameImpl(transform)

/**
 * Renames the columns, previously selected with [<code>rename</code>][rename] to "camelCase" format.
 *
 * All delimiters between words are removed, words are capitalized except for the first one.
 * Places underscore between numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * Returns a [<code>DataFrame</code>][DataFrame] with updated column names.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * ### Examples
 * ```kotlin
 * // Renames "arrival_date" and "passport-ID" columns to "camelCase"
 * df.rename("arrival_date", "passport-ID").toCamelCase()
 * // Renames all numeric columns to "camelCase"
 * df.rename { colsOf<Number>() }.toCamelCase()
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
 * For more information: [See `renameToCamelCase` on the documentation website.](https://kotlin.github.io/dataframe/rename.html#renametocamelcase)
 *
 * @return a [<code>DataFrame</code>][DataFrame] with column names converted to "camelCase" format.
 */
@Refine
@Interpretable("RenameToCamelCaseClause")
public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> = to { it.renameToCamelCase().name() }

// endregion

// region DataColumn

/**
 *
 * Renames this column to "camelCase" format.
 * All delimiters between words are removed, words are capitalized except for the first one.
 * Places underscore between numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * Returns a [<code>ColumnReference</code>][ColumnReference] with updated name.
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
 *
 * For more information: [See `renameToCamelCase` on the documentation website.](https://kotlin.github.io/dataframe/rename.html#renametocamelcase)
 *
 * @return a [<code>ColumnReference</code>][ColumnReference] with the name converted to "camelCase" format.
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
 * Returns a new column reference with the original column values but a new [<code>name</code>][name].
 *
 * This is useful when you want to specify an existing column
 * (for example, in `select`, `update`, or `rename` operations)
 * but give it a different name in the resulting [<code>DataFrame</code>][DataFrame].
 *
 * ### Example:
 * ```kotlin
 * // Select "size" column as "dimensions"
 * df.select { size named "dimensions" }
 * ```
 *
 * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
 *
 * @param name The new name to assign to the column.
 * @return A new column with the original structure and values but with the specified [<code>name</code>][name].
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
 * ## Rename: `named` / `into` [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface RenameColumnsSelectionDsl {

    /**
     * ## Rename: `named` / `into` Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]` `[<code>**named**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]`/`[<code>**into**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]` `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     *  `| `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]__`.`__[<code>**named**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**
     *
     *  `| `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]__`.`__[<code>**into**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**
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

        /** [<code>**named**</code>][ColumnsSelectionDsl.named] */
        public typealias InfixNamedName = Nothing

        /** [<code>**into**</code>][ColumnsSelectionDsl.into] */
        public typealias InfixIntoName = Nothing

        /** __`.`__[<code>**named**</code>][ColumnsSelectionDsl.named] */
        public typealias NamedName = Nothing

        /** __`.`__[<code>**into**</code>][ColumnsSelectionDsl.into] */
        public typealias IntoName = Nothing
    }

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][ColumnReference.named] or [<code>into</code>][ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { name  `[<code>named</code>][ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>expr</code>][expr]`  { 0 }  `[<code>into</code>][ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "colA"  `[<code>named</code>][String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {   `[<code></code>][.]`  }`
     *
     * @receiver The [<code></code>][] referencing the column to rename.
     * @param [] A [] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][ColumnReference] to the renamed column.
     */
    @Suppress("ClassName")
    private interface CommonRenameDocs {

        typealias RECEIVER = Nothing
        typealias RECEIVER_TYPE = Nothing

        /** "named" or "into" */
        typealias FUNCTION_NAME = Nothing

        /** "newName" or "nameOf" */
        typealias PARAM_NAME = Nothing
        typealias PARAM = Nothing
        typealias PARAM_TYPE = Nothing

        /**
         */
        typealias ColumnReferenceReceiver = Nothing

        /**
         */
        typealias StringReceiver = Nothing

        /**
         */
        typealias KPropertyReceiver = Nothing

        /**
         */
        typealias SingleColumnReceiver = Nothing

        /**
         */
        typealias ColumnReferenceParam = Nothing

        /**
         */
        typealias StringParam = Nothing

        /**
         */
        typealias KPropertyParam = Nothing

        typealias NamedFunctionName = Nothing

        typealias IntoFunctionName = Nothing
    }

    // region named

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>named</code>][ColumnReference.named]` "columnB" }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>named</code>][ColumnReference.named]` columnB }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>named</code>][ColumnReference.named]` Type::columnB }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>named</code>][String.named]` "columnB" }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(newName: String): ColumnReference<*> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>named</code>][String.named]` columnB }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>named</code>][String.named]` Type::columnB }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>named</code>][KProperty.named]` "columnB" }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>named</code>][KProperty.named]` columnB }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>named</code>][KProperty.named]` Type::columnB }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { col(0)  `[<code>named</code>][SingleColumn.named]` "columnB" }`
     *
     * @receiver The [<code>SingleColumn</code>][SingleColumn] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named1")
    public infix fun <C> SingleColumn<C>.named(newName: String): SingleColumn<C> = renamedColumn(newName)

    // endregion

    // region into

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>into</code>][ColumnReference.into]` "columnB" }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>into</code>][ColumnReference.into]` columnB }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { columnA  `[<code>into</code>][ColumnReference.into]` Type::columnB }`
     *
     * @receiver The [<code>ColumnReference</code>][ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>into</code>][String.into]` "columnB" }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(newName: String): ColumnReference<*> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>into</code>][String.into]` columnB }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "columnA"  `[<code>into</code>][String.into]` Type::columnB }`
     *
     * @receiver The [<code>String</code>][String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>into</code>][KProperty.into]` "columnB" }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>into</code>][KProperty.into]` columnB }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::columnA  `[<code>into</code>][KProperty.into]` Type::columnB }`
     *
     * @receiver The [<code>KProperty</code>][KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * For more information: [See Rename: `named` / `into` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#rename)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { name  `[<code>named</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.expr]`  { 0 }  `[<code>into</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>named</code>][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { col(0)  `[<code>into</code>][SingleColumn.into]` "columnB" }`
     *
     * @receiver The [<code>SingleColumn</code>][SingleColumn] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    @Interpretable("Named1")
    public infix fun <C> SingleColumn<C>.into(newName: String): SingleColumn<C> = named(newName)

    // endregion
}

// endregion
