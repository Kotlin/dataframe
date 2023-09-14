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
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface AndColumnsSelectionDsl<out T> {

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][DataFrame.groupBy]` { "colA" `[and][String.and]` colB }`
     *
     * `df.`[select][DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[and][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
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
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { ... } `[and][ColumnsResolver.and] {@getArg [ColumnsResolverAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsResolverAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnsList(this, other)

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { ... } `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this and other()

    // endregion

    // region String

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { "colA" `[and][String.and] {@getArg [StringAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface StringAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colA" `[and][kotlin.String.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsSelector<T, C>): ColumnSet<*> = toColumnAccessor() and other()

    // endregion

    // region KProperty

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::colA `[and][KProperty.and] {@getArg [KPropertyAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface KPropertyAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::colA `[and][kotlin.reflect.KProperty.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> KProperty<C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = toColumnAccessor() and other()

    // endregion

    // region ColumnsSelector

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { intCols `[and][and] {@getArg [ColumnsSelectorAndDocs.Argument]}` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsSelectorAndDocs {

        interface Argument
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] [colsOf][SingleColumn.colsOf]`<`[Int][Int]`>()` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsResolver<C>): ColumnSet<C> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] "colB"` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: String): ColumnSet<*> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] Type::colB` }` 
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: KProperty<C>): ColumnSet<C> = this() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Examples:
     *
     * `df.`[groupBy][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]` { "colA" `[and][kotlin.String.and]` colB }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[String][String]`>() `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "price" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColsAtAnyDepthColumnsSelectionDsl.atAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "colC" `[and][kotlin.String.and]` Type::colB `[and][kotlin.reflect.KProperty.and]` "pathTo"["colC"] `[and][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `val intCols: `[ColumnsSelector][org.jetbrains.kotlinx.dataframe.ColumnsSelector]`<*, `[Int][Int]`> = { `[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { intCols `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] `{ colA `[/][DataColumn.div]` 2.0 `[named][ColumnReference.named]` "half colA" ` }`  `}
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsSelector<T, C>.and(other: ColumnsSelector<T, C>): ColumnSet<C> = this() and other

    // endregion

    /**
     * Invokes the given [ColumnsSelector] using this [ColumnsSelectionDsl].
     */
    private operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        with(this@AndColumnsSelectionDsl as ColumnsSelectionDsl<T>) { this@invoke() }
}
// endregion
