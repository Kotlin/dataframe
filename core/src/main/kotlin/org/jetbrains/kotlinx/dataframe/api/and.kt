package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * See [Usage]
 */
public interface AndColumnsSelectionDsl<out T> {

    /**
     * ## And Operator Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnOrColumnSetDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [UsageTemplate.ColumnOrColumnSetRef]} {@include [InfixName]}` [ `**`{`**` ] `{@include [UsageTemplate.ColumnOrColumnSetRef]}` [ `**`\\}`**` ] `
     *
     *  `| `{@include [UsageTemplate.ColumnOrColumnSetRef]}{@include [Name]} **`(`**`|`**`{ `**{@include [UsageTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [Name]} **`(`**`|`**`{ `**{@include [UsageTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [Name]} **`(`**`|`**`{ `**{@include [UsageTemplate.ColumnOrColumnSetRef]}**` \\}`**`|`**`)`**
     * }
     */
    public interface Usage {

        /** [**and**][ColumnsSelectionDsl.and] */
        public interface InfixName

        /** .[**and**][ColumnsSelectionDsl.and] */
        public interface Name
    }

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any {@include [AccessApiLink]}!
     *
     * Check out the [Usage] for how to use [and].
     *
     * ### Examples:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { "colA" `[and][String.and]` colB }`
     *
     * `df.`[select][DataFrame.select]` {`
     *
     * {@include [Indent]}[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * {@include [DoubleIndent]}[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]` { "price" `[in][String.contains]` it.`[name][DataColumn.name]` }`
     *
     * {@include [Indent]}`}`
     *
     * `}`
     *
     * `df.`[select][DataFrame.select]` { "colC" `[and][String.and]` Type::colB `[and][KProperty.and]` "pathTo"["colC"] `[and][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     * {@getArg [CommonAndDocs.ExampleArg]}
     *
     * @return A [ColumnSet] that contains all the columns from the [ColumnsResolvers][ColumnsResolver] on the left
     *   and right side of the [and] operator.
     */
    private interface CommonAndDocs {

        interface ExampleArg
    }

    // region ColumnsResolver

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { ... } `[and][ColumnsResolver.and] {@getArg [ColumnsResolverAndDocs.Argument]}` }`
     */
    private interface ColumnsResolverAndDocs {

        interface Argument
    }

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnsList(this, other)

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] "colB"} */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] Type::colB} */
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    /** @include [ColumnsResolverAndDocs] {@setArg [ColumnsResolverAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this and other()

    // endregion

    // region String

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "colA" `[and][String.and] {@getArg [StringAndDocs.Argument]}` }`
     */
    private interface StringAndDocs {

        interface Argument
    }

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] "colB"} */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] Type::colB} */
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    /** @include [StringAndDocs] {@setArg [StringAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> String.and(other: ColumnsSelector<T, C>): ColumnSet<*> = toColumnAccessor() and other()

    // endregion

    // region KProperty

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::colA `[and][KProperty.and] {@getArg [KPropertyAndDocs.Argument]}` }`
     */
    private interface KPropertyAndDocs {

        interface Argument
    }

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] "colB"} */
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] Type::colB} */
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    /** @include [KPropertyAndDocs] {@setArg [KPropertyAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> KProperty<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = toColumnAccessor() and other()

    // endregion

    // region ColumnsSelector

    /**
     * @include [CommonAndDocs]
     * @setArg [CommonAndDocs.ExampleArg]
     *
     * `val intCols: `[ColumnsSelector][ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { intCols `[and][and] {@getArg [ColumnsSelectorAndDocs.Argument]}` }`
     */
    private interface ColumnsSelectorAndDocs {

        interface Argument
    }

    /** @include [ColumnsSelectorAndDocs] {@setArg [ColumnsSelectorAndDocs.Argument] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsResolver<C>): ColumnSet<C> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@setArg [ColumnsSelectorAndDocs.Argument] "colB"} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: String): ColumnSet<*> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@setArg [ColumnsSelectorAndDocs.Argument] Type::colB} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: KProperty<C>): ColumnSet<C> = this() and other

    /** @include [ColumnsSelectorAndDocs] {@setArg [ColumnsSelectorAndDocs.Argument] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" } `} */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this() and other

    // endregion

    /**
     * Invokes the given [ColumnsSelector] using this [ColumnsSelectionDsl].
     */
    private operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        with(this@AndColumnsSelectionDsl as ColumnsSelectionDsl<T>) { this@invoke() }
}

// endregion
