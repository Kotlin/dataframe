package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ParamNameArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ParamTypeArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.CommonRenameDocs.ReceiverTypeArg
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar.InfixIntoName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar.InfixNamedName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar.IntoName
import org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.Grammar.NamedName
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.renamedReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
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

@Interpretable("Rename")
public fun <T, C> DataFrame<T>.rename(columns: ColumnsSelector<T, C>): RenameClause<T, C> = RenameClause(this, columns)

public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

@HasSchema(schemaArg = 0)
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

@Refine
@Interpretable("RenameInto")
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
 * ## Rename: `named` / `into` {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface RenameColumnsSelectionDsl {

    /**
     * ## Rename: `named` / `into` Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [DslGrammarTemplate.ColumnRef]} {@include [InfixNamedName]}`/`{@include [InfixIntoName]} {@include [DslGrammarTemplate.ColumnRef]}
     *
     *  `| `{@include [DslGrammarTemplate.ColumnRef]}{@include [NamedName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`**
     *
     *  `| `{@include [DslGrammarTemplate.ColumnRef]}{@include [IntoName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.ColumnGroupPart]}
     * {@set [DslGrammarTemplate.ColumnSetPart]}
     */
    public interface Grammar {

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
     * so it's up to contextual preference which one to use. Any combination of {@include [AccessApiLink]} can be
     * used to specify the column to rename and which name should be used instead.
     *
     * ### Check out: [Grammar]
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
     * `df.`[select][DataFrame.select]` { {@get [CommonRenameDocs.ReceiverArg]} `[{@get [CommonRenameDocs.FunctionNameArg]}][{@get [CommonRenameDocs.ReceiverTypeArg]}.{@get [CommonRenameDocs.FunctionNameArg]}]` {@get [CommonRenameDocs.ParamArg]} }`
     *
     * @receiver The [{@get [ReceiverTypeArg]}] referencing the column to rename.
     * @param [{@get [ParamNameArg]}\] A [{@get [ParamTypeArg]}\] used to specify the new name of the column.
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
         * @set [ReceiverArg] columnA
         * @set [ReceiverTypeArg] ColumnReference
         */
        interface ColumnReferenceReceiver

        /**
         * @set [ReceiverArg] "columnA"
         * @set [ReceiverTypeArg] String
         */
        interface StringReceiver

        /**
         * @set [ReceiverArg] Type::columnA
         * @set [ReceiverTypeArg] KProperty
         */
        interface KPropertyReceiver

        /**
         * @set [ParamArg] columnB
         * @set [ParamNameArg] nameOf
         * @set [ParamTypeArg] ColumnReference
         */
        interface ColumnReferenceParam

        /**
         * @set [ParamArg] "columnB"
         * @set [ParamNameArg] newName
         * @set [ParamTypeArg] String
         */
        interface StringParam

        /**
         * @set [ParamArg] Type::columnB
         * @set [ParamNameArg] nameOf
         * @set [ParamTypeArg] KProperty
         */
        interface KPropertyParam

        /** @set [CommonRenameDocs.FunctionNameArg] named */
        interface NamedFunctionName

        /** @set [CommonRenameDocs.FunctionNameArg] into */
        interface IntoFunctionName
    }

    // region named

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> ColumnReference<C>.named(nameOf: KProperty<*>): ColumnReference<C> =
        named(nameOf.columnName)

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
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
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
    public infix fun <C> ColumnReference<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
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
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
