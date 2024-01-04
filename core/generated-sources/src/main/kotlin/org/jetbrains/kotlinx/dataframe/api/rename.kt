package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ParamNameArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ParamTypeArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ReceiverTypeArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage.InfixIntoName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage.InfixNamedName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage.IntoName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage.NamedName
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.DELIMITED_STRING_REGEX
import org.jetbrains.kotlinx.dataframe.impl.DELIMITERS_REGEX
import org.jetbrains.kotlinx.dataframe.impl.api.renameImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.toCamelCaseByDelimiters
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.rename(vararg mappings: Pair<String, String>): DataFrame<T> =
    rename { mappings.map { it.first.toColumnAccessor() }.toColumnSet() }
        .into(*mappings.map { it.second }.toTypedArray())

public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

public data class RenameClause<T, C>(val df: DataFrame<T>, val columns: ColumnsSelector<T, C>)

/**
 * ## Rename to camelCase
 *
 * This function renames all columns to `camelCase` by replacing all [delimiters][DELIMITERS_REGEX]
 * and converting the first char to lowercase.
 * Even [DataFrames][DataFrame] inside [FrameColumns][FrameColumn] are traversed recursively.
 */
public fun <T> DataFrame<T>.renameToCamelCase(): DataFrame<T> = this
    // recursively rename all columns written with delimiters or starting with a capital to camel case
    .rename {
        colsAtAnyDepth { it.name() matches DELIMITED_STRING_REGEX || it.name[0].isUpperCase() }
    }.toCamelCase()

    // take all frame columns at any depth and call renameToCamelCase() on all dataframes inside
    .update {
        colsAtAnyDepth().colsOf<AnyFrame>()
    }.with { it.renameToCamelCase() }

public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> =
    renameImpl(newNames)

public fun <T, C> RenameClause<T, C>.into(vararg newNames: KProperty<*>): DataFrame<T> =
    into(*newNames.map { it.name }.toTypedArray())

public fun <T, C> RenameClause<T, C>.into(transform: (ColumnWithPath<C>) -> String): DataFrame<T> =
    renameImpl(transform)

/**
 * ## Rename to camelCase
 *
 * Renames the selected columns to `camelCase` by replacing all [delimiters][DELIMITERS_REGEX]
 * and converting the first char to lowercase.
 */
public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> = into {
    it.name()
        .toCamelCaseByDelimiters(DELIMITERS_REGEX)
        .replaceFirstChar { it.lowercaseChar() }
}

// endregion

// region DataColumn

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
 * See [Usage]
 */
public interface RenameColumnsSelectionDsl {

    /**
     * ## Rename: `named` / `into` Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `column: `[ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]` | `[String][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[KProperty][kotlin.reflect.KProperty]`<*> | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef] [**named**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]`/`[**into**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into] [column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]
     *
     *  `| `[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef].[**named**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]**`(`**[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]**`)`**
     *
     *  `| `[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef].[**into**][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]**`(`**[column][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnDef]**`)`**
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
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**named**][ColumnsSelectionDsl.named] */
        public interface InfixNamedName

        /** [**into**][ColumnsSelectionDsl.into] */
        public interface InfixIntoName

        /** .[**named**][ColumnsSelectionDsl.named] */
        public interface NamedName

        /** .[**into**][ColumnsSelectionDsl.into] */
        public interface IntoName
    }

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][ColumnReference.named] or [into][ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage]
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]` { name `[named][ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][DataFrame.select]` { `[expr][expr]` { 0 } `[into][ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][DataFrame.select]` { "colA" `[named][String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { {@getArg [CommonRenameDocs.ReceiverArg]} `[{@getArg [CommonRenameDocs.FunctionNameArg]}][{@getArg [CommonRenameDocs.ReceiverTypeArg]}.{@getArg [CommonRenameDocs.FunctionNameArg]}]` {@getArg [CommonRenameDocs.ParamArg]} }`
     *
     * @receiver The [{@getArg [ReceiverTypeArg]}] referencing the column to rename.
     * @param [{@getArg [ParamNameArg]}\] A [{@getArg [ParamTypeArg]}\] used to specify the new name of the column.
     * @return A [ColumnReference] to the renamed column.
     */
    private interface CommonRenameDocs {

        interface ReceiverArg

        interface ReceiverTypeArg

        /** "named" or "into" */
        interface FunctionNameArg

        /** "newName" or "nameOf" */
        interface ParamNameArg
        interface ParamArg

        interface ParamTypeArg

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
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[named][ColumnReference.named]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` "columnB" }`
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
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[named][String.named]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[named][KProperty.named]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
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
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` "columnB" }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { columnA `[into][ColumnReference.into]` Type::columnB }`
     *
     * @receiver The [ColumnReference] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` "columnB" }`
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
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnA" `[into][String.into]` Type::columnB }`
     *
     * @receiver The [String] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` "columnB" }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [newName] A [String] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [ColumnReference] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * ## Rename: `named` / `into`
     * Renaming a column in the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] is done by calling the `infix` functions
     * [named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named] or [into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]. They behave exactly the same,
     * so it's up to contextual preference which one to use. Any combination of [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Usage][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Usage]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { name `[named][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.named]` "Full Name" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[expr][org.jetbrains.kotlinx.dataframe.api.expr]` { 0 } `[into][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.into]` "zeroes" }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[named][kotlin.String.named]` Type::colB }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnA `[into][KProperty.into]` Type::columnB }`
     *
     * @receiver The [KProperty] referencing the column to rename.
     * @param [nameOf] A [KProperty] used to specify the new name of the column.
     * @return A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] to the renamed column.
     */
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
