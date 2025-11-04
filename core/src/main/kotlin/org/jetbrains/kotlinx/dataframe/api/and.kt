package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## And {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface AndColumnsSelectionDsl {

    /**
     * ## And Operator Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnOrColumnSetDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [DslGrammarTemplate.ColumnOrColumnSetRef]}` `{@include [InfixName]}`  [  `**`{`**`  ]  `{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}`  [  `**`\}`**` ]`
     *
     *  `| `{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}{@include [Name]}**` (`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [Name]}**` (`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [Name]}**` (`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     * }
     */
    public interface Grammar {

        /** [**`and`**][ColumnsSelectionDsl.and] */
        public typealias InfixName = Nothing

        /** __`.`__[**`and`**][ColumnsSelectionDsl.and] */
        public typealias Name = Nothing
    }

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any {@include [AccessApiLink]}!
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][DataFrame.groupBy]`  { "colA"  `[`and`][String.and]` colB }`
     *
     * `df.`[`select`][DataFrame.select]` {`
     *
     * {@include [Indent]}[`colsOf`][SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][ColumnSet.and]` {`
     *
     * {@include [DoubleIndent]}[`colsAtAnyDepth`][ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][DataColumn.name]` }`
     *
     * {@include [Indent]}`}`
     *
     * `}`
     *
     * `df.`[`select`][DataFrame.select]`  { "colC"  `[`and`][String.and]`  Type::colB  `[`and`][KProperty.and]`  "pathTo"["colC"]  `[`and`][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * {@get [CommonAndDocs.EXAMPLE]}
     *
     * @return A [ColumnSet] that contains all the columns from the [ColumnsResolvers][ColumnsResolver] on the left
     *   and right side of the [and] operator.
     */
    private interface CommonAndDocs {

        typealias EXAMPLE = Nothing
    }

    // region ColumnsResolver

    /**
     * @include [CommonAndDocs]
     * @set [CommonAndDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`  { ... }  `[`and`][ColumnsResolver.and]` `<code>{@get [ColumnsResolverAndDocs.Argument]}</code>` }`
     */
    private interface ColumnsResolverAndDocs {

        typealias Argument = Nothing
    }

    /** @include [ColumnsResolverAndDocs] {@set [ColumnsResolverAndDocs.Argument] [`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`} */
    @Interpretable("And0")
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnListImpl(this, other)

    /** @include [ColumnsResolverAndDocs] {@set [ColumnsResolverAndDocs.Argument] `{ colA `[`/`][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" \}`} */
    public infix fun <C> ColumnsResolver<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> = this and other()

    /** @include [ColumnsResolverAndDocs] {@set [ColumnsResolverAndDocs.Argument] `"colB"`} */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@set [ColumnsResolverAndDocs.Argument] `Type::colB`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    // endregion

    // region String

    /**
     * @include [CommonAndDocs]
     * @set [CommonAndDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  { "colA"  `[`and`][String.and]` `<code>{@get [StringAndDocs.Argument]}</code>` }`
     */
    private interface StringAndDocs {

        typealias Argument = Nothing
    }

    /** @include [StringAndDocs] {@set [StringAndDocs.Argument] [`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`} */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@set [StringAndDocs.Argument] `{ colA `[`/`][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" \}`} */
    public infix fun <C> String.and(other: () -> ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other()

    /** @include [StringAndDocs] {@set [StringAndDocs.Argument] `"colB"`} */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** @include [StringAndDocs] {@set [StringAndDocs.Argument] `Type::colB`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    // endregion

    // region KProperty

    /**
     * @include [CommonAndDocs]
     * @set [CommonAndDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  { Type::colA  `[`and`][KProperty.and]` `<code>{@get [KPropertyAndDocs.Argument]}</code>` }`
     */
    private interface KPropertyAndDocs {

        typealias Argument = Nothing
    }

    /** @include [KPropertyAndDocs] {@set [KPropertyAndDocs.Argument] [`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@set [KPropertyAndDocs.Argument] `{ colA `[/][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" \}`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> =
        toColumnAccessor() and other()

    /** @include [KPropertyAndDocs] {@set [KPropertyAndDocs.Argument] `"colB"`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@set [KPropertyAndDocs.Argument] `Type::colB`} */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    // endregion
}

// endregion
