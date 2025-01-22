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

@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: ColumnReference<C>): RenameClause<T, C> =
    rename { cols.toColumnSet() }

@AccessApiOverload
public fun <T, C> DataFrame<T>.rename(vararg cols: KProperty<C>): RenameClause<T, C> = rename { cols.toColumnSet() }

public fun <T> DataFrame<T>.rename(vararg cols: String): RenameClause<T, Any?> = rename { cols.toColumnSet() }

@HasSchema(schemaArg = 0)
public class RenameClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "RenameClause(df=$df, columns=$columns)"
}

/**
 * ## Rename to camelCase
 *
 * This function renames all columns to `camelCase` by replacing all [delimiters][DELIMITERS_REGEX]
 * and converting the first char to lowercase.
 * Even [DataFrames][DataFrame] inside [FrameColumns][FrameColumn] are traversed recursively.
 */
public fun <T> DataFrame<T>.renameToCamelCase(): DataFrame<T> =
    // recursively rename all columns written with delimiters or starting with a capital to camel case
    rename {
        colsAtAnyDepth { it.name() matches DELIMITED_STRING_REGEX || it.name[0].isUpperCase() }
    }.toCamelCase()
        // take all frame columns at any depth and call renameToCamelCase() on all dataframes inside
        .update {
            colsAtAnyDepth().colsOf<AnyFrame>()
        }.with { it.renameToCamelCase() }

@AccessApiOverload
public fun <T, C> RenameClause<T, C>.into(vararg newColumns: ColumnReference<*>): DataFrame<T> =
    into(*newColumns.map { it.name() }.toTypedArray())

@Refine
@Interpretable("RenameInto")
public fun <T, C> RenameClause<T, C>.into(vararg newNames: String): DataFrame<T> = renameImpl(newNames)

@AccessApiOverload
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
public fun <T, C> RenameClause<T, C>.toCamelCase(): DataFrame<T> =
    into {
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
    public infix fun <C> ColumnReference<C>.named(newName: String): ColumnReference<C> = renamedReference(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
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
    @AccessApiOverload
    public infix fun String.named(nameOf: ColumnReference<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
    public infix fun String.named(nameOf: KProperty<*>): ColumnReference<*> =
        toColumnAccessor().named(nameOf.columnName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(newName: String): ColumnReference<C> = toColumnAccessor().named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @AccessApiOverload
    public infix fun <C> KProperty<C>.named(nameOf: ColumnReference<*>): ColumnReference<C> =
        toColumnAccessor().named(nameOf.name)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.NamedFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
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
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.ColumnReferenceReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
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
    @AccessApiOverload
    public infix fun String.into(nameOf: ColumnReference<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.StringReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
    public infix fun String.into(nameOf: KProperty<*>): ColumnReference<*> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.StringParam]
     */
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(newName: String): ColumnReference<C> = named(newName)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.ColumnReferenceParam]
     */
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: ColumnReference<*>): ColumnReference<C> = named(nameOf)

    /**
     * @include [CommonRenameDocs]
     * @include [CommonRenameDocs.IntoFunctionName]
     * @include [CommonRenameDocs.KPropertyReceiver]
     * @include [CommonRenameDocs.KPropertyParam]
     */
    @AccessApiOverload
    public infix fun <C> KProperty<C>.into(nameOf: KProperty<*>): ColumnReference<C> = named(nameOf)

    // endregion
}

// endregion
