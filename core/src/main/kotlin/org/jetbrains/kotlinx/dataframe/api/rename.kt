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
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Renames the specified [columns\] keeping their original values and location within the [DataFrame].
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
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][RenameSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Rename]}
 *
 * See also [renameToCamelCase] which renames all columns to "camelCase" format.
 */
internal interface RenameDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetRenameOperationArg]}
     */
    interface RenameSelectingOptions

    /**
     * ## Rename Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * [**`rename`**][rename]**`  { `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  `**`}`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`into`**][RenameClause.into]**`(`**`name: `[`String`][String]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`into`**][RenameClause.into]**`  { `**`nameExpression: (`[`ColumnWithPath`][ColumnWithPath]`) -> `[String]` `**`}`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`toCamelCase`**][RenameClause.toCamelCase]**`()`**
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [rename][rename]} */
@ExcludeFromSources
private interface SetRenameOperationArg

/**
 * {@include [RenameDocs]}
 * ### This Rename Overload
 */
@ExcludeFromSources
private interface CommonRenameDocs

/**
 * Renames columns in the [DataFrame].
 *
 * This function allows renaming multiple columns in a single call by supplying a list of name pairs.
 * Each pair consists of the current column name and the desired new name.
 *
 * See also [renameToCamelCase] which renames all columns to "camelCase" format.
 *
 * Example:
 * ```kotlin
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
 * @include [CommonRenameDocs]
 * @include [SelectingColumns.Dsl] {@include [SetRenameOperationArg]}
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
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Rename")
public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

/**
 * @include [CommonRenameDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetRenameOperationArg]}
 * ### Examples:
 * ```kotlin
 * // Rename "col1" to "width" and "col2" to "length"
 * df.rename("col1", "col2").into("width", "length")
 *
 * // Renames "arrival_date" and "passport-ID" columns to "camelCase"
 * df.rename("arrival_date", "passport-ID").toCamelCase()
 * ```
 * @param [columns\] The [Columns Names][String] used to select the columns of this [DataFrame] to group.
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
 * @see [rename]
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

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
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
 * For more information: {@include [DocumentationUrls.Rename]}
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

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

/**
 * Renames the columns selected with [rename] by applying the [transform] expression
 * to each of them. This expression receives the column together with its full path
 * (as [ColumnWithPath]) and must return the new name for that column.
 * The operation preserves the original columns’ values and their positions within the [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Rename]}
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
@Refine
@Interpretable("RenameIntoLambda")
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
 * ## Rename: `named` / `into` {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface RenameColumnsSelectionDsl {

    /**
     * ## Rename: `named` / `into` Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [DslGrammarTemplate.ColumnRef]}` `{@include [InfixNamedName]}`/`{@include [InfixIntoName]}` `{@include [DslGrammarTemplate.ColumnRef]}
     *
     *  `| `{@include [DslGrammarTemplate.ColumnRef]}{@include [NamedName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`**
     *
     *  `| `{@include [DslGrammarTemplate.ColumnRef]}{@include [IntoName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]}
     * {@set [DslGrammarTemplate.COLUMN_SET_PART]}
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
     * so it's up to contextual preference which one to use. Any combination of {@include [AccessApiLink]} can be
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
     * `df.`[select][DataFrame.select]`  { {@get [CommonRenameDocs.RECEIVER]}  `[{@get [CommonRenameDocs.FUNCTION_NAME]}][{@get [CommonRenameDocs.RECEIVER_TYPE]}.{@get [CommonRenameDocs.FUNCTION_NAME]}]` {@get [CommonRenameDocs.PARAM]} }`
     *
     * @receiver The [{@get [RECEIVER_TYPE]}] referencing the column to rename.
     * @param [{@get [PARAM_NAME]}\] A [{@get [PARAM_TYPE]}\] used to specify the new name of the column.
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
         * @set [RECEIVER] columnA
         * @set [RECEIVER_TYPE] ColumnReference
         */
        interface ColumnReferenceReceiver

        /**
         * @set [RECEIVER] "columnA"
         * @set [RECEIVER_TYPE] String
         */
        interface StringReceiver

        /**
         * @set [RECEIVER] Type::columnA
         * @set [RECEIVER_TYPE] KProperty
         */
        interface KPropertyReceiver

        /**
         * @set [PARAM] columnB
         * @set [PARAM_NAME] nameOf
         * @set [PARAM_TYPE] ColumnReference
         */
        interface ColumnReferenceParam

        /**
         * @set [PARAM] "columnB"
         * @set [PARAM_NAME] newName
         * @set [PARAM_TYPE] String
         */
        interface StringParam

        /**
         * @set [PARAM] Type::columnB
         * @set [PARAM_NAME] nameOf
         * @set [PARAM_TYPE] KProperty
         */
        interface KPropertyParam

        /** @set [CommonRenameDocs.FUNCTION_NAME] named */
        interface NamedFunctionName

        /** @set [CommonRenameDocs.FUNCTION_NAME] into */
        interface IntoFunctionName
    }

    // region named

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun String.named(newName: String): ColumnReference<*> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.columnName)

    // endregion

    // region into

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @Interpretable("Named0")
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun String.into(newName: String): ColumnReference<*> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
