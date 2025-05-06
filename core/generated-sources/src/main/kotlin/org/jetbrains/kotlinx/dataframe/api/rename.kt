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
import kotlin.reflect.KProperty

// region DataFrame

@Refine
@Interpretable("RenameMapping")
public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

@Interpretable("Rename")
public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

@HasSchema(schemaArg = 0)
public class RenameClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "RenameClause(df=$df, columns=$columns)"
}

/**
 * ## Rename to "camelCase"
 *
 * This function renames all columns in this [DataFrame] to the "camelCase" format.
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
 * ### Examples:
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

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

@Refine
@Interpretable("RenameInto")
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = renameImpl(newNames)

@Deprecated(
    "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
)
@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    renameImpl(transform)

/**
 * ## Rename to "camelCase"
 *
 * Renames the columns, previously selected with [rename] to "camelCase" format.
 * All delimiters between words are removed, words are capitalized except for the first one.
 * Places underscore between numbers.
 * If the string does not contain any letters or numbers, it remains unchanged.
 *
 * Returns a [DataFrame] with updated column names.
 *
 * This function supports converting names from `snake_case`, `PascalCase`, and other delimited formats
 * into a consistent "camelCase" representation.
 *
 * ### Examples:
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
 * ## Rename to camelCase
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
 * ### Examples:
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
public fun <T, C : ColumnReference<T>> C.rename(column: KProperty<T>): C = rename(column.columnName) as C

@Suppress("UNCHECKED_CAST")
public fun <T, C : ColumnReference<T>> C.rename(column: ColumnAccessor<T>): C = rename(column.name()) as C

// endregion

// region named

@Suppress("UNCHECKED_CAST")
public infix fun <T, C : ColumnReference<T>> C.named(name: String): C = rename(name) as C

public infix fun <T, C : ColumnReference<T>> C.named(name: KProperty<*>): C = rename(name)

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
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  | `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
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
    @Deprecated(
        "Recommended to migrate to use String or Extension properties API https://kotlin.github.io/dataframe/apilevels.html",
    )
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
