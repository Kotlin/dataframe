package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.impl.columns.getColumn
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/** [<code>Column Selection DSL</code>][ColumnSelectionDsl] */
internal typealias ColumnSelectionDslLink = Nothing

public interface ColumnSelectionDsl<out T> : ColumnsContainer<T> {

    /**
     * Retrieves the value of this [<code>ColumnReference</code>][ColumnReference] or [<code>-Accessor</code>][ColumnAccessor] from
     * the [<code>DataFrame</code>][DataFrame].
     *
     * This is a shorthand for [<code>get</code>][ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonColumnReferenceInvokeDocs = Nothing

    /**
     * Retrieves the value of this [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [<code>-Accessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>DataColumn</code>][DataColumn] this [<code>Column Reference</code>][ColumnReference] or [<code>-Accessor</code>][ColumnAccessor] points to.
     */
    public operator fun <C> ColumnReference<C>.invoke(): DataColumn<C> = get(this)

    /**
     * Retrieves the value of this [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [<code>-Accessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>ColumnGroup</code>][ColumnGroup] this [<code>Column Reference</code>][ColumnReference] or [<code>-Accessor</code>][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataRow<T>>.invoke(): ColumnGroup<T> = get(this)

    /**
     * Retrieves the value of this [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] or [<code>-Accessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] from
     * the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>FrameColumn</code>][FrameColumn] this [<code>Column Reference</code>][ColumnReference] or [<code>-Accessor</code>][ColumnAccessor] points to.
     */
    public operator fun <T> ColumnReference<DataFrame<T>>.invoke(): FrameColumn<T> = get(this)

    /**
     * Retrieves the value of this [<code>ColumnPath</code>][ColumnPath] from the [<code>DataFrame</code>][DataFrame].
     * This is a shorthand for [<code>getColumn</code>][ColumnsContainer.getColumn]`(myColumnPath)` and
     * is most often used in combination with `operator fun String.get(column: String)`,
     * for instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     * ```
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>DataColumn</code>][DataColumn] this [<code>ColumnPath</code>][ColumnPath] points to.
     */
    @Interpretable("ColumnPathInvokeTyped")
    public operator fun <C> ColumnPath.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][DataFrame].
     *
     * This is a shorthand for [<code>get</code>][ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonKPropertyInvokeDocs = Nothing

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>DataColumn</code>][DataColumn] this [<code>KProperty Accessor</code>][KProperty] points to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<T>.invoke(): DataColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>ColumnGroup</code>][ColumnGroup] this [<code>KProperty Accessor</code>][KProperty] points to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<DataRow<T>>.invoke(): ColumnGroup<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumn)`.
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>FrameColumn</code>][FrameColumn] this [<code>KProperty Accessor</code>][KProperty] points to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T> KProperty<DataFrame<T>>.invoke(): FrameColumn<T> = this@ColumnSelectionDsl[this]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][asColumnGroup]`().`[<code>get</code>][ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     */
    private typealias CommonKPropertyGetDocs = Nothing

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>DataColumn</code>][DataColumn] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<R>): DataColumn<R> = invoke()[column]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>ColumnGroup</code>][ColumnGroup] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<DataRow<R>>): ColumnGroup<R> =
        invoke()[column]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>FrameColumn</code>][FrameColumn] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowGet")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<DataRow<T>>.get(column: KProperty<DataFrame<R>>): FrameColumn<R> =
        invoke()[column]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>DataColumn</code>][DataColumn] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<R>): DataColumn<R> = invoke().asColumnGroup()[column]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>ColumnGroup</code>][ColumnGroup] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<DataRow<R>>): ColumnGroup<R> =
        invoke().asColumnGroup()[column]

    /**
     * Retrieves the value of this [<code>KProperty Accessor</code>][KProperty] from the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * This is a shorthand for
     *
     * [<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyType::myColumnGroup).`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.api.asColumnGroup]`().`[<code>get</code>][org.jetbrains.kotlinx.dataframe.ColumnsContainer.get]`(MyOtherType::myOtherColumn)`
     *
     * and can instead be written as
     *
     * `MyType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.get]`MyOtherType::myOtherColumn`[<code>`]`</code>][kotlin.reflect.KProperty.get].
     *
     * @throws [IllegalArgumentException] if the column is not found.
     * @return The [<code>FrameColumn</code>][FrameColumn] these [<code>KProperty Accessors</code>][KProperty] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <T, R> KProperty<T>.get(column: KProperty<DataFrame<R>>): FrameColumn<R> =
        invoke().asColumnGroup()[column]

    /**
     * Retrieves the value of the column with this name from the [<code>DataFrame</code>][DataFrame]. This can be
     * both typed and untyped.
     * This is a shorthand for [<code>get</code>][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"<MyColumnType>()` instead.
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     *
     * @throws [IllegalArgumentException] if there is no column with this name.
     * @return The [<code>DataColumn</code>][DataColumn] with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeTyped")
    @Interpretable("StringInvokeTyped")
    public operator fun <C> String.invoke(): DataColumn<C> = getColumn(this).cast()

    /**
     * Retrieves the value of the column with this name from the [<code>DataFrame</code>][DataFrame]. This can be
     * both typed and untyped.
     * This is a shorthand for [<code>get</code>][ColumnsContainer.get]`("myColumnName")` and can be
     * written as `"myColumnName"()` instead.
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     *
     * @throws [IllegalArgumentException] if there is no column with this name.
     * @return The [<code>DataColumn</code>][DataColumn] with this name.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("stringInvokeUntyped")
    @Interpretable("StringInvokeUntyped")
    public operator fun String.invoke(): DataColumn<*> = getColumn(this)

    /**
     * Creates a [<code>ColumnPath</code>][ColumnPath] from the receiver and the given column name [<code>column</code>][column].
     * This is a shorthand for [<code>pathOf</code>][pathOf]`("myColumnName", "myNestedColumnName")` and is often used
     * in combination with [<code>ColumnPath.invoke</code>][ColumnPath.invoke] to retrieve the value of a nested column.
     * For instance:
     * ```kotlin
     * "myColumn"["myNestedColumn"]<NestedColumnType>()
     *
     * "myColumn"["myNestedColumn"]["myDoublyNestedColumn"]<NestedColumnType>()
     * ```
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     */
    @Interpretable("StringGetColumn")
    public operator fun String.get(column: String): ColumnPath = pathOf(this, column)

    /**
     * As extension to `"myColumn"["myNestedColumn"]`, this function enables
     * `"myColumn"["myNestedColumn"]["myDoublyNestedColumn"]` as alternative to
     * [<code>pathOf</code>][pathOf]`("myColumn", "myNestedColumn", "myDoublyNestedColumn")`
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     */
    @Interpretable("ColumnPathGetColumn")
    public operator fun ColumnPath.get(column: String): ColumnPath = this + column
}
